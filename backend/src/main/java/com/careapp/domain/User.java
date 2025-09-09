package com.careapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String uname;
    private String password;

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUname() { return uname; }
    public void setUname(String uname) { this.uname = uname; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
