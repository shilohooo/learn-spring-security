package org.shiloh.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wf.captcha.utils.CaptchaUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author lxlei
 * @date 2020/11/5 15:20
 * @description 自定义登录认证失败的处理器
 */
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * 自定义
     * @author lxlei
     * @date 2020/11/5 15:20
     * @param request
     * @param response
     * @param exception
     * @return void
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // 清除session中的验证码
        CaptchaUtil.clear(request);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        final PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper()
                .writeValueAsString(ResponseEntity.ok(exception.getMessage()))
        );
        writer.flush();
        writer.close();
    }
}
