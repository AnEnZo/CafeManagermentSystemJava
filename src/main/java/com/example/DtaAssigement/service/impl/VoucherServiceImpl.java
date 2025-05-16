package com.example.DtaAssigement.service.impl;


import com.example.DtaAssigement.entity.Voucher;
import com.example.DtaAssigement.repository.VoucherRepository;
import com.example.DtaAssigement.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Override
    @Cacheable(value = "vouchers", key = "'all'")
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    @Override
    @Cacheable(value = "vouchers", key = "#id")
    public Optional<Voucher> getVoucherById(Long id) {
        return voucherRepository.findById(id);
    }

    @Override
    @CachePut(value = "vouchers", key = "#voucher.id")
    public Voucher createVoucher(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    @Override
    @CachePut(value = "vouchers", key = "#id")
    @CacheEvict(value = "vouchers", key = "#id")
    public Voucher updateVoucher(Long id, Voucher updatedVoucher) {
        return voucherRepository.findById(id)
                .map(voucher -> {
                    voucher.setCode(updatedVoucher.getCode());
                    voucher.setType(updatedVoucher.getType());
                    voucher.setDiscountValue(updatedVoucher.getDiscountValue());
                    voucher.setMinOrderAmount(updatedVoucher.getMinOrderAmount());
                    voucher.setActive(updatedVoucher.isActive());
                    return voucherRepository.save(voucher);
                })
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
    }

    @Override
    @CacheEvict(value = "vouchers", key = "#id")
    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }
}

