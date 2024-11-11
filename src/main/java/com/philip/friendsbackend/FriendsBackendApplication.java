package com.philip.friendsbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.philip.friendsbackend.mapper")
public class FriendsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendsBackendApplication.class, args);
    }

}
