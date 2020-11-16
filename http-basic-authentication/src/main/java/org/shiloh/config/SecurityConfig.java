package org.shiloh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lxlei
 * @date 2020/11/12 16:06
 * @description
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("admin")
                .password("admin")
                .roles("admin")
                .build()
        );
        return manager;
    }

    /**
     * Http摘要认证配置：服务器端随机字符串生成的配置
     * 关于nonce字符串的生成请参考：{@link DigestAuthenticationEntryPoint#commence(HttpServletRequest, HttpServletResponse,
     * AuthenticationException)}
     * @author lxlei
     * @date 2020/11/12 16:21
     * @return org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint
     */
    @Bean
    public DigestAuthenticationEntryPoint digestAuthenticationEntryPoint() {
        DigestAuthenticationEntryPoint entryPoint = new DigestAuthenticationEntryPoint();
        // 生成nonce时所需要的key
        entryPoint.setKey("SHILOH");
        // realm的名字
        entryPoint.setRealmName("SHILOH_REALM");
        // nonce的有效期（多长时间会发生变化）
        entryPoint.setNonceValiditySeconds(1000);
        return entryPoint;
    }

    /**
     * 配置一个处理前端请求的过滤器，具体请参考：{@link DigestAuthenticationFilter#doFilter(ServletRequest, ServletResponse,
     * FilterChain)}
     * @author lxlei
     * @date 2020/11/12 16:25
     * @return org.springframework.security.web.authentication.www.DigestAuthenticationFilter
     */
    @Bean
    public DigestAuthenticationFilter digestAuthenticationFilter() {
        DigestAuthenticationFilter filter = new DigestAuthenticationFilter();
        filter.setAuthenticationEntryPoint(digestAuthenticationEntryPoint());
        filter.setUserDetailsService(userDetailsService());
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .csrf()
                .disable()
                // 配置Http摘要认证
                // http摘要认证流程：
                // 1. 浏览器发出请求访问资源服务器上的某个接口
                // 2. 服务端接收到请求与后先返回 401，表示未认证，同时在响应头中携带 WWW-Authenticate 字段来描述认证形式。
                // 不同的是，这次服务端会计算出一个随机字符串，一同返回前端，这样可以防止重放攻击
                // （所谓重放攻击就是别人嗅探到你的摘要信息，把摘要当成密码一次次发送服务端，
                // 加一个会变化的随机字符串，生成的摘要信息就会变化，就可以防止重放攻击）
                // 同时，服务端返回的字段还有一个 qop，表示保护级别，auth 表示只进行身份验证；auth-int 表示还要校验内容
                // nonce 是服务端生成的随机字符串，这是一个经过 Base64 编码的字符串，经过解码可以发现，它是由过期时间和密钥组成的。
                // 在以后的请求中 nonce 会原封不动的再发回给服务端。
                // 3. 客户端选择一个算法，根据该算法计算出密码以及其他数据的摘要并发送到服务端
                // 4. 服务端根据客户端发送来的用户名，可以查询出用户密码，再根据用户密码可以计算出摘要信息，
                // 再将摘要信息和客户端发送来的摘要信息进行对比，就能确认用户身份。
                .exceptionHandling()
                .authenticationEntryPoint(digestAuthenticationEntryPoint())
                .and()
                .addFilter(digestAuthenticationFilter());
                // 开启SpringSecurity自带的Http Basic认证
                // HttpBasic 认证的流程：
                // 1. 浏览器发出请求访问资源服务器的某个接口。
                // 2. 服务端返回 401，表示未认证。同时在响应头中携带 WWW-Authenticate 字段来描述认证形式。
                // 3. 浏览器收到 401 响应之后，弹出对话框，要求用户输入用户名/密码，用户输入完用户名/密码之后，
                // 4. 浏览器会将之进行 Base64 编码，编码完成后，再发送到服务端。
                // 5. 服务端对浏览器传来的信息进行解码，并校验，当校验成功后给客户端作出响应。
//                .httpBasic();
    }
}
