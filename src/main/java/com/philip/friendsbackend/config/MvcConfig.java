package com.philip.friendsbackend.config;

import com.philip.friendsbackend.utils.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登入攔截器
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",        // 放行註冊接口
                        "/swagger-ui.html",      // 放行 Swagger UI 主頁面
                        "/swagger-resources/**", // 放行 Swagger 相關資源
                        "/v2/api-docs",          // 放行 Swagger API 文檔
                        "/webjars/**"            // 放行 Swagger 前端靜態資源
                );
    }
}
