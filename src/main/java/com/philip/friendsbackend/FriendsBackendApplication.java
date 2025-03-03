package com.philip.friendsbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.philip.friendsbackend.mapper")
@EnableScheduling
public class FriendsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendsBackendApplication.class, args);
    }

}
