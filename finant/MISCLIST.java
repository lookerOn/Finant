package com.example.finant;

public class MISCLIST {

    String Mid,user_ID, MISCEXP_Des, MISCEXP_date, MISCtimeStamp, MiscExpID, currency;
    double MISCEXP_Amount;

    public MISCLIST() {}

    public MISCLIST(String Mid, String user_ID, String MISCEXP_Des, String MISCEXP_date, String MISCtimeStamp, String MiscExpID, String currency, double MISCEXP_Amount) {
        this.Mid = Mid;
        this.user_ID = user_ID;
        this.MISCEXP_Des = MISCEXP_Des;
        this.MISCEXP_date = MISCEXP_date;
        this.MISCtimeStamp = MISCtimeStamp;
        this.MiscExpID = MiscExpID;
        this.currency = currency;
        this.MISCEXP_Amount = MISCEXP_Amount;
    }

    public String getMid() {
        return Mid;
    }

    public void setMid(String mid) {
        Mid = mid;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getMISCEXP_Des() {
        return MISCEXP_Des;
    }

    public void setMISCEXP_Des(String MISCEXP_Des) {
        this.MISCEXP_Des = MISCEXP_Des;
    }

    public String getMISCEXP_date() {
        return MISCEXP_date;
    }

    public void setMISCEXP_date(String MISCEXP_date) {
        this.MISCEXP_date = MISCEXP_date;
    }

    public String getMISCtimeStamp() {
        return MISCtimeStamp;
    }

    public void setMISCtimeStamp(String MISCtimeStamp) {
        this.MISCtimeStamp = MISCtimeStamp;
    }

    public String getMiscExpID() {
        return MiscExpID;
    }

    public void setMiscExpID(String miscExpID) {
        MiscExpID = miscExpID;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getMISCEXP_Amount() {
        return MISCEXP_Amount;
    }

    public void setMISCEXP_Amount(double MISCEXP_Amount) {
        this.MISCEXP_Amount = MISCEXP_Amount;
    }
}
