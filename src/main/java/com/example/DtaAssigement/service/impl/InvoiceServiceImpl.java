package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.ennum.PaymentMethod;
import com.example.DtaAssigement.entity.Invoice;
import com.example.DtaAssigement.entity.Order;
import com.example.DtaAssigement.ennum.OrderStatus;
import com.example.DtaAssigement.entity.User;
import com.example.DtaAssigement.repository.InvoiceRepository;
import com.example.DtaAssigement.repository.OrderRepository;
import com.example.DtaAssigement.repository.UserRepository;
import com.example.DtaAssigement.service.InvoiceService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final OrderRepository orderRepo;
    private final InvoiceRepository invoiceRepo;
    private final UserRepository userRepo;



    @Override
    public Invoice createInvoice(Long orderId, Long cashierId, PaymentMethod paymentMethod) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
        if (order.getStatus() != OrderStatus.SERVED) {
            throw new IllegalStateException("Only served orders can be paid");
        }

        double total = order.getOrderItems().stream()
                .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();

        User cashier = userRepo.findById(cashierId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + cashierId));

        Invoice invoice = Invoice.builder()
                .order(order)
                .cashier(cashier)
                .paymentTime(LocalDateTime.now())
                .totalAmount(total)
                .paymentMethod(paymentMethod)
                .build();

        order.setStatus(OrderStatus.PAID);
        order.getTable().setAvailable(true);
        orderRepo.save(order);
        return invoiceRepo.save(invoice);
    }


        public boolean deleteInvoice(Long id){
        if(!invoiceRepo.existsById(id)){return false;}
            invoiceRepo.deleteById(id);
            return true;
        }

        public Optional<Invoice> getInvoiceById(Long id){
        return invoiceRepo.findById(id);
        }
        public Invoice findById(Long id){
        return invoiceRepo.findById(id).orElseThrow(()->new NoSuchElementException("Invoice not found: "+id));
        }

        public List<Invoice> getAllInvoice(){
        return invoiceRepo.findAll();
        }


}
