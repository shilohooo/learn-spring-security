package org.shiloh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.shiloh.filter.MyLoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lxlei
 * @date 2020/11/4 10:05
 * @description Spring Security配置
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 配置一个用于加密用户密码的类
     * @author lxlei
     * @date 2020/11/4 10:53
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
     * 配置一个存储于内存的用户信息测试登录认证
     * @author lxlei
     * @date 2020/11/4 10:53
     * @param auth
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("shiloh")
                .password("123456")
                .roles("admin");
    }


    /**
     * 将自定义处理登录认证逻辑的过滤器注入到SpringIOC容器中
     * @author lxlei
     * @date 2020/11/4 10:54
     * @return org.shiloh.filter.MyLoginFilter
     */
    @Bean
    public MyLoginFilter myLoginFilter() throws Exception {
        final MyLoginFilter myLoginFilter = new MyLoginFilter();
        // 登录成功后的处理
        myLoginFilter.setAuthenticationSuccessHandler((req, res, authentication) -> {
            res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            final PrintWriter writer = res.getWriter();
            // 响应数据
            Map<String, Object> responseData = new HashMap<>(16);
            responseData.put("code", 0);
            responseData.put("message", "登录成功");
            responseData.put("data", authentication);
            final String result = new ObjectMapper().writeValueAsString(responseData);
            writer.write(result);
            writer.flush();
            writer.close();
        });
        // 登录失败后的处理
        myLoginFilter.setAuthenticationFailureHandler((req, res, exception) -> {
            res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            final PrintWriter writer = res.getWriter();
            // 响应数据
            Map<String, Object> responseData = new HashMap<>(16);
            responseData.put("code", 500);
            responseData.put("message", "用户名或密码错误");
            final String result = new ObjectMapper().writeValueAsString(responseData);
            writer.write(result);
            writer.flush();
            writer.close();
        });
        // 注入认证管理器
        myLoginFilter.setAuthenticationManager(authenticationManagerBean());
        // 设置登录接口地址，默认为/login
        myLoginFilter.setFilterProcessesUrl("/do-login");
        return myLoginFilter;
    }

    /**
     * 认证和授权相关配置
     * @author lxlei
     * @date 2020/11/4 11:18
     * @param http
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .csrf()
                .disable();
        // 将自定义处理登录认证的过滤器添加到SpringSecurity的过滤器链中
        // addFilterAt(Filter filter, Class<?> atFilter)此方法用于指定某个过滤器替换掉SpringSecurity原来使用的过滤器
        http.addFilterAt(myLoginFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
