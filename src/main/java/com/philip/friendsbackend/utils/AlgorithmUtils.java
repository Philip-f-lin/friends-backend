package com.philip.friendsbackend.utils;

import java.util.List;

/**
 * 演算法工具類
 *
 * @author Philip
 */
public class AlgorithmUtils {

    /**
     * 比較兩個 tagList
     * 編輯距離演算法（用於計算兩個最相似的字串
     * 參考：https://blog.csdn.net/tianjindong0804/article/details/115803158
     * @param tagList1
     * @param tagList2
     * @return
     */
    public static int minDistance(List<String> tagList1, List<String> tagList2) {
        if (tagList1 == null || tagList2 == null) {
            throw new RuntimeException("參數不能為 null");
        }
        int[][] dp = new int[tagList1.size() + 1][tagList2.size() + 1];
        // 初始化 DP 陣列
        for (int i = 0; i <= tagList1.size(); i++) {
            dp[i][0] = i;
        }
        for (int i = 0; i <= tagList2.size(); i++) {
            dp[0][i] = i;
        }
        int cost;
        for (int i = 1; i <= tagList1.size(); i++) {
            for (int j = 1; j <= tagList2.size(); j++) {
                if (tagList1.get(i - 1).equals(tagList2.get(j - 1))) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                dp[i][j] = min(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost);
            }
        }
        return dp[tagList1.size()][tagList2.size()];
    }

    /**
     * 比較兩個字串
     * 編輯距離演算法（用於計算兩個最相似的字串
     * 參考：https://blog.csdn.net/tianjindong0804/article/details/115803158
     * @param word1
     * @param word2
     * @return
     */
    public static int minDistance(String word1, String word2) {
        if (word1 == null || word2 == null) {
            throw new RuntimeException("參數不能為 null");
        }
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];
        // 初始化 DP 陣列
        for (int i = 0; i <= word1.length(); i++) {
            dp[i][0] = i;
        }
        for (int i = 0; i <= word2.length(); i++) {
            dp[0][i] = i;
        }
        int cost;
        for (int i = 1; i <= word1.length(); i++) {
            for (int j = 1; j <= word2.length(); j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                dp[i][j] = min(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost);
            }
        }
        return dp[word1.length()][word2.length()];
    }

    private static int min(int x, int y, int z) {
        return Math.min(x, Math.min(y, z));
    }
}
