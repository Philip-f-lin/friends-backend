package com.philip.friendsbackend.service;

import com.philip.friendsbackend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class InsertUsersTest {
    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(60, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 大量寫入使用者資料
     */
    @Test
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("模擬資料");
            user.setUserAccount("fake");
            user.setAvatarUrl("https://cdn-icons-png.flaticon.com/512/3135/3135768.png");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@gmail.com");
            user.setTags("[]");
            user.setUserStatus(0);
            userList.add(user);
        }
        // 插入 10 萬條資料，以每 1 萬條為一批
        userService.saveBatch(userList, 10000);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

    /**
     * 併發大量寫入使用者資料
     */
    @Test
    public void doConcurrencyInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 分 n 組同時進行，而不是 1 組插入完再執行下 1 組
        int batchSize = 4000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            List<User> userList = new ArrayList<>();
            while(true){
                j++;
                User user = new User();
                user.setUsername("模擬資料");
                user.setUserAccount("fake");
                user.setAvatarUrl("https://cdn-icons-png.flaticon.com/512/3135/3135768.png");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@gmail.com");
                user.setTags("[]");
                user.setUserStatus(0);
                userList.add(user);
                if(j % batchSize == 0){
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("Thread Name: " + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }
}
