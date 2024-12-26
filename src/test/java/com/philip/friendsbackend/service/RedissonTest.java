package com.philip.friendsbackend.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test(){
        // list 資料存在本地 JVM 內存中
        ArrayList<String> list = new ArrayList<>();
        list.add("philip");
        System.out.println("list: " + list.get(0));
        list.remove(0);

        // 資料存在 redis 內存中
        RList<String> rList = redissonClient.getList("test-list");
        rList.add("philip");
        System.out.println("rList: " + rList.get(0));
        rList.remove(0);

        // map
        Map<String, Integer> map = new HashMap<>();
        map.put("philip", 10);
        map.get("philip");

        RMap<Object, Object> rMap = redissonClient.getMap("test-map");
        rMap.put("philip", 30);
    }
}
