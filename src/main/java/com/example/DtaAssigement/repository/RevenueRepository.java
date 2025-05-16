package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.Revenue;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long> {

    @Procedure(name = "record_revenue")
    void recordRevenue(
            @Param("p_date") LocalDate date,
            @Param("p_method") String method,
            @Param("p_amount") Double amount
    );

    @Procedure(procedureName = "get_total_by_date")
    Double getTotalByDate(
            @Param("p_date") LocalDate date
    );

    @Procedure(procedureName = "get_total_by_month")
    Double getTotalByMonth(
            @Param("p_month") Integer month,
            @Param("p_year") Integer year
    );

    @Procedure(procedureName = "get_total_by_method")
    Double getTotalByPaymentMethod(
            @Param("p_method") String method,
            @Param("p_start") LocalDate start,
            @Param("p_end") LocalDate end
    );

    @Query(value = "CALL get_grouped_by_method(:start, :end)", nativeQuery = true)
    List<Object[]> getGroupedByMethod(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
