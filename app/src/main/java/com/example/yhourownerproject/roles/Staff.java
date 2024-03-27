package com.example.yhourownerproject.roles;

public class Staff {
    private String name;
    private String userNameS;
    private String passwordS;

    public Staff(){

    }
    public Staff(String name) {
        this.name = name;
    }
    public Staff(String name, String userNameS, String passwordS) {
        this.name = name;
        this.userNameS = userNameS;
        this.passwordS = passwordS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
