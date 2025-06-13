package com.example.DtaAssigement.controller;


import com.example.DtaAssigement.dto.SalaryRecordDTO;
import com.example.DtaAssigement.dto.SalaryRecordRequestDTO;
import com.example.DtaAssigement.entity.SalaryRecord;
import com.example.DtaAssigement.invoidGenerateWordExcelQrCode.WordGenerator;
import com.example.DtaAssigement.service.SalaryRecordService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@AllArgsConstructor
public class SalaryRecordController {

    private final SalaryRecordService salaryRecordService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<List<SalaryRecordDTO>> getAll() {
        return ResponseEntity.ok(salaryRecordService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<SalaryRecordDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(salaryRecordService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SalaryRecordDTO> create(@Valid @RequestBody SalaryRecordRequestDTO dto) {
        SalaryRecordDTO created = salaryRecordService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<SalaryRecordDTO> update(@PathVariable Long id,
//                                                  @Valid @RequestBody SalaryRecordDTO dto) {
//        return ResponseEntity.ok(salaryRecordService.update(id, dto));
//    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        salaryRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{employeeId}/export")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void exportToWord(
            @PathVariable Long employeeId,
            @RequestParam("month") String monthStr,
            HttpServletResponse response) {

        try {
            // Parse tham số month (format "YYYY-MM")
            YearMonth month = YearMonth.parse(monthStr);

            // Lấy danh sách bản ghi lương của employee trong tháng đó
            List<SalaryRecord> records = salaryRecordService.getByEmployeeAndMonth(employeeId, month);

            if (records.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }

            // Tạo tên file có chứa tháng
            String filename = String.format("salary_%d_%s.docx", employeeId, month);
            String headerValue = "attachment; filename=\"" +
                    URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()) + "\"";

            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", headerValue);

            // Xuất file
            WordGenerator.exportSalaryRecords(records, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xuất file Word: " + e.getMessage(), e);
        }
    }

}
