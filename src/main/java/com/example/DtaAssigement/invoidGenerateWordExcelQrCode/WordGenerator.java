package com.example.DtaAssigement.invoidGenerateWordExcelQrCode;

import com.example.DtaAssigement.entity.Branch;
import com.example.DtaAssigement.entity.Invoice;
import com.example.DtaAssigement.entity.OrderItem;
import com.example.DtaAssigement.ennum.PaymentMethod;
import com.example.DtaAssigement.entity.SalaryRecord;
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

    public static byte[] generateInvoiceWord(Invoice invoice) throws IOException,InvalidFormatException {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            DecimalFormat currencyFormat = new DecimalFormat("###,###");

            Branch branch = invoice.getCashier().getBranch();

            // Company Header
            XWPFParagraph header = doc.createParagraph();
            header.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runHeader = header.createRun();
            runHeader.setBold(true);
            runHeader.setFontSize(14);
            runHeader.setText(branch.getName());
            runHeader.addBreak();
            runHeader.setText(branch.getAddress() != null ? branch.getAddress() : "");
            runHeader.addBreak();
            runHeader.setText("Phone: " + (branch.getPhone() != null ? branch.getPhone() : ""));


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
            // Spacing
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

            // Header Row
            XWPFTableRow headerRow = table.getRow(0);
            createHeaderCell(headerRow.getCell(0), "Tên món");
            createHeaderCell(headerRow.addNewTableCell(), "Số lượng");
            createHeaderCell(headerRow.addNewTableCell(), "Đơn giá");
            createHeaderCell(headerRow.addNewTableCell(), "Thành tiền");

            // Data Rows
            for (OrderItem item : items) {
                XWPFTableRow row = table.createRow();
                row.getCell(0).setText(item.getMenuItem().getName());
                row.getCell(1).setText(String.valueOf(item.getQuantity()));
                row.getCell(2).setText(currencyFormat.format(item.getMenuItem().getPrice()));
                double lineTotal = item.getQuantity() * item.getMenuItem().getPrice().doubleValue();
                row.getCell(3).setText(currencyFormat.format(lineTotal));
            }

            // Summary (Original, Discount, Total)
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

            // Nếu phương thức thanh toán là chuyển khoản, thêm QR code
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

    // Helper to set column widths
    private static void setTableColumnWidths(XWPFTable table, int[] widths) {
        CTTbl ttbl = table.getCTTbl();
        CTTblGrid grid = ttbl.getTblGrid() != null ? ttbl.getTblGrid() : ttbl.addNewTblGrid();
        grid.setNil();
        for (int w : widths) {
            CTTblGridCol gridCol = grid.addNewGridCol();
            gridCol.setW(BigInteger.valueOf(w));
        }
    }

    // Helper to style header cells
    private static void createHeaderCell(XWPFTableCell cell, String text) {
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r = p.createRun();
        r.setBold(true);
        r.setText(text);
    }

    // Helper to fill info cells
    private static void fillInfoCell(XWPFTableCell cell, String text) {
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setSpacingAfter(0);
        XWPFRun r = p.createRun();
        r.setFontSize(12);
        r.setText(text);
    }

    public static void exportSalaryRecords(List<SalaryRecord> records, OutputStream os) throws Exception {
        try (XWPFDocument doc = new XWPFDocument()) {
            // Tiêu đề
            XWPFParagraph title = doc.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = title.createRun();
            run.setText("Bảng lương nhân viên");
            run.setBold(true);
            run.setFontSize(16);

            // Tạo bảng: header + n dòng data, 7 cột
            XWPFTable table = doc.createTable(records.size() + 1, 7);

            // Header
            XWPFTableRow header = table.getRow(0);
            header.getCell(0).setText("Tên nhân viên");
            header.getCell(1).setText("Tháng");
            header.getCell(2).setText("Lương gộp");
            header.getCell(3).setText("Ngày nghỉ");
            header.getCell(4).setText("Số lần đi muộn");
            header.getCell(5).setText("Số lần tăng ca");
            header.getCell(6).setText("Lương thực nhận");

            // Data
            for (int i = 0; i < records.size(); i++) {
                SalaryRecord r = records.get(i);
                XWPFTableRow row = table.getRow(i + 1);

                // Lấy tên nhân viên từ SalaryRecord → Employee
                String employeeName = r.getEmployee().getFullName(); // hoặc getName(), tuỳ tên getter
                row.getCell(0).setText(employeeName);
                row.getCell(1).setText(r.getMonth().toString());
                row.getCell(2).setText(r.getGrossSalary().toString());
                row.getCell(3).setText(String.valueOf(r.getDaysOff()));
                row.getCell(4).setText(String.valueOf(r.getLateCount()));
                row.getCell(5).setText(String.valueOf(r.getOvertimeCount()));
                row.getCell(6).setText(r.getNetSalary().toString());
            }

            // Ghi ra OutputStream
            doc.write(os);
        }
    }





}
