package com.example.yhourownerproject.roles;

import java.util.List;

public class Staff {
    String id;
    private String name;
    private String dateOfBirth;
    private String address;
    private Integer phoneNumber;
    private String email;
    private String position;
    private Integer hourlySalary;
    private Integer role;
    private String shopID;
    private String userNameS;
    private String passwordS;
    private Timekeeping timekeeping;

    public Staff(){

    }
    public Staff(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Staff(String id, String name, String dateOfBirth, String address, Integer phoneNumber, String email, String position, Integer hourlySalary, Integer role, String shopID, String userNameS, String passwordS, Timekeeping timekeeping) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.position = position;
        this.hourlySalary = hourlySalary;
        this.role = role;
        this.shopID = shopID;
        this.userNameS = userNameS;
        this.passwordS = passwordS;
        this.timekeeping = timekeeping;
    }

    public Timekeeping getTimekeeping() {
        return timekeeping;
    }

    public void setTimekeeping(Timekeeping timekeeping) {
        this.timekeeping = timekeeping;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getHourlySalary() {
        return hourlySalary;
    }

    public void setHourlySalary(Integer hourlySalary) {
        this.hourlySalary = hourlySalary;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
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
