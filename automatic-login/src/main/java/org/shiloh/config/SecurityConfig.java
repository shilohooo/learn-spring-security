package org.shiloh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * @author lxlei
 * @date 2020/11/4 17:20
 * @description spring security config
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;

    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 加密类配置
     * @author lxlei
     * @date 2020/11/4 17:28
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
     * 持久化token配置，防止用户多端登录
     * @author lxlei
     * @date 2020/11/5 9:06
     * @return org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl
     */
    @Bean
    public JdbcTokenRepositoryImpl jdbcTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    /**
     * 基于内存数据源构建一个用户测试自动登录功能
     * @author lxlei
     * @date 2020/11/4 17:26
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
     * Spring Security 认证授权配置
     * 关于设置rememberMe令牌加密key请参考：
     * @see TokenBasedRememberMeServices#onLoginSuccess(HttpServletRequest, HttpServletResponse, Authentication)
     * 关于默认key的生成请参考：
     * @see RememberMeConfigurer 这个类里面的getKey()方法
     * 关于记住我功能请参考：
     * @see RememberMeAuthenticationFilter#doFilter(ServletRequest, ServletResponse, FilterChain)
     * 关于持久化自动登录令牌的功能请参考：
     * @see org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
     * 上面这个类中的onLoginSuccess()方法
     * 关于如何防止用户多端登录的功能请参考：
     * @see org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
     * 上面这个类的processAutoLoginCookie()方法
     *
     * authenticated和fullyAuthenticated的区别：
     * authenticated包含自动登录，fullyAuthenticated则不包含
     * @author lxlei
     * @date 2020/11/4 17:22
     * @param http
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 配置只有通过自动登录认证才能访问的接口
                .antMatchers("/test-remember-me")
                .rememberMe()
                // 配置必须是通过用户名和密码登录认证后才能访问的接口
                // 如果用户是通过自动登录认证的，访问此接口则会跳转到登录页面
                .antMatchers("/admin-msg")
                .fullyAuthenticated()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .permitAll()
                .and()
                // 开启记住我功能，下次访问系统时自动登录
                .rememberMe()
                // 设置用于生成cookie中的rememberMe值时，加密token签名要用到的key
                // 这个key如果不设置的话则是一个随机的UUID，这样会导致之前的rememberMe令牌失效
                // 如果不想记住我令牌失效的话可以手动指定一个key
                .key("SHILOH595")
                // token持久化，将自动登录令牌存储到数据库中，可防止用户多端登录
                // 在持久化令牌中，新增了两个经过 MD5 散列函数计算的校验参数，一个是 series，另一个是 token。
                // 其中，series 只有当用户在使用用户名/密码登录时，才会生成或者更新，而 token 只要有新的会话，就会重新生成，
                // 这样就可以避免一个用户同时在多端登录，就像手机 QQ ，一个手机上登录了，就会踢掉另外一个手机的登录，
                // 这样用户就会很容易发现账户是否泄漏
                .tokenRepository(jdbcTokenRepository())
                .and()
                .csrf()
                .disable();
    }

//    public static void main(String[] args) {
//        // cookie中的rememberMe值，将其进行Base64解码
//        final String decode = new String(Base64.getDecoder().decode("YWRtaW46MTYwNTY5MTkzOTk5MzpmZTNmNGJkNGFhMDZmNzFhMmMwMjg2ZGYxNWMzMDJjZQ"),
//                StandardCharsets.UTF_8);
//        // 解码得到的值示例：admin:1605691939993:fe3f4bd4aa06f71a2c0286df15c302ce
//        // 可以看到，上面这段 Base64 字符串解码后实际上用:隔开，分成了三部分：
//        // 第一段是用户名，这个无需质疑。
//        // 第二段看起来是一个时间戳，通过在线工具或者 Java 代码解析后发现，这是一个两周后的数据。
//        // 第三段是使用 MD5 散列函数算出来的值，他的明文格式是 username + ":" + tokenExpiryTime + ":" + password + ":" + key，
//        // 最后的 key 是一个散列盐值，可以用来防治令牌被修改。
//        System.out.println("decode = " + decode);
//    }
}
