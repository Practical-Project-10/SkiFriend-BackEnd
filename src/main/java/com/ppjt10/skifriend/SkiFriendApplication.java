package com.ppjt10.skifriend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SkiFriendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkiFriendApplication.class, args);
    }


}
//    public static void main(String[] args) {
//        SpringApplication.run(SkiFriendApplication.class, args);
//    }
//