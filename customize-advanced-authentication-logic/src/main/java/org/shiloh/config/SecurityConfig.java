package org.shiloh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wf.captcha.utils.CaptchaUtil;
import org.shiloh.authentication.MyWebAuthenticationDetailsSource;
import org.shiloh.provider.MyAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.PrintWriter;
import java.util.Collections;

/**
 * @author lxlei
 * @date 2020/11/5 10:15
 * @description Spring Security Configuration
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MyWebAuthenticationDetailsSource myWebAuthenticationDetailsSource;

    public SecurityConfig(MyWebAuthenticationDetailsSource myWebAuthenticationDetailsSource) {
        this.myWebAuthenticationDetailsSource = myWebAuthenticationDetailsSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder encoder = (DelegatingPasswordEncoder) PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
        encoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        return encoder;
    }

    /**
     * 基于内存数据源构建一个用户测试自定义认证逻辑
     * @author lxlei
     * @date 2020/11/5 10:59
     * @return org.springframework.security.core.userdetails.UserDetailsService
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("shiloh")
                .password("123456")
                .roles("admin")
                .build()
        );
        return manager;
    }

    /**
     * 注入自定义的认证逻辑
     * @author lxlei
     * @date 2020/11/5 10:57
     * @return org.shiloh.provider.MyAuthenticationProvider
     */
    @Bean
    public MyAuthenticationProvider myAuthenticationProvider() {
        MyAuthenticationProvider myAuthenticationProvider = new MyAuthenticationProvider();
        myAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        myAuthenticationProvider.setUserDetailsService(userDetailsService());
        return myAuthenticationProvider;
    }

    /**
     * 注入管理AuthenticationProvider的管理器
     * @author lxlei
     * @date 2020/11/5 10:59
     * @return org.springframework.security.authentication.ProviderManager
     */
    @Bean
    public ProviderManager providerManager() {
        // 加入自定义的认证逻辑
        return new ProviderManager(Collections.singletonList(myAuthenticationProvider()));
    }

    /**
     * 认证授权相关配置
     * @author lxlei
     * @date 2020/11/5 10:16
     * @param http
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/captcha.png")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                // 用自定义的 MyWebAuthenticationDetailsSource 代替系统默认的 WebAuthenticationDetailsSource
                // MyWebAuthenticationDetailsSource会构建一个MyWebAuthenticationDetails
                // MyWebAuthenticationDetails构造器中有对于验证码校验的逻辑处理，
                // 通过MyWebAuthenticationDetails的isPassed字段可以获取验证码是否校验通过的结果
                .authenticationDetailsSource(myWebAuthenticationDetailsSource)
                .successHandler((req, res, authentication) -> {
                    // 登录成功后清除验证码
                    CaptchaUtil.clear(req);
                    res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    final PrintWriter writer = res.getWriter();
                    writer.write(new ObjectMapper()
                            .writeValueAsString(ResponseEntity.ok(authentication.getPrincipal()))
                    );
                    writer.flush();
                    writer.close();
                })
                .failureHandler((req, res, exception) -> {
                    // 登录失败也需要清除验证码
                    CaptchaUtil.clear(req);
                    res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    final PrintWriter writer = res.getWriter();
                    writer.write(new ObjectMapper()
                            .writeValueAsString(ResponseEntity.ok(exception.getMessage()))
                    );
                    writer.flush();
                    writer.close();
                })
                .permitAll()
                .and()
                .csrf()
                .disable();
    }
}
