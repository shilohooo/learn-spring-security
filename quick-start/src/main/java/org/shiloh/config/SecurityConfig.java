package org.shiloh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.*;

/**
 * @author lxlei
 * @date 2020/11/3 15:15
 * @description Spring Security 配置
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 配置用于加密用户密码的类
     * NoOpPasswordEncoder: 不加密用户密码，直接以明文方式存储
     * @author lxlei
     * @date 2020/11/3 15:16
     * @return org.springframework.security.crypto.password.PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 在内存中定义一个用户信息，如果需要配置多个用户，用 and 相连。
     * 在没有 Spring Boot 的时候，我们都是在 SSM 中使用 Spring Security，这种时候都是在 XML 文件中配置 Spring Security，
     * 既然是 XML 文件，标签就有开始与结束，现在的 and 符号相当于就是 XML 标签的结束符，表示结束当前标签，
     * 这个时候上下文会回到 inMemoryAuthentication 方法中，然后开启新用户的配置。
     * @author lxlei
     * @date 2020/11/3 15:24
     * @param auth
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                // 用户名
                .withUser("shiloh")
                // 密码
                .password("123456")
                // 角色
                .roles("admin");
    }

    /**
     * 配置无需认证就能访问的资源，一般用于开放静态资源
     * @author lxlei
     * @date 2020/11/3 15:33
     * @param web
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/js/**", "/css/**", "/images/**", "/fonts/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 开启认证请求配置
        http.authorizeRequests()
                // 所有请求都需要通过认证才能访问
                .anyRequest()
                .authenticated()
                .and()
                // 配置与登录相关的信息
                // 基于表单的登录
                .formLogin()
                // 配置表单登录页面位置
                // 当我们定义了登录页面为 /login.html 的时候，Spring Security 也会帮我们自动注册一个 /login.html 的接口，
                // 这个接口是 POST 请求，用来处理登录逻辑。配置后访问任意页面，就会自动重定向到这里定义的login.html页面上来。
                // 配置了loginPage为/login.html之后，就是设置登录页面的地址为 /login.html。实际上它还有一个隐藏的操作:
                // 就是登录接口地址也设置成/login.html。也就是说新的登录页面和登录接口地址都是 /login.html
                // 此时发送Get请求的/login.html则为访问登录页，发送Post请求的/login.html就是提交登录数据请求登录系统
                .loginPage("/login.html")
                // 开放表单登录页面资源的访问权限，无需认证就能访问
                .permitAll()
                .and()
                // 禁用csrf
                .csrf()
                .disable();
    }
}
