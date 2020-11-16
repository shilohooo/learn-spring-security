package org.shiloh.provider;

import org.shiloh.authentication.MyWebAuthenticationDetails;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author lxlei
 * @date 2020/11/5 10:21
 * @description
 */
public class MyAuthenticationProvider extends DaoAuthenticationProvider {

    /**
     * 添加自定义的认证逻辑
     * @author lxlei
     * @date 2020/11/5 10:22
     * @param userDetails
     * @param authentication
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        MyWebAuthenticationDetails details = (MyWebAuthenticationDetails) authentication.getDetails();
        // 校验验证码，错误则做出一些处理
        if (!details.getIsPassed()) {
            throw new AuthenticationServiceException("验证码错误");
        }
        // 验证码校验成功后继续走父类的认证处理逻辑
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
