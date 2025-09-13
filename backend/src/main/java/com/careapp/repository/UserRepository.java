package com.careapp.repository;

import com.careapp.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Query methods based on username (backward compatibility)
    User findByUname(String uname);
    User findByUnameAndPassword(String uname, String password);
    
    // Query methods based on email (frontend usage)
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);
}
