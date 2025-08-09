package com.docusense.DocuSenseApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.docusense.repository")
@EntityScan(basePackages = "com.docusense.model")
@ComponentScan("com.docusense")
public class DocuSenseApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocuSenseApplication.class, args);
	}

}
