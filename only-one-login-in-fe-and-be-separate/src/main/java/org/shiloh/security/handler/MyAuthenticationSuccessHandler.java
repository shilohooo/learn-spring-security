package org.shiloh.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wf.captcha.utils.CaptchaUtil;
import org.shiloh.entity.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author lxlei
 * @date 2020/11/5 15:15
 * @description 自定义登录认证成功的处理器
 */
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 自定义登录认证成功后的处理逻辑
     * @author lxlei
     * @date 2020/11/5 15:17
     * @param request
     * @param response
     * @param authentication
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 清除session中的验证码
        CaptchaUtil.clear(request);
        User user = (User) authentication.getPrincipal();
        user.setPassword(null);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        final PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper()
                .writeValueAsString(ResponseEntity.ok(user))
        );
        writer.flush();
        writer.close();
    }
}
