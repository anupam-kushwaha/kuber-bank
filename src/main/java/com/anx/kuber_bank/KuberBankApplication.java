package com.anx.kuber_bank;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition (
		info = @Info(
				title = "Kuber Bank Application",
				description = "Bankend rest APIs for Kuber Bank",
				version = "v1.0",
				contact = @Contact(
						name = "Anupam Kushwaha",
						email = "anupamkushwaha1426@gmail.com",
						url = "https://github.com/anupam-kushwaha/kuber-bank.git"
				),
				license = @License(
						name = "Kuber Bank",
						url = "https://github.com/anupam-kushwaha/kuber-bank.git"
				)
		)
)
public class KuberBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(KuberBankApplication.class, args);
	}

}
