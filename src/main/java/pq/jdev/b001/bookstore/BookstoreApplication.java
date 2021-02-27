package pq.jdev.b001.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@SpringBootApplication
@EnableWebSecurity
public class BookstoreApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}
}
