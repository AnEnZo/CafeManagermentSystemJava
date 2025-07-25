package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.dto.InvoiceCalculationDTO;
import com.example.DtaAssigement.ennum.PaymentMethod;
import com.example.DtaAssigement.entity.*;
import com.example.DtaAssigement.ennum.OrderStatus;
import com.example.DtaAssigement.repository.*;
import com.example.DtaAssigement.service.InvoiceService;
import com.example.DtaAssigement.service.RevenueService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final InvoiceRepository invoiceRepo;
    private final UserRepository userRepo;
    private final VoucherRepository voucherRepo;
    private final UserVoucherRepository userVoucherRepo;
    private final RevenueService revenueService;

    @Override
    public Invoice createInvoice(Long orderId, String voucherCode, Long cashierId, PaymentMethod paymentMethod, String phoneNumber) {
        // Lấy đơn hàng và kiểm tra trạng thái
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
        if (order.getStatus() != OrderStatus.SERVED) {
            throw new IllegalStateException("Only served orders can be paid");
        }
        // Lấy thông tin nhân viên thu ngân
        User cashier = userRepo.findById(cashierId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + cashierId));


        // Tính tổng tiền gốc của các món
//        double originalAmount = order.getOrderItems().stream()
//                .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
//                .sum();
        BigDecimal originalAmount=orderItemRepo.calculateOrderTotalAmount(orderId);

        // Xử lý voucher nếu có
        UserVoucher userVoucher = null;
        Voucher voucher = null;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (voucherCode != null && !voucherCode.isEmpty()) {
            userVoucher = userVoucherRepo.findByCode(voucherCode)
                    .orElseThrow(() -> new NoSuchElementException("Invalid voucher code: " + voucherCode));

            // Kiểm tra voucher đã dùng chưa
            if (userVoucher.isUsed()) {
                throw new IllegalStateException("Voucher already used");
            }

            // Kiểm tra hạn sử dụng
            if (userVoucher.getExpiryAt().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("Voucher expired");
            }

            voucher = userVoucher.getVoucher();
            if (voucher.isActive()) {
                switch (voucher.getType()) {
                    case PERCENTAGE_DISCOUNT:
                        if (originalAmount.compareTo(voucher.getMinOrderAmount()) >= 0) {
                            BigDecimal percent = voucher.getDiscountValue()
                                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                            discountAmount = originalAmount.multiply(percent).setScale(2, RoundingMode.HALF_UP);
                        }
                        break;
                    case FIXED_DISCOUNT:
                        if (originalAmount.compareTo(voucher.getMinOrderAmount()) >= 0) {
                            discountAmount = voucher.getDiscountValue();
                        }
                        break;
                    case BUY_ONE_GET_ONE:
                        Optional<OrderItem> firstItemOpt = order.getOrderItems().stream().findFirst();
                        if (firstItemOpt.isPresent() && firstItemOpt.get().getQuantity() >= 2) {
                            discountAmount = firstItemOpt.get().getMenuItem().getPrice();
                        }
                        break;
                }
                if (discountAmount.compareTo(originalAmount) > 0) {
                    discountAmount = originalAmount;
                }
                // Đánh dấu voucher đã dùng
                userVoucher.setUsed(true);
                userVoucherRepo.save(userVoucher);
            }
            else {
                throw new IllegalStateException("Voucher is inactive");
            }
        }

        // Tính thành tiền cuối cùng
        BigDecimal totalAmount =  originalAmount.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);


        // Xây dựng hóa đơn
        Invoice invoice = Invoice.builder()
                .order(order)
                .voucher(voucher)
                .originalAmount(originalAmount)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .cashier(cashier)
                .paymentTime(LocalDateTime.now())
                .paymentMethod(paymentMethod)
                .build();

        // Cập nhật trạng thái đơn hàng và bàn
        order.setStatus(OrderStatus.PAID);
        if(order.getTable()!=null){
            order.getTable().setAvailable(true);
        }
        orderRepo.save(order);


        if(phoneNumber!=null&& !phoneNumber.isEmpty()){
            // Cộng điểm thưởng: 1 điểm = 1000đ
            User customer = userRepo.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new NoSuchElementException("Customer not found: " + phoneNumber));

            int pointsEarned = totalAmount
                    .divide(BigDecimal.valueOf(1000), 0, RoundingMode.FLOOR) // Làm tròn xuống
                    .intValue();

            customer.setRewardPoints(
                    (customer.getRewardPoints() == null ? 0 : customer.getRewardPoints()) + pointsEarned
            );
            userRepo.save(customer);
        }

        //cộng dồn tổng tiền doanh thu nếu đã tồn tại doanh thu ngày đó code ở stored producedure
        revenueService.recordRevenue(
                invoice.getPaymentTime().toLocalDate(),
                paymentMethod,
                invoice.getTotalAmount()
        );

        return invoiceRepo.save(invoice);
    }




        public boolean deleteInvoice(Long id){
        if(!invoiceRepo.existsById(id)){return false;}
            Invoice invoice = invoiceRepo.findById(id).get();
            invoiceRepo.deleteById(id);
            // **TRỪ DOANH THU**: dùng cùng SP nhưng gửi amount âm
            revenueService.recordRevenue(
                    invoice.getPaymentTime().toLocalDate(),
                    invoice.getPaymentMethod(),
                    invoice.getTotalAmount().negate());
            return true;
        }

        @Override
        public Optional<Invoice> getInvoiceById(Long id){
        return invoiceRepo.findById(id);
        }

        @Override
        public Invoice findById(Long id){
        return invoiceRepo.findById(id).orElseThrow(()->new NoSuchElementException("Invoice not found: "+id));
        }

        @Override
        public Page<Invoice> getAllInvoice(Pageable pageable) {
        return invoiceRepo.findAll(pageable);
        }


    @Override
    public InvoiceCalculationDTO calculateInvoiceAmount(Long orderId, String voucherCode) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));

        BigDecimal originalAmount = orderItemRepo.calculateOrderTotalAmount(orderId);

        AtomicReference<BigDecimal> discountAmount = new AtomicReference<>(BigDecimal.ZERO);


        if (voucherCode != null && !voucherCode.isEmpty()) {
            UserVoucher userVoucher = userVoucherRepo.findByCode(voucherCode)
                    .orElseThrow(() -> new NoSuchElementException("Invalid voucher code: " + voucherCode));

            if (!userVoucher.isUsed() && userVoucher.getExpiryAt().isAfter(LocalDateTime.now())) {
                Voucher voucher = userVoucher.getVoucher();
                if (voucher.isActive()) {
                    switch (voucher.getType()) {
                        case PERCENTAGE_DISCOUNT:
                            if (originalAmount.compareTo(voucher.getMinOrderAmount()) >= 0) {
                                BigDecimal percent = voucher.getDiscountValue()
                                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                                discountAmount.set(originalAmount.multiply(percent).setScale(2, RoundingMode.HALF_UP));
                            }
                            break;
                        case FIXED_DISCOUNT:
                            if (originalAmount.compareTo(voucher.getMinOrderAmount()) >= 0) {
                                discountAmount.set(voucher.getDiscountValue());
                            }
                            break;
                        case BUY_ONE_GET_ONE:
                            order.getOrderItems().stream().findFirst().ifPresent(item -> {
                                if (item.getQuantity() >= 2) {
                                    discountAmount.set(item.getMenuItem().getPrice());
                                }
                            });
                            break;
                    }

                    // Đảm bảo không vượt quá tiền gốc
                    if (discountAmount.get().compareTo(originalAmount) > 0) {
                        discountAmount.set(originalAmount);
                    }
                }
            }
        }

        BigDecimal totalAmount = originalAmount.subtract(discountAmount.get()).setScale(2, RoundingMode.HALF_UP);
        return new InvoiceCalculationDTO(originalAmount, discountAmount.get(), totalAmount);
    }



}
