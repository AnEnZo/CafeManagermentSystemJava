package com.example.DtaAssigement.config;

import com.example.DtaAssigement.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex, HttpServletRequest request) {
        // Lấy chi tiết stack trace
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        // Trả về chi tiết lỗi dạng JSON
        String response = String.format("""
        {
            "timestamp": "%s",
            "status": 500,
            "error": "Internal Server Error",
            "message": "%s",
            "path": "%s",
            "exception": "%s",
            "stackTrace": "%s"
        }
        """,
                LocalDateTime.now(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getClass().getName(),
                stackTrace
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<String> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        return new ResponseEntity<>(
                "Định dạng phản hồi không được hỗ trợ. Vui lòng yêu cầu với application/json.",
                new HttpHeaders(),
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Không tìm thấy: " + ex.getMessage());
    }

}
