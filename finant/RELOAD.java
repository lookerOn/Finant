package com.example.finant;

public class RELOAD {

    private double Reload_Amount;
    private String date,  rid, timeStamp,user_ID,wallet_currency;

    public RELOAD(){}

    public RELOAD(double Reload_Amount, String date, String rid, String timeStamp, String user_ID, String wallet_currency) {
        this.Reload_Amount = Reload_Amount;
        this.date = date;
        this.rid = rid;
        this.timeStamp = timeStamp;
        this.user_ID = user_ID;
        this.wallet_currency = wallet_currency;
    }

    public RELOAD(double amount, String dtte, String rldid, String timestamp, String wcurr) {
        Reload_Amount = amount;
        date = dtte;
        rid = rldid;
        timeStamp = timestamp;
        wallet_currency = wcurr;
    }

    public double getReload_Amount() {
        return Reload_Amount;
    }

    public void setReload_Amount(double reload_Amount) {
        Reload_Amount = reload_Amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getWallet_currency() {
        return wallet_currency;
    }

    public void setWallet_currency(String wallet_currency) {
        this.wallet_currency = wallet_currency;
    }
}
