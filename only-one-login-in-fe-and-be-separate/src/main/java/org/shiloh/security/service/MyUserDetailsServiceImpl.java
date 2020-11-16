package org.shiloh.security.service;

import org.shiloh.dao.UserDao;
import org.shiloh.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author lxlei
 * @date 2020/11/4 16:14
 * @description
 */
@Service
public class MyUserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    public MyUserDetailsServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 自定义登录时加载用户信息的逻辑：从数据库中根据传入的用户名获取用户信息
     * 查询出用户信息后框架会进行密码对比~
     * @author lxlei
     * @date 2020/11/4 16:15
     * @param username 用户名
     * @return org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("用户：[%s]不存在", username));
        }
        return user;
    }
}
