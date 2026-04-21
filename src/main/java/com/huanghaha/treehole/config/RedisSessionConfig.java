package com.huanghaha.treehole.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Spring Session + Redis 配置
 * 使用 Redis 存储 HTTP Session，实现分布式会话；
 * Cookie 名称为 TREEHOLE_SESSION，SameSite=Lax 防止 CSRF
 */
@Configuration
public class RedisSessionConfig {

    /** Spring Session 使用 JDK 序列化，与 RedisTemplate 保持一致 */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new JdkSerializationRedisSerializer();
    }

    /** 自定义 Cookie 配置：名称、路径、安全属性 */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("TREEHOLE_SESSION");
        serializer.setCookiePath("/");
        serializer.setUseHttpOnlyCookie(true); // 防止 XSS 读取 Cookie
        serializer.setUseSecureCookie(false); // 本地开发环境暂不启用 HTTPS
        serializer.setSameSite("Lax"); // 防止 CSRF 攻击
        return serializer;
    }
}
