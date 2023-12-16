package com.bbi.ps7;

import com.bbi.ps7.service.ConversionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Ps7Application {

	public static void main(String[] args) {
		SpringApplication.run(Ps7Application.class, args);

		ConversionService conversionService = new ConversionService();
		conversionService.csvService();
	}

}
