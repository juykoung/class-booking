package com.classbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class ClassBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassBookingApplication.class, args);
    }

}
