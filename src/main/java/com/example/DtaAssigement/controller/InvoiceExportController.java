package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.entity.Invoice;
import com.example.DtaAssigement.invoidGenerateWordExcelQrCode.ExcelGenerator;
import com.example.DtaAssigement.invoidGenerateWordExcelQrCode.WordGenerator;
import com.example.DtaAssigement.service.InvoiceService;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/invoices")
@AllArgsConstructor
public class InvoiceExportController {

    private final InvoiceService invoiceService;

    @GetMapping("/{invoiceId}/export/excel")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<byte[]> exportToExcel(@PathVariable Long invoiceId) throws IOException {
        Invoice invoice = invoiceService.findById(invoiceId);
        if (invoice == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] bytes = ExcelGenerator.generateInvoiceExcel(invoice);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + invoiceId + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @GetMapping("/{invoiceId}/export/word")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<byte[]> exportToWord(@PathVariable Long invoiceId) throws IOException {
        Invoice invoice = invoiceService.findById(invoiceId);
        byte[] bytes = WordGenerator.generateInvoiceWord(invoice);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + invoiceId + ".docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

}
