package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.dto.RevenueDTO;
import com.example.DtaAssigement.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("""
      SELECT
        DATE(i.paymentTime) AS period,
        SUM(i.totalAmount)  AS total
      FROM Invoice i
      GROUP BY DATE(i.paymentTime)
      ORDER BY DATE(i.paymentTime)
    """)
    List<RevenueDTO> findDailyRevenue();

    @Query("""
      SELECT
        FUNCTION('DATE_FORMAT', i.paymentTime, '%Y-%m-01') AS period,
        SUM(i.totalAmount)                             AS total
      FROM Invoice i
      GROUP BY FUNCTION('DATE_FORMAT', i.paymentTime, '%Y-%m-01')
      ORDER BY FUNCTION('DATE_FORMAT', i.paymentTime, '%Y-%m-01')
    """)
    List<RevenueDTO> findMonthlyRevenue();

}
