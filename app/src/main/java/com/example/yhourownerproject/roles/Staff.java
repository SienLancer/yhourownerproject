package com.example.yhourownerproject.roles;

import java.util.List;

public class Staff {
    String id;
    private String name;
    private String dateOfBirth;
    private String address;
    private String phoneNumber;
    private String email;
    private String position;
    private String hourlySalary;
    private List<String> timekeeping;
    private String userNameS;
    private String passwordS;

    public Staff(){

    }
    public Staff(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Staff(String id, String name, String dateOfBirth, String address, String phoneNumber, String email,
                 String position, String hourlySalary) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.position = position;
        this.hourlySalary = hourlySalary;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHourlySalary() {
        return hourlySalary;
    }

    public void setHourlySalary(String hourlySalary) {
        this.hourlySalary = hourlySalary;
    }

    public List<String> getTimekeeping() {
        return timekeeping;
    }

    public void setTimekeeping(List<String> timekeeping) {
        this.timekeeping = timekeeping;
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
