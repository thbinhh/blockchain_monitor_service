package com.example.blockchainmonitorservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BlockchainMonitorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlockchainMonitorServiceApplication.class, args);
	}

}
