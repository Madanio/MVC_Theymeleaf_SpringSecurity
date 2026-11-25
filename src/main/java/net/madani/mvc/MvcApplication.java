package net.madani.mvc;

import net.madani.mvc.entities.Product;
import net.madani.mvc.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class MvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MvcApplication.class, args);
	}

	@Bean
	CommandLineRunner start(ProductRepository productRepository) {
		return args -> {
			productRepository.save(Product.builder().name("Computer").price(5400).quantity(12).build());
			productRepository.save(Product.builder().name("Printer").price(1200).quantity(11).build());
			productRepository.save(Product.builder().name("Smart Phone").price(12000).quantity(33).build());
			productRepository.findAll().forEach(p -> {
				System.out.println(p.toString());
			});
		};
	}

}
