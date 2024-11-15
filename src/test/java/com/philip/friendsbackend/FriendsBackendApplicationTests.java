package com.philip.friendsbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest
class FriendsBackendApplicationTests {

    @Test
    void testDigest(){
        StringBuilder hashedPassword = new StringBuilder();
        StringBuilder md5DigestAsHex = DigestUtils.appendMd5DigestAsHex(("philip" + "12345678").getBytes(), hashedPassword);
        System.out.println(md5DigestAsHex.toString());
    }

    @Test
    void contextLoads() {
    }

}
