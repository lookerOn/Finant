package com.example.finant;

public class Budget {

    String budget_name,bcurrency,bid, begin_date,repeat_status,user_ID;
    double amount,usage, counter;

    public Budget(){}

    public Budget(String bid, String budget_name, String bcurrency, double amount, String begin_date, String repeat_status, String user_ID, long usage, double counter) {
        this.bid = bid;
        this.budget_name = budget_name;
        this.bcurrency = bcurrency;
        this.amount = amount;
        this.begin_date = begin_date;
        this.repeat_status = repeat_status;
        this.user_ID = user_ID;
        this.usage = usage;
        this.counter = counter;
    }

    public Budget(String budgid, String budget_Name, String budgetCur, String BeginDateString) {
        bid = budgid;
        budget_name = budget_Name;
        bcurrency = budgetCur;
        begin_date = BeginDateString;
    }

    public double getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public double getUsage() {
        return usage;
    }

    public void setUsage(double usage) {
        this.usage = usage;
    }

    public String getBudget_name() {
        return budget_name;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public void setBudget_name(String budget_name) {
        this.budget_name = budget_name;
    }

    public String getBcurrency() {
        return bcurrency;
    }

    public void setBcurrency(String bcurrency) {
        this.bcurrency = bcurrency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public String getRepeat_status() {
        return repeat_status;
    }

    public void setRepeat_status(String repeat_status) {
        this.repeat_status = repeat_status;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    @Override
    public String toString() {
        return budget_name;
    }

}
