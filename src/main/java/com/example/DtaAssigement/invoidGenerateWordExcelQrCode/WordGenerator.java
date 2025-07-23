package com.example.DtaAssigement.invoidGenerateWordExcelQrCode;

import com.example.DtaAssigement.entity.Invoice;
import com.example.DtaAssigement.entity.OrderItem;
import com.example.DtaAssigement.ennum.PaymentMethod;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordGenerator {

    // Thông tin công ty (thiết lập tĩnh)
    private static final String COMPANY_NAME = "The Coffe House";
    private static final String COMPANY_ADDRESS = "394 Mỹ Đình";
    private static final String COMPANY_PHONE = "0564870803";

    public static byte[] generateInvoiceWord(Invoice invoice) throws IOException, InvalidFormatException {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            DecimalFormat currencyFormat = new DecimalFormat("###,###");

            // Company Header (sử dụng thông tin tĩnh)
            XWPFParagraph header = doc.createParagraph();
            header.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runHeader = header.createRun();
            runHeader.setBold(true);
            runHeader.setFontSize(14);
            runHeader.setText(COMPANY_NAME);
            runHeader.addBreak();
            runHeader.setText(COMPANY_ADDRESS);
            runHeader.addBreak();
            runHeader.setText("Phone: " + COMPANY_PHONE);

            // Invoice Title
            XWPFParagraph title = doc.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runTitle = title.createRun();
            runTitle.setBold(true);
            runTitle.setFontSize(18);
            runTitle.setText("HÓA ĐƠN THANH TOÁN");
            runTitle.addBreak();

            // General Info Table
            XWPFTable infoTable = doc.createTable(5, 2);
            infoTable.setWidth("100%");
            setTableColumnWidths(infoTable, new int[]{4000, 4000});

            fillInfoCell(infoTable.getRow(0).getCell(0), "Mã HD:");
            fillInfoCell(infoTable.getRow(0).getCell(1), invoice.getId().toString());
            fillInfoCell(infoTable.getRow(1).getCell(0), "Thời gian:");
            fillInfoCell(infoTable.getRow(1).getCell(1), invoice.getPaymentTime().toString());
            fillInfoCell(infoTable.getRow(2).getCell(0), "Thu ngân:");
            fillInfoCell(infoTable.getRow(2).getCell(1), invoice.getCashier().getUsername());
            fillInfoCell(infoTable.getRow(3).getCell(0), "Phương thức TT:");
            fillInfoCell(infoTable.getRow(3).getCell(1), invoice.getPaymentMethod().toString());
            fillInfoCell(infoTable.getRow(4).getCell(0), "Mã giảm giá:");
            fillInfoCell(infoTable.getRow(4).getCell(1),
                    invoice.getVoucher() != null ? invoice.getVoucher().getCode() : "Không có");

            doc.createParagraph().createRun().addBreak();

            // Details Table
            List<OrderItem> items = invoice.getOrder().getOrderItems();
            XWPFParagraph tableTitle = doc.createParagraph();
            tableTitle.setSpacingBefore(200);
            XWPFRun runTableTitle = tableTitle.createRun();
            runTableTitle.setBold(true);
            runTableTitle.setFontSize(14);
            runTableTitle.setText("Chi tiết hóa đơn");

            XWPFTable table = doc.createTable();
            table.setWidth("100%");
            setTableColumnWidths(table, new int[]{4000, 1000, 2000, 2000});

            XWPFTableRow headerRow = table.getRow(0);
            createHeaderCell(headerRow.getCell(0), "Tên món");
            createHeaderCell(headerRow.addNewTableCell(), "Số lượng");
            createHeaderCell(headerRow.addNewTableCell(), "Đơn giá");
            createHeaderCell(headerRow.addNewTableCell(), "Thành tiền");

            for (OrderItem item : items) {
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(item.getMenuItem().getName());
                row.getCell(1).setText(String.valueOf(item.getQuantity()));
                row.getCell(2).setText(currencyFormat.format(item.getMenuItem().getPrice()));
                double lineTotal = item.getQuantity() * item.getMenuItem().getPrice().doubleValue();
                row.getCell(3).setText(currencyFormat.format(lineTotal));
            }

            XWPFParagraph summary = doc.createParagraph();
            summary.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun runSummary = summary.createRun();
            runSummary.setFontSize(12);
            runSummary.setBold(true);
            runSummary.setText("Tiền gốc: " + currencyFormat.format(invoice.getOriginalAmount()) + " VNĐ");
            runSummary.addBreak();
            runSummary.setText("Giảm giá: " + currencyFormat.format(invoice.getDiscountAmount()) + " VNĐ");
            runSummary.addBreak();
            runSummary.setText("Tổng tiền: " + currencyFormat.format(invoice.getTotalAmount()) + " VNĐ");

            if (invoice.getPaymentMethod() == PaymentMethod.TRANSFER) {
                byte[] qrBytes = fetchQrCode(invoice);
                XWPFParagraph qrPara = doc.createParagraph();
                qrPara.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun qrRun = qrPara.createRun();
                try (InputStream qrStream = new ByteArrayInputStream(qrBytes)) {
                    qrRun.addPicture(qrStream,
                            Document.PICTURE_TYPE_PNG,
                            "qr.png",
                            Units.toEMU(150),
                            Units.toEMU(150));
                } catch (InvalidFormatException e) {
                    // log lỗi nhúng ảnh
                }
            }

            doc.write(out);
            return out.toByteArray();
        }
    }

    private static byte[] fetchQrCode(Invoice invoice) throws IOException {
        double amount = invoice.getTotalAmount().doubleValue();
        String accountNumber = "0564870803";
        String accountName = "DINH TUAN AN";
        String acqId = "970422";
        String addInfo = "Thanh toan don hang #" + invoice.getOrder().getId();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("accountNo", accountNumber);
        body.put("accountName", accountName);
        body.put("acqId", acqId);
        body.put("amount", amount);
        body.put("addInfo", addInfo);
        body.put("template", "print");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.vietqr.io/v2/generate",
                request,
                Map.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map data = (Map) response.getBody().get("data");
            String qrDataURL = (String) data.get("qrDataURL");
            String base64 = qrDataURL.substring(qrDataURL.indexOf(',') + 1);
            return java.util.Base64.getDecoder().decode(base64);
        }
        throw new IOException("Không thể tạo QR code từ API");
    }

    private static void setTableColumnWidths(XWPFTable table, int[] widths) {
        CTTbl ttbl = table.getCTTbl();
        CTTblGrid grid = ttbl.getTblGrid() != null ? ttbl.getTblGrid() : ttbl.addNewTblGrid();
        grid.setNil();
        for (int w : widths) {
            CTTblGridCol gridCol = grid.addNewGridCol();
            gridCol.setW(BigInteger.valueOf(w));
        }
    }

    private static void createHeaderCell(XWPFTableCell cell, String text) {
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r = p.createRun();
        r.setBold(true);
        r.setText(text);
    }

    private static void fillInfoCell(XWPFTableCell cell, String text) {
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setSpacingAfter(0);
        XWPFRun r = p.createRun();
        r.setFontSize(12);
        r.setText(text);
    }

}