package com.example.DtaAssigement.service;

import com.example.DtaAssigement.dto.InvoiceCalculationDTO;
import com.example.DtaAssigement.ennum.PaymentMethod;
import com.example.DtaAssigement.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    Invoice createInvoice(Long orderId, String voucherCode, Long cashierId, PaymentMethod paymentMethod, String phoneNumber);
    boolean deleteInvoice(Long id);
    Optional<Invoice> getInvoiceById(Long id);
    Invoice findById(Long id);
    Page<Invoice> getAllInvoice(Pageable pageable);
    InvoiceCalculationDTO calculateInvoiceAmount(Long orderId, String voucherCode);
}
