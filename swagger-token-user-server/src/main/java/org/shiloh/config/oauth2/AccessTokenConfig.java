package org.shiloh.config.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author lxlei
 * @date 2020/11/11 16:40
 * @description
 */
@Configuration
public class AccessTokenConfig {

    /**
     * 配置oauth2 访问令牌的存储方案：将令牌信息放置于Jwt，做无状态登录
     * @author lxlei
     * @date 2020/11/11 16:40
     * @return org.springframework.security.oauth2.provider.token.TokenStore
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 提供了一个 JwtAccessTokenConverter，这个 JwtAccessTokenConverter 可以实现将用户信息和 JWT 进行转换
     * （将用户信息转为 jwt 字符串，或者从 jwt 字符串提取出用户信息）。
     * 另外，在 JWT 字符串生成的时候，需要一个签名，这个签名需要自己保存好。
     * @author lxlei
     * @date 2020/11/11 16:43
     * @return org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
     */
    @Bean
    JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("SHILOH595");
        return converter;
    }
}
