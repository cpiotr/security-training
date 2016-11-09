package pl.ciruk.security.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "pl.ciruk.security.spring")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
