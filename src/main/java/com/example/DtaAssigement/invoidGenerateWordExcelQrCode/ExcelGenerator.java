package com.example.DtaAssigement.invoidGenerateWordExcelQrCode;


import com.example.DtaAssigement.entity.Invoice;
import com.example.DtaAssigement.entity.OrderItem;
import com.example.DtaAssigement.entity.Voucher;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

    public static byte[] generateInvoiceExcel(Invoice invoice) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CreationHelper createHelper = workbook.getCreationHelper();

            // Styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));

            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));

            // ===== SHEET 1: INVOICE =====
            Sheet sheet = workbook.createSheet("Invoice");

            String[] headers = {
                    "Mã hóa đơn",      // Invoice ID
                    "Mã đơn hàng",     // Order ID
                    "Thời gian thanh toán",
                    "Nhân viên thu ngân",
                    "Phương thức thanh toán",
                    "Voucher",
                    "Tiền gốc",
                    "Giảm giá",
                    "Tổng tiền"
            };
            // Header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Data row
            Row dataRow = sheet.createRow(1);
            int c = 0;
            dataRow.createCell(c++).setCellValue(invoice.getId());
            dataRow.createCell(c++).setCellValue(invoice.getOrder().getId());

            Cell paymentTimeCell = dataRow.createCell(c++);
            paymentTimeCell.setCellValue(invoice.getPaymentTime());
            paymentTimeCell.setCellStyle(dateStyle);

            dataRow.createCell(c++).setCellValue(invoice.getCashier().getUsername());
            dataRow.createCell(c++).setCellValue(invoice.getPaymentMethod().name());

            // Voucher (if any)
            Voucher voucher = invoice.getVoucher();
            if (voucher != null) {
                // Assuming Voucher has a 'code' field; adjust if different
                dataRow.createCell(c++).setCellValue(voucher.getCode());
            } else {
                dataRow.createCell(c++).setCellValue("");
            }

            // Original amount
            Cell origCell = dataRow.createCell(c++);
            origCell.setCellValue(invoice.getOriginalAmount().doubleValue());
            origCell.setCellStyle(currencyStyle);

            // Discount amount
            Cell discountCell = dataRow.createCell(c++);
            discountCell.setCellValue(invoice.getDiscountAmount().doubleValue());
            discountCell.setCellStyle(currencyStyle);

            // Total amount
            Cell totalCell = dataRow.createCell(c++);
            totalCell.setCellValue(invoice.getTotalAmount().doubleValue());
            totalCell.setCellStyle(currencyStyle);

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // ===== SHEET 2: DETAILS =====
            Sheet detailSheet = workbook.createSheet("Details");
            String[] detailHeaders = {
                    "Tên món",
                    "Danh mục",
                    "Đơn giá",
                    "Số lượng",
                    "Thành tiền"
            };
            Row dh = detailSheet.createRow(0);
            for (int i = 0; i < detailHeaders.length; i++) {
                Cell cell = dh.createCell(i);
                cell.setCellValue(detailHeaders[i]);
                cell.setCellStyle(headerStyle);
                detailSheet.autoSizeColumn(i);
            }

            List<OrderItem> items = invoice.getOrder().getOrderItems();
            for (int i = 0; i < items.size(); i++) {
                OrderItem item = items.get(i);
                Row row = detailSheet.createRow(i + 1);
                row.createCell(0).setCellValue(item.getMenuItem().getName());
                row.createCell(1).setCellValue(item.getMenuItem().getCategory().getName());

                Cell priceCell = row.createCell(2);
                priceCell.setCellValue(item.getMenuItem().getPrice().doubleValue());
                priceCell.setCellStyle(currencyStyle);

                row.createCell(3).setCellValue(item.getQuantity());

                Cell lineTotal = row.createCell(4);
                lineTotal.setCellValue(item.getMenuItem().getPrice().doubleValue() * item.getQuantity());
                lineTotal.setCellStyle(currencyStyle);

                for (int j = 0; j < detailHeaders.length; j++) {
                    detailSheet.autoSizeColumn(j);
                }
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
