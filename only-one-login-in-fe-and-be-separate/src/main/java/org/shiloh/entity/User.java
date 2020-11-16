package org.shiloh.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author lxlei
 * @date 2020/11/4 15:39
 * @description
 */
@Entity
@Table(name = "sys_user")
@org.hibernate.annotations.Table(appliesTo = "sys_user", comment = "系统用户表")
@Setter
@Getter
@Accessors(chain = true)
public class User implements UserDetails, Serializable {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint(20) comment '主键ID'")
    private Long id;

    /**
     * 系统帐号
     */
    @Column(name = "username", columnDefinition = "varchar(50) comment '系统帐号'")
    private String username;

    /**
     * 用户姓名
     */
    @Column(name = "nickname", columnDefinition = "varchar(50) comment '用户姓名'")
    private String nickname;

    /**
     * 登录密码
     */
    @Column(name = "password", columnDefinition = "varchar(500) comment '登录密码'")
    private String password;

    // 描述用户的状态字段 start

    /**
     * 帐号是否未过期，1 = 是，0 = 否
     */
    @Column(name = "account_non_expired", columnDefinition = "tinyint(1) comment '帐号是否未过期，1 = 是，0 = 否'")
    private Boolean accountNonExpired;

    /**
     * 帐号是否未锁定，1 = 是，0 = 否
     */
    @Column(name = "account_non_locked", columnDefinition = "tinyint(1) comment '帐号是否未锁定，1 = 是，0 = 否'")
    private Boolean accountNonLocked;

    /**
     * 密码是否未过期，1 = 是，0 = 否
     */
    @Column(name = "credentials_non_expired", columnDefinition = "tinyint(1) comment '密码是否未过期，1 = 是，0 = 否'")
    private Boolean credentialsNonExpired;

    /**
     * 帐号是否启用，1 = 是，0 = 禁用
     */
    @Column(name = "enabled", columnDefinition = "tinyint(1) comment '帐号是否启用，1 = 是，0 = 禁用'")
    private Boolean enabled;

    // 描述用户的状态字段 end

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "sys_user_role",
            joinColumns = {@JoinColumn(name = "user_id",
                    columnDefinition = "bigint(20) comment '用户ID（外键），关联系统用户表'")},
            inverseJoinColumns = {@JoinColumn(name = "role_id",
                    columnDefinition = "bigint(20) comment '角色ID（外键），关联角色表'")})
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    /**
     * 获取权限方法：将roles中的角色遍历并把每个角色的名称加上ROLE_前缀然后添加到一个集合中
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", role.getName())));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(getUsername(), user.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }
}
