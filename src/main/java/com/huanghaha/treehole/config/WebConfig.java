package com.huanghaha.treehole.config;

import com.huanghaha.treehole.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(
                        "/post/publish",
                        "/post/delete",
                        "/post/like/*",
                        "/post/liked/*",
                        "/post/favorite/*",
                        "/post/favorited/*",
                        "/post/favorites",
                        "/comment",
                        "/comment/*",
                        "/admin/*"
                )
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/user/me",
                        "/user/logout"
                );
    }
}
