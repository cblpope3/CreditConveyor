package ru.leonov.conveyor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CreditConveyorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditConveyorApplication.class, args);
        //todo remove this system.out
        System.out.println("i'm working!");
    }
}
