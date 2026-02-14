package com.budgettrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BudgetTripApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetTripApplication.class, args);
        System.out.println("----------------------------------------------------------");
        System.out.println("BudgetTrip Application Started Successfully!");
        System.out.println("Go to http://localhost:8080 to view your app.");
        System.out.println("----------------------------------------------------------");
    }

}