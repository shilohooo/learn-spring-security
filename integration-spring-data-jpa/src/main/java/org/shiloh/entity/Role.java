package org.shiloh.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lxlei
 * @date 2020/11/4 15:34
 * @description
 */
@Entity
@Table(name = "sys_role")
@org.hibernate.annotations.Table(appliesTo = "sys_role", comment = "系统角色表")
@Setter
@Getter
@Accessors(chain = true)
public class Role implements Serializable {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint(20) comment '主键ID'")
    private Long id;

    /**
     * 角色名称
     */
    @Column(name = "name", columnDefinition = "varchar(50) comment '角色名称'")
    private String name;

    /**
     * 描述信息
     */
    @Column(name = "description", columnDefinition = "varchar(255) comment '描述信息'")
    private String description;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_role",
            joinColumns = {@JoinColumn(name = "role_id",
                    columnDefinition = "bigint(20) comment '角色ID（外键），关联角色表'")},
            inverseJoinColumns = {@JoinColumn(name = "user_id",
                    columnDefinition = "bigint(20) comment '用户ID（外键），关联系统用户表'")})
    private Set<User> users = new HashSet<>();

}
