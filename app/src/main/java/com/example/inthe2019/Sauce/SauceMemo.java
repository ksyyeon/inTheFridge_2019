package com.example.inthe2019.Sauce;

public class SauceMemo {
    String name;
    String date;
    String chk;

    public SauceMemo(String name, String date, String chk) {
        this.name = name;
        this.date = date;
        this.chk = chk;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getChk() {
        return chk;
    }
}