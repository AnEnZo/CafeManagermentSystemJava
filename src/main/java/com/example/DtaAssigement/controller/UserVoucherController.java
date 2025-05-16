package com.example.DtaAssigement.controller;

import com.example.DtaAssigement.entity.UserVoucher;
import com.example.DtaAssigement.invoidGenerateWordExcelQrCode.QRCodeGenerator;
import com.example.DtaAssigement.service.UserVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-vouchers")
public class UserVoucherController {

    private final UserVoucherService userVoucherService;

    @GetMapping(value = "/{userVoucherId}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<?> getVoucherQRCode(@PathVariable Long userVoucherId) {
        try {
            // Lấy UserVoucher từ DB
            UserVoucher userVoucher = userVoucherService.getById(userVoucherId);

            if (userVoucher == null) {
                return ResponseEntity.notFound().build();
            }

            // Sinh QR code từ mã code của UserVoucher
            BufferedImage qrImage = QRCodeGenerator.generateQRCodeImage(userVoucher.getCode());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Lỗi khi tạo QR code: " + e.getMessage());
        }
    }


    @PostMapping("/exchange")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<UserVoucher> exchangeVoucher(
            @RequestParam String username,
            @RequestParam String voucherCode) {
        UserVoucher userVoucher = userVoucherService.exchangePointsForVoucher(username, voucherCode);
        return ResponseEntity.ok(userVoucher);
    }


}
