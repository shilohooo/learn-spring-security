package org.shiloh.config;

import org.apache.catalina.session.StandardSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * @author lxlei
 * @date 2020/11/5 14:09
 * @description Spring Security Config
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 加密类
     * @author lxlei
     * @date 2020/11/5 14:18
     * @return org.springframework.security.crypto.password.PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder encoder = (DelegatingPasswordEncoder) PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
        encoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        return encoder;
    }

    /**
     * 在Spring Security中，通过监听session的销毁事件来及时的清理session的记录。
     * 用户从不同的浏览器登录后，都会有对应的session，当用户注销登录之后，session就会失效，
     * 但是默认的失效是通过调用 {@link StandardSession#invalidate()} 方法来实现的:
     * 这一个失效事件无法被Spring容器感知到，进而导致当用户注销登录之后，Spring Security 没有及时清理会话信息表，以为用户还在线，
     * 进而导致用户无法重新登录进来，为了解决这一问题，需要提供一个 {@link HttpSessionEventPublisher}
     * 这个类实现了 {@link javax.servlet.http.HttpSessionListener} 接口，在该Bean中，可以将session创建以及销毁的事件及时感知到，
     * 并且调用Spring中的事件机制将相关的创建和销毁事件发布出去，进而被 Spring Security 感知到
     * @author lxlei
     * @date 2020/11/5 14:19
     * @return org.springframework.security.web.session.HttpSessionEventPublisher
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * 基于内存数据源配置一个用户测试一台设置只允许一个登录
     * @author lxlei
     * @date 2020/11/5 14:12
     * @param auth
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("admin")
                .roles("admin");
    }

    /**
     * 认证授权配置
     * @author lxlei
     * @date 2020/11/5 14:11
     * @param http
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .permitAll()
                .and()
                .csrf()
                .disable()
                // 开启session管理
                .sessionManagement()
                // maximumSessions 表示配置最大会话数为 1，这样后面的登录就会自动踢掉前面的登录
                .maximumSessions(1)
                // 如果相同的用户已经登录了，则不踢掉他，而是禁止新的登录操作
                .maxSessionsPreventsLogin(true);
    }
}
