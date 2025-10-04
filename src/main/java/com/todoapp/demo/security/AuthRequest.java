package com.todoapp.demo.security;

public class AuthRequest {
    private String username;
    private String email; // login sırasında null dönme durumu var
    private String password;

    // getter + setter

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
