package org.shiloh.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lxlei
 * @date 2020/11/7 17:03
 * @description
 */
@Component
@ConfigurationProperties(prefix = "cas.server")
@Data
public class CasServerProperties {

    /**
     * cas单点登录服务器访问路径前缀：https://domain:port/cas
     */
    private String prefix;

    /**
     * 单点登录url
     */
    private String loginUrl;

    /**
     * 注销登录url
     */
    private String logoutUrl;
}
