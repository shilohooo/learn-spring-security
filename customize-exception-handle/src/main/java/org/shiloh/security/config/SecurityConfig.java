package org.shiloh.security.config;

import org.shiloh.security.handler.MyAccessDeniedHandler;
import org.shiloh.security.handler.MyAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author lxlei
 * @date 2020/11/13 14:10
 * @description
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    private final MyAccessDeniedHandler myAccessDeniedHandler;

    public SecurityConfig(MyAuthenticationEntryPoint myAuthenticationEntryPoint,
                          MyAccessDeniedHandler myAccessDeniedHandler) {
        this.myAuthenticationEntryPoint = myAuthenticationEntryPoint;
        this.myAccessDeniedHandler = myAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("admin");
    }

    /**
     * 认证授权配置
     * 关于异常处理请参考：{@link ExceptionTranslationFilter#doFilter(ServletRequest, ServletResponse, FilterChain)}
     * @author lxlei
     * @date 2020/11/13 14:38
     * @param http
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**")
                .hasRole("admin")
                .antMatchers("/user/**")
                .hasRole("user")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .csrf()
                .disable()
                // 配置自定义异常处理，包括处理认证和授权异常
                .exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint)
                .accessDeniedHandler(myAccessDeniedHandler);
    }
}
