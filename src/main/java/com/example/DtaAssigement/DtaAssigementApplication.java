package com.example.DtaAssigement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class  DtaAssigementApplication {

	private static final Logger logger = LoggerFactory.getLogger(DtaAssigementApplication.class);

	public static void main(String[] args) {
		logger.info("Ứng dụng chuẩn bị khởi động.");
		SpringApplication.run(DtaAssigementApplication.class, args);
		logger.info("Ứng dụng đã khởi động thành công.");

	}
}
