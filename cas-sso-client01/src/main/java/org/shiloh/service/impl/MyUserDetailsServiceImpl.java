package org.shiloh.service.impl;

import org.shiloh.dao.UserDao;
import org.shiloh.entity.User;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author lxlei
 * @date 2020/11/9 9:56
 * @description
 */
@Component
@Primary
public class MyUserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    public MyUserDetailsServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 加载用户信息逻辑
     * 既然是单点登录，也就是用户是在 CAS Server 上登录的，这里的 UserDetailsService 意义在哪里呢？
     * 用户虽然在 CAS Server 上登录，但是，登录成功之后，CAS Client 还是要获取用户的基本信息、角色等，以便做进一步的权限控制，
     * 所以，这里的 loadUserByUsername 方法中的参数，实际上就是从 CAS Server 上登录成功后获取到的用户名，
     * 拿着这个用户名，去数据库中查询用户的相关信心并返回，方便 CAS Client 在后续的鉴权中做进一步的使用
     * @author lxlei
     * @date 2020/11/9 9:57
     * @param username 登录用户名
     * @return org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userDao.findByUsername(username);
        if (user == null) {
            throw new AuthenticationServiceException(String.format("用户[%s]不存在", username));
        }
        return user;
    }
}
