package org.shiloh.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.shiloh.entity.User;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lxlei
 * @date 2020/11/5 15:07
 * @description
 */
public class MyLoginFilter extends UsernamePasswordAuthenticationFilter {

    private static final String DEFAULT_LOGIN_METHOD = "POST";

    private final SessionRegistry sessionRegistry;

    public MyLoginFilter(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }


    /**
     * 自定义认证逻辑
     * @author lxlei
     * @date 2020/11/5 15:07
     * @return org.springframework.security.core.Authentication
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // 默认只支持使用POST请求登录接口，如果不是POST则抛出异常（或给出提醒）
        if (!DEFAULT_LOGIN_METHOD.equalsIgnoreCase(request.getMethod())) {
            throw new AuthenticationServiceException(String.format("请求方法：[%s]不支持", request.getMethod()));
        }
        // 请求的Content-Type需为application/json或application/json; charset=UTF-8
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE) ||
                request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)) {
            // 验证码校验
//            final String captcha = request.getParameter("captcha");
//            if (!CaptchaUtil.ver(captcha, request)) {
//                throw new AuthenticationServiceException("验证码输入错误");
//            }
            // 获取登录请求数据
            User user;
            try {
                user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            } catch (IOException e) {
                throw new AuthenticationServiceException(e.getMessage());
            }
            // 获取用户名和密码
            if (user == null) {
                throw new AuthenticationServiceException("登录失败，登录信息为空！");
            }
            String username = StringUtils.hasLength(user.getUsername()) ? user.getUsername() : "";
            String password = StringUtils.hasLength(user.getPassword()) ? user.getPassword() : "";
            username = username.trim();
            // 构造一个以用户名和密码生成的token
            final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,
                    password);
            // set details
            super.setDetails(request, token);
            // 在处理完登录数据之后，手动向 SessionRegistryImpl 中添加一条记录
            User principal = new User();
            principal.setUsername(username);
            sessionRegistry.registerNewSession(request.getSession(true).getId(), principal);
            // 进行认证并返回
            return super.getAuthenticationManager().authenticate(token);
        } else {
            throw new AuthenticationServiceException(String.format("请求头中的Content-Type值只能为[%s]或[%s]",
                    MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE));
        }
    }
}
