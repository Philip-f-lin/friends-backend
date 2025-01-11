package com.philip.friendsbackend.service;

import com.philip.friendsbackend.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * 演算法工具類測試
 */
@SpringBootTest
public class AlgorithmUtilsTest {

    @Test
    void testCompareTags(){
        List<String> tagList1 = Arrays.asList("Java", "三年", "男");
        List<String> tagList2 = Arrays.asList("Python", "三年", "男");
        List<String> tagList3 = Arrays.asList("Java", "二年", "女");

        int score1 = AlgorithmUtils.minDistance(tagList1, tagList2);
        int score2 = AlgorithmUtils.minDistance(tagList1, tagList3);

        System.out.println(score1);// 1
        System.out.println(score2);// 2
    }

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
