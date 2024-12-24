package com.philip.friendsbackend.service;

import com.philip.friendsbackend.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 增
        valueOperations.set("philipString", "good");
        valueOperations.set("philipInt", 1);
        valueOperations.set("philipDouble", 2.0);
        User user = new User();
        user.setId(1L);
        user.setUsername("philip");
        valueOperations.set("philipUser", user);
        // 查
        Object philip = valueOperations.get("philipString");
        Assertions.assertTrue("good".equals((String) philip));
        philip = valueOperations.get("philipInt");
        Assertions.assertTrue(1 == (Integer)philip);
        philip = valueOperations.get("philipDouble");
        Assertions.assertTrue(2.0 == (Double) philip);
        System.out.println(valueOperations.get("philipUser"));
    }
}
