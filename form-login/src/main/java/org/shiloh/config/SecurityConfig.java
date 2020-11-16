package org.shiloh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    /**
     * Spring Security认证授权相关配置
     * 关于登录页面和登录接口地址的配置请看：
     * @see AbstractAuthenticationFilterConfigurer 类的无参构造器
     * @see AbstractAuthenticationFilterConfigurer#init(HttpSecurityBuilder)
     * 关于登录表单用户名和密码输入框name属性值的配置请看：
     * @see FormLoginConfigurer#FormLoginConfigurer()
     * 关于使用HttpServletRequest根据用户名和密码输入框name属性值获取用户输入的登录信息请看：
     * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
     * @author lxlei
     * @date 2020/11/3 16:10
     * @param http
     */
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
                // 单独配置登录接口的请求地址，如果没有为loginProcessingUrl配置值的话它的默认值将与loginPage一致
                // 如果loginPage也没有配置值的话，那么loginPage和loginProcessingUrl的值都默认为/login
                .loginProcessingUrl("/do-login")
                // 开放表单登录页面资源的访问权限，无需认证就能访问
                // 配置登录表单用户名和密码输入框的name属性值
                .usernameParameter("user")
                .passwordParameter("pass")
                // 配置登录成功后的回调
                // 在 Spring Security 中，和登录成功重定向 URL 相关的方法有两个：
                // defaultSuccessUrl
                // successForwardUrl
                // 这两个咋看没什么区别，实际上内藏乾坤。配置的时候，defaultSuccessUrl 和 successForwardUrl 只需要配置一个即可，
                // 两个的区别如下：
                // defaultSuccessUrl(String defaultSuccessUrl) 如果在defaultSuccessUrl中指定登录成功的跳转页面为/index，
                // 此时分两种情况，如果你是直接在浏览器中输入的登录地址，登录成功后，就直接跳转到/index，
                // 如果你是在浏览器中输入了其他地址，例如 http://localhost:8080/hello，结果因为没有登录，又重定向到登录页面，
                // 此时登录成功后，就不会来到 /index ，而是来到 /hello 页面。
                // defaultSuccessUrl(String defaultSuccessUrl, boolean alwaysUse) 第二个参数如果不设置默认为 false，
                // 也就是我们上面的的情况，如果手动设置第二个参数为 true，则 defaultSuccessUrl 的效果和 successForwardUrl 一致。
                // successForwardUrl 表示不管你是从哪里来的，登录后一律跳转到 successForwardUrl 指定的地址。
                // /index ，你在浏览器地址栏输入 http://localhost:8080/hello，结果因为没有登录，重定向到登录页面，
                // 当你登录成功之后，就会服务端跳转到/index页面；或者你直接就在浏览器输入了登录页面地址，登录成功后也是来到 /index。
                //相关配置如下：
                .defaultSuccessUrl("/index")
//                .successForwardUrl("/index")
                // 登录失败回调配置
                // 与登录成功相似，登录失败也是有两个方法：
                // failureForwardUrl(String forwardUrl)
                // failureUrl(String authenticationFailureUrl)
                // 这两个方法在设置的时候也是设置一个即可。failureForwardUrl 是登录失败之后会发生服务端跳转，
                // failureUrl 则在登录失败之后，会发生重定向。
                .failureForwardUrl("/login-failed")
//                .failureUrl("/login-failed")
                // 开放登录页面
                .permitAll()
                .and()
                // 注销登录配置
                .logout()
                // 配置注销登录接口的请求地址，默认为：/logout
//                .logoutUrl("/logout")
                // logoutRequestMatcher可以配置注销登录接口的请求地址与请求方式，默认请求方式为GET
                // 此配置和logoutUrl(String logoutUrl) 任意配置一个即可
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                // 配置注销成功后要跳转的地址
                .logoutSuccessUrl("/logged-out")
                // 注销成功后清楚cookie
                .deleteCookies()
                // clearAuthentication 和 invalidateHttpSession 分别表示清除认证信息和使 HttpSession 失效，默认就会清除
//                .clearAuthentication(true)
//                .invalidateHttpSession(true)
                // 开放注销配置的跳转地址
                .permitAll()
                .and()
                // 禁用csrf
                .csrf()
                .disable();
    }
}
