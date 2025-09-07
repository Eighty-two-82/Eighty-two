package com.springboot.springbootlogindemo.repository;

import com.springboot.springbootlogindemo.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends MongoRepository<User, String> {
    // Find user by username
    User findByUname(String uname);

    // Find user by username and password
    User findByUnameAndPassword(String uname, String password);
}
