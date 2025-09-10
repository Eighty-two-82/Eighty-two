package com.careapp.service;

import com.careapp.domain.User;

public interface UserService {
    // 登录
    User loginService(String uname, String password);

    // 注册
    User registerService(User user);
}
