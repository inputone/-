package com.huanghaha.treehole;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TreeHole 匿名社区启动类
 * 技术栈：Spring Boot 4.0.3 + MyBatis + MySQL + Redis + Spring Session
 */
@SpringBootApplication
public class TreeholeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TreeholeApplication.class, args);
        System.out.println("启动成功");
        System.out.println("http://localhost:8080/");
    }

}
