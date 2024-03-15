package com.example.yhourownerproject.roles;

public class Staff {
    private String userNameS;
    private String passwordS;

    public Staff(){

    }

    public Staff(String userNameS, String passwordS) {
        this.userNameS = userNameS;
        this.passwordS = passwordS;
    }

    public String getUserNameS() {
        return userNameS;
    }

    public void setUserNameS(String userNameS) {
        this.userNameS = userNameS;
    }

    public String getPasswordS() {
        return passwordS;
    }

    public void setPasswordS(String passwordS) {
        this.passwordS = passwordS;
    }
}
