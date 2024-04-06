package com.example.yhourownerproject.roles;

import java.util.HashMap;

public class Timekeeping {
    private HashMap<String, String> timeRecords;
    private String id;
    private String checkIn;
    private String checkOut;
    long timestamp = System.currentTimeMillis();

    public Timekeeping() {
        timeRecords = new HashMap<String, String>();
    }

    public HashMap<String, String> getTimeRecords() {
        return timeRecords;
    }

    public void setTimeRecords(HashMap<String, String> timeRecords) {
        this.timeRecords = timeRecords;
    }

    // Thêm phương thức để thêm check-in và check-out
    public void addTimeRecord(String id, String checkIn, String checkOut) {
        //HashMap<String, String> record = new HashMap<>();
        timeRecords.put("checkIn", checkIn);
        timeRecords.put("checkOut", checkOut);
        timeRecords.put(id, "newId");
    }

    public Timekeeping(String id, String checkIn, String checkOut) {
        this.id = id;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }
}
