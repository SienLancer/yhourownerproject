package com.example.yhourownerproject.roles;

public class Owner {
    private String idO;
    private String userNameO;
    private String passwordO;

    public Owner(String idO, String userNameO, String passwordO) {
        this.idO = idO;
        this.userNameO = userNameO;
        this.passwordO = passwordO;
    }

    public String getIdO() {
        return idO;
    }

    public void setIdO(String idO) {
        this.idO = idO;
    }

    public String getUserNameO() {
        return userNameO;
    }

    public void setUserNameO(String userNameO) {
        this.userNameO = userNameO;
    }

    public String getPasswordO() {
        return passwordO;
    }

    public void setPasswordO(String passwordO) {
        this.passwordO = passwordO;
    }
}
