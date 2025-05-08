package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.ennum.OrderStatus;
import com.example.DtaAssigement.ennum.PaymentMethod;
import com.example.DtaAssigement.entity.Invoice;
import com.example.DtaAssigement.entity.Order;
import com.example.DtaAssigement.repository.OrderRepository;
import com.example.DtaAssigement.service.InvoiceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/invoices")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final OrderRepository orderRepo;

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN','USER')")
    public List<Invoice> getAllInvoice(){
        return invoiceService.getAllInvoice();
    }

    @GetMapping("/{invoiceId}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN','USER')")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long invoiceId){
           return invoiceService.getInvoiceById(invoiceId)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
    }

    // Tạo hóa đơn cho đơn hàng đã phục vụ
    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<Invoice> createInvoice(@RequestParam Long orderId,
                                                 @RequestParam Long cashierId,
                                                 @RequestParam PaymentMethod paymentMethod){
        return ResponseEntity.ok(invoiceService.createInvoice(orderId, cashierId, paymentMethod));
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> deleteInvoice(@RequestParam Long invoiceId){
        boolean deleted = invoiceService.deleteInvoice(invoiceId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @GetMapping(value = "/{orderId}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<?> getQRCode(@PathVariable Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));

        double total = order.getOrderItems().stream()
                .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();

        String accountNumber = "0564870803";
        String accountName = "DINH TUAN AN";
        String acqId         = "970422"; // Mã ngân hàng MB Bank
        String addInfo       = "Thanh toan don hang #" + orderId;

        try {
            // Gọi VietQR API
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String,Object> body = new HashMap<>();
            body.put("accountNo",   accountNumber);
            body.put("accountName", accountName);
            body.put("acqId",       acqId);
            body.put("amount",      total);
            body.put("addInfo",     addInfo);
            body.put("template",    "print");

            HttpEntity<Map<String,Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.vietqr.io/v2/generate",
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String,Object> data = (Map<String,Object>) response.getBody().get("data");
                String qrDataURL = (String) data.get("qrDataURL");
                // qrDataURL có dạng "data:image/png;base64,AAAAB3NzaC1yc2E..."

                // Tách và decode phần Base64
                String base64 = qrDataURL.substring(qrDataURL.indexOf(',') + 1);
                byte[] imageBytes = java.util.Base64.getDecoder().decode(base64);

                // Đọc BufferedImage từ bytes
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                BufferedImage qrImage = ImageIO.read(bis);
                bis.close();

                // Ghi ra byte[] để trả về
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(qrImage, "PNG", baos);

                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(baos.toByteArray());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể tạo QR từ VietQR API");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi trong quá trình tạo QR Code: " + e.getMessage());
        }
    }



}
