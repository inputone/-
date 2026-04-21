package com.huanghaha.treehole.interceptor;

import com.huanghaha.treehole.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 登录拦截器
 * 校验 Session 中是否存在 loginUser 属性，未登录则返回 JSON 格式的错误提示
 * 拦截路径由 WebConfig 配置
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        
        // Session 不存在或未登录，返回错误提示
        if (session == null || session.getAttribute("loginUser") == null) {
            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error("请先登录")));
            return false;
        }
        
        return true;
    }
}
