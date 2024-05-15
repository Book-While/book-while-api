package com.bookwhile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the BookWhile application.
 * <p>
 * This class initializes and runs the Spring Boot application.
 * </p>
 *
 * @see org.springframework.boot.SpringApplication
 */
@SpringBootApplication
public class BookWhileApplication {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args Command line arguments (if any).
     */
    public static void main(String[] args) {
        SpringApplication.run(BookWhileApplication.class, args);
    }

}
