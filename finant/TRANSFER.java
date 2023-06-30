package com.example.finant;

public class TRANSFER {

    private double Transfer_Amount;
    private String date,  tid, timeStamp,user_ID,wallet_currency,receiver_ID;

    public TRANSFER(){}

    public TRANSFER(double transfer_Amount, String date, String tid, String timeStamp, String user_ID, String wallet_currency, String receiver_ID) {
        Transfer_Amount = transfer_Amount;
        this.date = date;
        this.tid = tid;
        this.timeStamp = timeStamp;
        this.user_ID = user_ID;
        this.wallet_currency = wallet_currency;
        this.receiver_ID = receiver_ID;
    }

    public TRANSFER(double amount, String dtre, String rceivid, String trnid, String timestamp, String wcurr) {
        Transfer_Amount = amount;
        date = dtre;
        receiver_ID = rceivid;
        tid = trnid;
        timeStamp = timestamp;
        wallet_currency = wcurr;
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

    public String getReceiver_ID() {
        return receiver_ID;
    }

    public void setReceiver_ID(String receiver_ID) {
        this.receiver_ID = receiver_ID;
    }
}
