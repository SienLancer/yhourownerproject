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
    private String password;
    private String checkIn;


    public Staff(){

    }
    public Staff(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Staff(String id, String name, String checkIn) {
        this.id = id;
        this.name = name;
        this.checkIn = checkIn;
    }

    public Staff(String id, String name, String dateOfBirth, String address, Integer phoneNumber, String email, String position, Integer hourlySalary, Integer role, String shopID, String password) {
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
        this.password = password;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
