package org.shiloh.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lxlei
 * @date 2020/11/12 17:41
 * @description 访问控制测试
 */
@RestController
public class AccessControlTestController {

    /**
     * 需要用户名为“shiloh”或者“admin”才可以访问此接口
     * PreAuthorize(表达式)，根据给定的SpEL表达式的结果来确认授权是否成功
     * @author lxlei
     * @date 2020/11/12 17:48
     * @return java.lang.String
     */
    @GetMapping("/hello")
    @PreAuthorize("principal.username.equals('shiloh') || principal.username.equals('admin')")
    public String sayHello() {
        return "Hello";
    }

    /**
     * 需要用户具有“user”角色才可以访问此接口
     * {@link PreAuthorize}
     * {@link org.springframework.security.access.expression.SecurityExpressionRoot#hasRole(String)}
     * 具有给定角色的用户才能成功授权
     * @author lxlei
     * @date 2020/11/12 17:43
     * @return java.lang.String
     */
    @GetMapping("/user/hello")
    @PreAuthorize("hasRole('user')")
    public String helloUser() {
        return "Hello user";
    }

    /**
     * 需要用户具有“admin”角色才可以访问的接口
     * @author lxlei
     * @date 2020/11/12 17:52
     * @return java.lang.String
     */
    @GetMapping("/admin/hello")
    @PreAuthorize("hasRole('admin')")
    public String helloAdmin() {
        return "Hello admin";
    }

    /**
     * 需要用户具有“user”角色才可以访问的接口
     * Secured({"ROLE_user"}) 表示方法访问该资源的用户必须具备 user 角色，注意 user 角色需要加上 ROLE_ 前缀。
     * 具体请参考：{@link Secured}
     * @author lxlei
     * @date 2020/11/12 17:54
     * @return java.lang.String
     */
    @GetMapping("/secured/user")
    @Secured({"ROLE_user"})
    public String testSecuredUserRole() {
        return "secured user role";
    }

    /**
     * 对请求参数进行校验
     * PreAuthorize("#age <= 100") 表示该方法的请求参数age的值必须符合配置
     * @author lxlei
     * @date 2020/11/12 17:57
     * @return java.lang.String
     */
    @GetMapping("/request-params-test")
    @PreAuthorize("#age <= 100")
    public String requestParamsTest(Integer age) {
        return String.valueOf(age);
    }

    // 使用过滤注解进行访问控制

    /**
     * 返回以2结尾的字符串内容
     * {@link PostFilter} 对指定对象进行过滤，这里只返回后缀为 2 的元素，filterObject 表示要过滤的元素对象。
     * @author lxlei
     * @date 2020/11/13 10:52
     * @return java.util.List<java.lang.String>
     */
    @GetMapping("/user/list")
    @PostFilter("filterObject.lastIndexOf('2') != -1")
    public List<String> getUserList() {
        List<String> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add("shiloh" + i);
        }
        return users;
    }

    /**
     * 此方法的ages参数接收的list里面的数字值如果有余数，则不会被添加到list中
     * {@link PreFilter#filterTarget()} 可以指定要过滤的对象，如果方法接收参数有多个
     * 由于有两个集合，因此使用 filterTarget 指定过滤对象。
     * @author lxlei
     * @date 2020/11/13 10:53
     * @param ages
     */
    @PostMapping("/print-all-params")
    @PreFilter(filterTarget = "ages", value = "filterObject % 2 == 0")
    public void printAllParams(@RequestBody List<Integer> ages) {
        System.out.println("ages = " + ages);
    }

}
