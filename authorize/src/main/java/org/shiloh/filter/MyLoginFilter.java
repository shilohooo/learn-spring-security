package org.shiloh.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.shiloh.entity.LoginForm;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lxlei
 * @date 2020/11/4 10:09
 * @description 自定义登录过滤器
 * 用来替代 {@link UsernamePasswordAuthenticationFilter}
 */
public class MyLoginFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * 默认登录请求方式
     */
    private static final String DEFAULT_LOGIN_METHOD = "POST";

    /**
     * 登录认证处理
     * @author lxlei
     * @date 2020/11/4 10:12
     * @param request 请求
     * @param response 响应
     * @return org.springframework.security.core.Authentication
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // 默认只支持使用POST请求登录接口，如果不是POST则抛出异常（或给出提醒）
        if (!DEFAULT_LOGIN_METHOD.equals(request.getMethod())) {
            throw new AuthenticationServiceException(String.format("请求方法：[%s]不支持", request.getMethod()));
        }
        // 请求的Content-Type需为application/json或application/json; charset=UTF-8
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE) ||
                request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)) {
            // 获取登录请求数据
            LoginForm loginForm = null;
            try {
                loginForm = new ObjectMapper().readValue(request.getInputStream(), LoginForm.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 获取用户名和密码
            Assert.notNull(loginForm, "登录失败，登录信息为空~");
            String username = StringUtils.hasLength(loginForm.getUsername()) ? loginForm.getUsername() : "";
            String password = StringUtils.hasLength(loginForm.getPassword()) ? loginForm.getPassword() : "";
            username = username.trim();
            // 构造一个以用户名和密码生成的token
            final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,
                    password);
            // set details
            super.setDetails(request, token);
            // 进行认证并返回
            return super.getAuthenticationManager().authenticate(token);
        } else {
            // 如果请求数据格式不是JSON的话就用父类的登录处理方法
            return super.attemptAuthentication(request, response);
        }
    }
}
