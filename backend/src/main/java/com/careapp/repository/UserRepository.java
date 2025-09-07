package com.careapp.repository;

import com.careapp.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUname(String uname);
    User findByUnameAndPassword(String uname, String password);
}
