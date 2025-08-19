package com.springboot.springbootlogindemo.service;

import com.springboot.springbootlogindemo.domain.User;

public interface UserService {
    /**
     * 登录业务逻辑
     * @param uname Account name
     * @param password Password
     * @return
     */
    User loginService(String uname, String password);

    /**
     * 注册业务逻辑
     * @param user User object to register, the primary key uid in properties should be empty, if uid is not empty it may overwrite existing user
     * @return
     */
    User registService(User user);
}
