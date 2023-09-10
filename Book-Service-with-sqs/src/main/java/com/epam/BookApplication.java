package com.epam;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@EnableJpaRepositories
@OpenAPIDefinition
@SpringBootApplication(exclude= {ContextStackAutoConfiguration.class})
public class BookApplication {

	Logger logger = LoggerFactory.getLogger(BookApplication.class);
	
	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(BookApplication.class, args);
	}

	@SqsListener("book-service")
	public void loadMessageFromSQS(String message)
	{
		logger.info(message);
	}

}
