package org.shiloh.authentication;

import com.wf.captcha.utils.CaptchaUtil;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lxlei
 * @date 2020/11/5 12:15
 * @description
 */
public class MyWebAuthenticationDetails extends WebAuthenticationDetails {

    /**
     * 验证码校验结果
     */
    private final Boolean isPassed;

    /**
     * Records the remote address and will also set the session Id if a session already
     * exists (it won't create one).
     *
     * @param request that the authentication request was received from
     */
    public MyWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        final String captcha = request.getParameter("captcha");
        this.isPassed = CaptchaUtil.ver(captcha, request);
    }

    public Boolean getIsPassed() {
        return isPassed;
    }
}
