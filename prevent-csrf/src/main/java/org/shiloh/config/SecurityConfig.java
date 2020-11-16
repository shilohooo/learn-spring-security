package org.shiloh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.io.PrintWriter;

/**
 * @author lxlei
 * @date 2020/11/7 9:06
 * @description Spring Security Config
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder encoder = (DelegatingPasswordEncoder) PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
        encoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        return encoder;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/js/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("admin")
                .roles("admin");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
                // 登录成功后的处理
                .successHandler((req, res, authentication) -> {
                    res.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    final PrintWriter writer = res.getWriter();
                    writer.write(new ObjectMapper()
                            .writeValueAsString(ResponseEntity.ok(authentication.getPrincipal()))
                    );
                    writer.flush();
                    writer.close();
                })
                .failureHandler((req, res, exception) -> {
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
                // 关闭SpringSecurity默认启动的防御CSRF配置
                .csrf()
                // 在 Cookie 中将_csrf参数返回前端
                // 将服务端生成的随机数放在 Cookie 中，前端需要从 Cookie 中自己提取出来 _csrf 参数，然后拼接成参数传递给后端，
                // 单纯的将 Cookie 中的数据传到服务端是没用的。
                // 注意，通过 withHttpOnlyFalse 方法获取了 CookieCsrfTokenRepository 的实例，
                // 该方法会设置Cookie中的HttpOnly属性为 false，也就是允许前端通过js操作Cookie（否则没有办法获取到 _csrf参数的值）。
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
//                .disable();
    }
}
