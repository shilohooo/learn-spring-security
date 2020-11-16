package org.shiloh.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author lxlei
 * @date 2020/11/13 14:12
 * @description 自定义异常处理器：处理拒绝访问资源
 */
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 当用户访问没有权限的资源时，给出提醒
     * @author lxlei
     * @date 2020/11/13 14:16
     * @param req 请求对象
     * @param res 响应对象
     * @param e 拒绝访问异常对象，具体见：{@link AccessDeniedException}
     */
    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException e)
            throws IOException {
        res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        final PrintWriter writer = res.getWriter();
        writer.write(new ObjectMapper().writeValueAsString(ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("抱歉，您没有权限访问该资源！")));
        writer.flush();
        writer.close();
    }
}
