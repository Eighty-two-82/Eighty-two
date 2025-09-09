package com.careapp.service;

import com.careapp.domain.User;

public interface UserService {
    User loginService(String uname, String password);
    User registService(User user);
}
