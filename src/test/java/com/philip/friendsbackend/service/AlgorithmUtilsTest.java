package com.philip.friendsbackend.service;

import com.philip.friendsbackend.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 演算法工具類測試
 */
@SpringBootTest
public class AlgorithmUtilsTest {

    @Test
    void test(){
        String word1 = "philip1234";
        String word2 = "philip2222";
        String word3 = "philip2234";

        int score1 = AlgorithmUtils.minDistance(word1, word2);
        int score2 = AlgorithmUtils.minDistance(word1, word3);

        System.out.println(score1);// 3
        System.out.println(score2);// 1
    }
}
