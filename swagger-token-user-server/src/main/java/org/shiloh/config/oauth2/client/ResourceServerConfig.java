package org.shiloh.config.oauth2.client;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author lxlei
 * @date 2020/11/11 17:11
 * @description 资源服务器配置，{@link EnableResourceServer} 开启资源服务器的自动化配置
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final TokenStore tokenStore;

    public ResourceServerConfig(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    /**
     * 配置资源ID以及token存储策略
     * 这里配置好之后，会自动调用 JwtAccessTokenConverter 将 jwt 解析出来，
     * jwt 里边就包含了用户的基本信息，所以就不用远程校验 access_token 了。
     * @author lxlei
     * @date 2020/11/11 17:13
     * @param resources
     * @return void
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("RES_O1")
                .tokenStore(tokenStore);
    }

    /**
     * 认证授权相关配置
     * @author lxlei
     * @date 2020/11/11 17:13
     * @param http
     * @return void
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**")
                .hasRole("admin")
                .antMatchers("/user/**")
                .hasRole("user")
                // 放行swagger2资源
                .antMatchers("/swagger-ui.html", "/webjars/**", "/v2/**", "/swagger-resources/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf()
                .disable();
    }
}
