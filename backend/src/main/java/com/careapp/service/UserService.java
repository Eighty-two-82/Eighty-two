package com.careapp.service;

import com.careapp.domain.User;

public interface UserService {
    // Login services
    User loginService(String uname, String password);
    User loginByEmailService(String email, String password);

    // Registration service
    User registerService(User user);
}
