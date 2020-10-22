package com.picture.identity_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 图片信息交流系统
 *      登陆、token签发、信息获取、注册等相关模块文档配置
 *
 * http://localhost:8762/swagger-ui.html
 *
 * @author Yue Wu
 * @since 2020/9/29
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.picture.identity_service.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        ApiInfo build = new ApiInfoBuilder()
                // 设置页面标题
                .title("图片信息交流系统——身份认证相关模块文档")
                // 描述
                .description("欢迎访问图片信息交流系统接口文档，本文档着重描述登陆、token签发、信息获取、注册等相关模块。\n"
                        + "前后端交互均采用JSON格式传递，"
                        + "后端基础传递ResultMod格式为：\n{\n'status':'success',\n'code':200,\n'message':'&&**%%'\n}")
                // 定义版本号
                .version("0.0.1").build();
        return build;
    }

}
