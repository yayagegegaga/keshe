package com.example.strayanimal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.strayanimal.mapper")
@SpringBootApplication
public class StrayAnimalApplication {

    public static void main(String[] args) {
        SpringApplication.run(StrayAnimalApplication.class, args);
    }
}
