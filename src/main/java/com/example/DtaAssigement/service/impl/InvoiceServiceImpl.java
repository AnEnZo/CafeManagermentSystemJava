package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.ennum.PaymentMethod;
import com.example.DtaAssigement.entity.*;
import com.example.DtaAssigement.ennum.OrderStatus;
import com.example.DtaAssigement.repository.*;
import com.example.DtaAssigement.service.InvoiceService;
import com.example.DtaAssigement.service.RevenueService;
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
        double originalAmount=orderItemRepo.calculateOrderTotalAmount(orderId);

        // Xử lý voucher nếu có
        UserVoucher userVoucher = null;
        Voucher voucher = null;
        double discountAmount = 0;

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
                        if (originalAmount >= voucher.getMinOrderAmount()) {
                            discountAmount = originalAmount * (voucher.getDiscountValue() / 100d);
                        }
                        break;
                    case FIXED_DISCOUNT:
                        if (originalAmount >= voucher.getMinOrderAmount()) {
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
                if (discountAmount > originalAmount) {
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
        double totalAmount = originalAmount - discountAmount;


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
        order.getTable().setAvailable(true);
        orderRepo.save(order);


        if(phoneNumber!=null&& !phoneNumber.isEmpty()){
            // Cộng điểm thưởng: 1 điểm = 1000đ
            User customer = userRepo.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new NoSuchElementException("Customer not found: " + phoneNumber));
            int pointsEarned = (int) Math.floor(totalAmount / 1000);
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
                    - invoice.getTotalAmount()
            );
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
