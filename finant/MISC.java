package com.example.finant;

public class MISC {

    String Mid, begin_date, user_ID;
    double amount, usage;

    public MISC() {}

    public MISC(String Mid, String begin_date, String user_ID, double amount, double usage) {
        this.Mid = Mid;
        this.begin_date = begin_date;
        this.user_ID = user_ID;
        this.amount = amount;
        this.usage = usage;
    }

    public String getMid() {
        return Mid;
    }

    public void setMid(String mid) {
        Mid = mid;
    }

    public String getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getUsage() {
        return usage;
    }

    public void setUsage(double usage) {
        this.usage = usage;
    }
}
