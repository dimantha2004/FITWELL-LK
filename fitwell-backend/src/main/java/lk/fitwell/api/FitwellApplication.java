package lk.fitwell.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Authentication is JWT based (see security package), so the default in-memory user is not needed.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class FitwellApplication {
    public static void main(String[] args) {
        SpringApplication.run(FitwellApplication.class, args);
    }
}
