package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.entity.User;
import com.example.DtaAssigement.entity.UserVoucher;
import com.example.DtaAssigement.entity.Voucher;
import com.example.DtaAssigement.invoidGenerateWordExcelQrCode.QRCodeGenerator;
import com.example.DtaAssigement.repository.UserRepository;
import com.example.DtaAssigement.repository.UserVoucherRepository;
import com.example.DtaAssigement.repository.VoucherRepository;
import com.example.DtaAssigement.service.UserVoucherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserVoucherServiceImpl implements UserVoucherService {

    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;

    @Override
    public UserVoucher exchangePointsForVoucher(String username, String voucherCode) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Voucher voucher = voucherRepository.findByCode(voucherCode)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        if (!voucher.isActive()) {
            throw new RuntimeException("Voucher is not active");
        }

        if (user.getRewardPoints() < voucher.getRequiredPoints()) {
            throw new RuntimeException("Not enough reward points");
        }

        user.setRewardPoints(user.getRewardPoints() - voucher.getRequiredPoints());
        userRepository.save(user);

        String code;
        UserVoucher userVoucher=null;
        do {
            code = UUID.randomUUID().toString();
        } while (userVoucherRepository.existsByCode(code));

        try {
            BufferedImage qrImage = QRCodeGenerator.generateQRCodeImage(code);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            String base64Qr = Base64.getEncoder().encodeToString(baos.toByteArray());

            userVoucher = UserVoucher.builder()
                    .user(user)
                    .voucher(voucher)
                    .used(false)
                    .code(code)
                    .qrCode("data:image/png;base64," + base64Qr)
                    .expiryAt(LocalDateTime.now().plusDays(7))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }

        return userVoucherRepository.save(userVoucher);
    }

    @Override
    public UserVoucher getById(Long id) {
        return userVoucherRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserVoucher not found: " + id));
    }

}

