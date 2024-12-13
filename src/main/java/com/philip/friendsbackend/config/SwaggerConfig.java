package com.philip.friendsbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 自訂 Swagger 介面文件的配置
 *
 * @author Philip
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()) // 設定文檔信息
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.philip.friendsbackend.controller")) // 僅掃描controller的介面
                .paths(PathSelectors.any()) // 掃描所有的路徑
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact(
                "Philip",
                "https://github.com/Philip-f-lin",
                "philip.f.lin@gmail.com");
        return new ApiInfoBuilder()
                .title("Friends Backend API Documentation") // 文檔標題
                .description("Friends Backend - 提供用戶中心相關的 API 文檔") // 描述
                .termsOfServiceUrl("https://github.com/Philip-f-lin")
                .version("1.0")
                .contact(contact)
                .build();
    }
}
