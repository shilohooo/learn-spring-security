package org.shiloh.controller;

import com.wf.captcha.utils.CaptchaUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lxlei
 * @date 2020/11/5 10:10
 * @description
 */
@RestController
public class LoginController {

    /**
     * 生成验证码
     * @author lxlei
     * @date 2020/11/5 10:15
     * @param req
     * @param res
     */
    @GetMapping("/captcha.png")
    public void getCaptcha(HttpServletRequest req, HttpServletResponse res) throws IOException {
        CaptchaUtil.out(req, res);
    }
}
