package com.philip.friendsbackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.philip.friendsbackend.model.domain.User;
import com.philip.friendsbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 緩存預熱任務
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    // 每天執行，預熱推薦使用者
    // 秒（0~59）
    // 分鐘（0~59）
    // 小時（0~23）　　
    // 日（0~31，但是你需要考慮你月的天數）
    // 月（0~11）　　
    // 星期（1~7 1=SUN 以此類推：SUN，MON，TUE，WED，THU，FRI，SAT）
    // "*" 字元代表所有可能的值
    // "?" 表示不指定值
    @Scheduled(cron = "0 00 16 * * *")
    public void doCacheRecommendUser() {
        log.info("開始緩存預熱");
        RLock lock = redissonClient.getLock("philip:precachejob:docache:lock");
        try {
            // 只有一個線程能獲取到鎖
            if (lock.tryLock(0, TimeUnit.MILLISECONDS)) {
                // 測試 WatchDog
                Thread.sleep(60000L);
                // 僅提供活躍使用者緩存預熱（因為記憶體有限）
                // 查詢最近活躍的使用者列表
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.orderByDesc("update_time"); // 根據 update_time 倒序排列
                queryWrapper.last("LIMIT 10"); // 限制查詢前 10 名
                List<User> activeUsers = userService.list(queryWrapper);

                if (activeUsers == null || activeUsers.isEmpty()) {
                    log.warn("No active users found for caching");
                    return;
                }

                // 動態生成 mainUserList
                List<Long> mainUserList = activeUsers.stream()
                        .map(User::getId)
                        .collect(Collectors.toList()); // 提取 userId

                for (Long userId : mainUserList) {
                    QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                    userQueryWrapper.ne("id", userId); // 排除自己
                    userQueryWrapper.orderByDesc("create_time"); // 根據建立時間降序排序
                    Page<User> userPage = userService.page(new Page<>(1, 20), userQueryWrapper);
                    String redisKey = String.format("philip:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    // 寫緩存
                    try {
                        valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 要寫在 finally 如果寫在 try 裡面，報錯就不會執行
            // 只能釋放自己的鎖
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unlock: " + Thread.currentThread().getId());
                lock.unlock();
                log.info("緩存預熱結束");
            }
        }
    }
}
