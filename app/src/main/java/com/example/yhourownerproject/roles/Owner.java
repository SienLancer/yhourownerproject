package com.example.yhourownerproject.roles;

public class Owner {
    private String id;
    private String name;
    private String email;
    private String shopID;
    private Integer role;
    private String password;

    public Owner(String id, String name, String email, String shopID, Integer role, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.shopID = shopID;
        this.role = role;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
