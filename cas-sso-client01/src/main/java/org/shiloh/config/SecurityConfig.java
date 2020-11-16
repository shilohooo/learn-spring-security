package org.shiloh.config;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * @author lxlei
 * @date 2020/11/7 17:39
 * @description Spring Security Configuration
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final AuthenticationProvider authenticationProvider;

    private final SingleSignOutFilter singleSignOutFilter;

    private final LogoutFilter logoutFilter;

    private final CasAuthenticationFilter casAuthenticationFilter;

    public SecurityConfig(AuthenticationEntryPoint authenticationEntryPoint,
                          AuthenticationProvider authenticationProvider, SingleSignOutFilter singleSignOutFilter,
                          LogoutFilter logoutFilter, CasAuthenticationFilter casAuthenticationFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationProvider = authenticationProvider;
        this.singleSignOutFilter = singleSignOutFilter;
        this.logoutFilter = logoutFilter;
        this.casAuthenticationFilter = casAuthenticationFilter;
    }

    /**
     * 设置authenticationProvider
     * @author lxlei
     * @date 2020/11/7 17:42
     * @param auth
     * @return void
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    /**
     * 整合cas单点登录配置
     * @author lxlei
     * @date 2020/11/7 17:41
     * @param http
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // /user/**资源需要具有user角色才能访问
                .antMatchers("/user/**")
                .hasRole("user")
                // 开放/login/cas 也就是cas server单点登录url的访问权限
                .antMatchers("/login/cas")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .addFilter(casAuthenticationFilter)
                .addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class)
                .addFilterBefore(logoutFilter, LogoutFilter.class);
    }
}
