package com.philip.friendsbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 當前跨域請求最大有效時長。這裡預設1天
    private static final long MAX_AGE = 24 * 60 * 60;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 1. 設置允許的來源，列出具體的前端地址
        corsConfiguration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173"
        ));

        // 2. 設置允許的請求頭
        corsConfiguration.addAllowedHeader("*");

        // 3. 設置允許的請求方法
        corsConfiguration.addAllowedMethod("*");

        // 4. 允許攜帶憑證（如 Cookie）
        corsConfiguration.setAllowCredentials(true);

        // 5. 設置預檢請求的緩存時間
        corsConfiguration.setMaxAge(3600L);

        // 6. 註冊 CORS 配置
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
