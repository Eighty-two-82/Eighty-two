package com.springboot.springbootlogindemo.domain;

import javax.persistence.*;

@Table(name = "users")
@Entity
public class User {
    // 注意属性名要与数据表中的字段名一致
    // Primary key auto-increment int(10) corresponds to long
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long uid;

    // Username property varchar corresponds to String
    private String uname;

    // Password property varchar corresponds to String
    private String password;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
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
