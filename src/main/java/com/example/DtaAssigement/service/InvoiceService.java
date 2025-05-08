package com.example.DtaAssigement.service;

import com.example.DtaAssigement.ennum.PaymentMethod;
import com.example.DtaAssigement.entity.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    Invoice createInvoice(Long orderId, Long cashierId, PaymentMethod paymentMethod);
    boolean deleteInvoice(Long id);
    Optional<Invoice> getInvoiceById(Long id);
    Invoice findById(Long id);
    List<Invoice> getAllInvoice();
}
