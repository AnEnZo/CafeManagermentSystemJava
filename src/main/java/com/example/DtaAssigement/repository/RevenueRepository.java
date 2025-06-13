package com.example.DtaAssigement.repository;

import com.example.DtaAssigement.entity.Revenue;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long> {

    @Procedure(procedureName = "record_revenue_by_branch")
    void recordRevenueAndBranch(
            @Param("p_date") LocalDate date,
            @Param("p_method") String method,
            @Param("p_amount") BigDecimal amount,
            @Param("p_branch_id") Long branchId
    );

    @Procedure(procedureName = "get_total_by_date_and_branch")
    Double getTotalByDateAndBranch(
            @Param("p_date") LocalDate date,
            @Param("p_branch_id") Long branchId
    );

    @Procedure(procedureName = "get_total_by_month_and_branch")
    Double getTotalByMonthAndBranch(
            @Param("p_month") Integer month,
            @Param("p_year") Integer year,
            @Param("p_branch_id") Long branchId
    );

    @Procedure(procedureName = "get_total_by_method_and_branch")
    Double getTotalByPaymentMethodAndBranch(
            @Param("p_method") String method,
            @Param("p_start") LocalDate start,
            @Param("p_end") LocalDate end,
            @Param("p_branch_id") Long branchId
    );

    @Query(value = "CALL get_grouped_by_method_and_branch(:p_start, :p_end, :p_branch_id)", nativeQuery = true)
    List<Object[]> getGroupedByMethodAndBranch(
            @Param("p_start") LocalDate start,
            @Param("p_end") LocalDate end,
            @Param("p_branch_id") Long branchId
    );

    Optional<Revenue> findByDate(LocalDate date);

}
