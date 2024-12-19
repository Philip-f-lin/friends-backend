package com.philip.friendsbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
      public class WebMvcConfig implements WebMvcConfigurer {

          @Override
          public void addCorsMappings(CorsRegistry registry) {
              //設定允許跨域的路徑
              registry.addMapping("/**")
                      //設定允許跨域請求的域名
                      //當**Credentials為true時，**來源不能為星號，需為具體的ip位址【若介面不帶cookie,ip則需設定成具體ip】
                      .allowedOrigins("http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:9527", "http://127.0.0.1:8082", "http://127.0.0.1:8083")
                      // 是否允許憑證不再預設開啟
                      .allowCredentials(true)
                      //設定允許的方法
                      .allowedMethods("*")
                      //跨域允許時間
                      .maxAge(3600);
          }
      }
