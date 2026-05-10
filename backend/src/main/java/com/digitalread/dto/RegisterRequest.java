package com.digitalread.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String realName;
    private String visualImpairmentType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getVisualImpairmentType() {
        return visualImpairmentType;
    }

    public void setVisualImpairmentType(String visualImpairmentType) {
        this.visualImpairmentType = visualImpairmentType;
    }
}
