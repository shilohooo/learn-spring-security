package org.shiloh.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.shiloh.dao.UserDao;
import org.shiloh.entity.Role;
import org.shiloh.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lxlei
 * @date 2020/11/4 16:53
 * @description 添加2个测试用户
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDao userDao;

    @Test
    public void saveAdminUserTest() {
        // 添加管理员用户
        User admin = new User();
        admin.setUsername("admin")
                .setNickname("超级管理员")
                .setPassword(passwordEncoder.encode("admin"))
                .setAccountNonExpired(true)
                .setAccountNonLocked(true)
                .setCredentialsNonExpired(true)
                .setEnabled(true);
        Set<Role> adminRoles = new HashSet<>();
        Role adminRole = new Role();
        adminRole.setName("admin")
                .setDescription("超级管理员");
        adminRoles.add(adminRole);
        admin.setRoles(adminRoles);
        userDao.save(admin);
    }

    @Test
    public void saveNormalUserTest() {
        // 添加普通用户
        User normalUser = new User();
        normalUser.setUsername("normal")
                .setNickname("shiloh")
                .setPassword(passwordEncoder.encode("123456"))
                .setAccountNonExpired(true)
                .setAccountNonLocked(true)
                .setCredentialsNonExpired(true)
                .setEnabled(true);
        Set<Role> normalUserRoles = new HashSet<>();
        Role normalUserRole = new Role();
        normalUserRole.setName("user")
                .setDescription("普通用户");
        normalUserRoles.add(normalUserRole);
        normalUser.setRoles(normalUserRoles);
        userDao.save(normalUser);
    }

}
