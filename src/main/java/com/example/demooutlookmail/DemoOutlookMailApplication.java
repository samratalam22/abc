package com.example.demooutlookmail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoOutlookMailApplication {


    public static void main(String[] args) {
        SpringApplication.run(DemoOutlookMailApplication.class, args);

        System.out.println("done");
    }

}
