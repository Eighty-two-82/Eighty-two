package com.springboot.springbootlogindemo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")  // 指定 MongoDB 集合名
public class User {

    @Id
    private String id;   // MongoDB 默认主键是 String (ObjectId)

    private String uname;
    private String password;

    // getter & setter
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }
    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
