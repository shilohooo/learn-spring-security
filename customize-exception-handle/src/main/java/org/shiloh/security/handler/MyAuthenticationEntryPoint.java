package org.shiloh.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author lxlei
 * @date 2020/11/13 14:18
 * @description 自定义认证异常处理：当用户进行登录操作后，如果认证失败的话就给出相应提醒
 */
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 如果用户登录认证失败了，就根据异常类型给出相应的提醒
     * @author lxlei
     * @date 2020/11/13 14:19
     * @param req 请求对象
     * @param res 响应对象
     * @param e 认证失败异常对象，具体见：{@link AuthenticationException}
     */
    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e)
            throws IOException {
        res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        final PrintWriter writer = res.getWriter();
        writer.write(new ObjectMapper().writeValueAsString(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage())));
        writer.flush();
        writer.close();
    }
}
