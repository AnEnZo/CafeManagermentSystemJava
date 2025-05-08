package com.example.DtaAssigement.invoidGenerateWordExcelQrCode;

import com.example.DtaAssigement.entity.Invoice;
import com.example.DtaAssigement.entity.OrderItem;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class WordGenerator {

    public static byte[] generateInvoiceWord(Invoice invoice) throws IOException {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            DecimalFormat currencyFormat = new DecimalFormat("###,###");
            // Tiêu đề
            XWPFParagraph title = doc.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runTitle = title.createRun();
            runTitle.setBold(true);
            runTitle.setFontSize(16);
            runTitle.setText("HÓA ĐƠN THANH TOÁN");

            // Thông tin chung
            XWPFParagraph info = doc.createParagraph();
            XWPFRun runInfo = info.createRun();
            runInfo.setFontSize(12);
            runInfo.addBreak();
            runInfo.setText("Mã HD: " + invoice.getId());
            runInfo.addBreak();
            runInfo.setText("Thời gian: " + invoice.getPaymentTime());
            runInfo.addBreak();
            runInfo.setText("Thu ngân: " + invoice.getCashier().getUsername());
            runInfo.addBreak();
            runInfo.setText("Phương thức thanh toán: " + invoice.getPaymentMethod());
            runInfo.addBreak();

            // Bảng chi tiết các món
            List<OrderItem> items = invoice.getOrder().getOrderItems();
            if (items != null && !items.isEmpty()) {
                XWPFParagraph tableTitle = doc.createParagraph();
                tableTitle.setSpacingBefore(200);
                XWPFRun runTableTitle = tableTitle.createRun();
                runTableTitle.setBold(true);
                runTableTitle.setFontSize(14);
                runTableTitle.setText("Chi tiết hóa đơn:");

                XWPFTable table = doc.createTable();
                // Thiết lập độ rộng table full page
                table.setWidth("100%");

                // Header
                XWPFTableRow header = table.getRow(0);
                header.getCell(0).setText("Tên món");
                header.addNewTableCell().setText("Số lượng");
                header.addNewTableCell().setText("Đơn giá");
                header.addNewTableCell().setText("Thành tiền");

                // Rows
                for (OrderItem item : items) {
                    XWPFTableRow row = table.createRow();
                    String name = item.getMenuItem().getName();
                    int qty = item.getQuantity();
                    double price = item.getMenuItem().getPrice();
                    double lineTotal = qty * price;

                    row.getCell(0).setText(name);
                    row.getCell(1).setText(String.valueOf(qty));
                    row.getCell(2).setText(currencyFormat.format(price)+" VNĐ.");
                    row.getCell(3).setText(currencyFormat.format(lineTotal)+" VNĐ.");
                }
            }

            // Tổng cộng
            XWPFParagraph total = doc.createParagraph();
            total.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun runTotal = total.createRun();
            runTotal.setBold(true);
            runTotal.setFontSize(12);
            runTotal.setText("Tổng tiền: " + currencyFormat.format(invoice.getTotalAmount())+" VNĐ.");

            doc.write(out);
            return out.toByteArray();
        }
    }
}
