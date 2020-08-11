package com.example.traceusdemo;

import java.io.Serializable;

public class User implements Serializable {

    private String phone;
    private String status;
    private String email;

    public User() {}

    public User( String phone) {
        this.phone = phone;
    }

    public User(String phone, String status, String email) {
        this.phone = phone;
        this.status = status;
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}