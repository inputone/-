package com.huanghaha.treehole.config;

import com.huanghaha.treehole.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 注册登录拦截器，定义需要登录才能访问的路径和放行路径
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Autowired
        private LoginInterceptor loginInterceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(loginInterceptor)
                                // 需要登录才能访问的路径
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
                                                "/admin/*",
                                                "/ai/reply")
                                // 无需登录即可访问的路径
                                .excludePathPatterns(
                                                "/user/login",
                                                "/user/register",
                                                "/user/me",
                                                "/user/logout");
        }
}
