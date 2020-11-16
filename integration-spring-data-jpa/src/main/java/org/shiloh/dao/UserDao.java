package org.shiloh.dao;

import org.shiloh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lxlei
 * @date 2020/11/4 16:12
 * @description
 */
@Repository
public interface UserDao extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户信息
     * @author lxlei
     * @date 2020/11/4 16:12
     * @param username 用户系统帐号
     * @return org.shiloh.entity.User
     */
    User findByUsername(String username);
}
