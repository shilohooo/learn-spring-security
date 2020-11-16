package org.shiloh.controller;

import com.wf.captcha.utils.CaptchaUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lxlei
 * @date 2020/11/5 15:10
 * @description
 */
@RestController
public class LoginController {

    /**
     * 获取验证码
     * @author lxlei
     * @date 2020/11/5 15:11
     * @param request
     * @param response
     */
    @GetMapping("/captcha.png")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CaptchaUtil.out(request, response);
    }
}
