package com.example.finant;

public class RECEIVE {
    private double Transfer_Amount;
    private String date, tid, timeStamp,wallet_currency,sender_user_ID;

    public RECEIVE(){}

    public RECEIVE(double Transfer_Amount, String date, String sender_user_ID, String tid, String timeStamp, String wallet_currency) {
        this.Transfer_Amount = Transfer_Amount;
        this.date = date;
        this.tid = tid;
        this.timeStamp = timeStamp;
        this.wallet_currency = wallet_currency;
        this.sender_user_ID = sender_user_ID;
    }

    public double getTransfer_Amount() {
        return Transfer_Amount;
    }

    public void setTransfer_Amount(double transfer_Amount) {
        Transfer_Amount = transfer_Amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getWallet_currency() {
        return wallet_currency;
    }

    public void setWallet_currency(String wallet_currency) {
        this.wallet_currency = wallet_currency;
    }

    public String getSender_user_ID() {
        return sender_user_ID;
    }

    public void setSender_user_ID(String sender_user_ID) {
        this.sender_user_ID = sender_user_ID;
    }
}
