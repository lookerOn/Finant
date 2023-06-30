package com.example.finant;

import com.google.firebase.Timestamp;

public class Expenses {
    long timeStamp;
    double EXP_Amount, EXPCV_Amount;
    String EXP_Des, EXP_date, currency,eid,user_ID, bid;

    public Expenses(){}

    public Expenses(double EXP_Amount, double EXPCV_Amount, String EXP_Des, String EXP_date, String currency, String eid, String user_ID, String bid, long timeStamp) {
        this.EXP_Amount = EXP_Amount;
        this.EXPCV_Amount = EXPCV_Amount;
        this.EXP_Des = EXP_Des;
        this.EXP_date = EXP_date;
        this.currency = currency;
        this.eid = eid;
        this.user_ID = user_ID;
        this.bid = bid;
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getEXP_Amount() {
        return EXP_Amount;
    }

    public void setEXP_Amount(double EXP_Amount) {
        this.EXP_Amount = EXP_Amount;
    }

    public double getEXPCV_Amount() {
        return EXPCV_Amount;
    }

    public void setEXPCV_Amount(double EXPCV_Amount) {
        this.EXPCV_Amount = EXPCV_Amount;
    }

    public String getEXP_Des() {
        return EXP_Des;
    }

    public void setEXP_Des(String EXP_Des) {
        this.EXP_Des = EXP_Des;
    }

    public String getEXP_date() {
        return EXP_date;
    }

    public void setEXP_date(String EXP_date) {
        this.EXP_date = EXP_date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }
}
