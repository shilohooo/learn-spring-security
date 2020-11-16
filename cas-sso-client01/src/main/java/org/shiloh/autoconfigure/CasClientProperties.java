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
@ConfigurationProperties(prefix = "cas.client")
@Data
public class CasClientProperties {

    /**
     * cas单点登录客户访问前缀：http/https://domain:port
     */
    private String prefix;

    /**
     * 登录url
     */
    private String loginUrl;

    /**
     * 注销登录相对路径
     */
    private String logoutRelativeUrl;

    /**
     * 注销登录url：prefix + logoutRelativeUrl
     */
    private String logoutUrl;
}
