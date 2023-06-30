package com.example.finant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Expense {

    private String eid, EXP_Amount,EXPCV_Amount,currency,EXP_date,EXP_Des,bcurrency,budget_name, timeStamp;

    public Expense(){};

    public Expense(String expId, String expDt, String expAmount, String expDE, String expCur, String expCVAmount, String budgetnm, String budgetcur, String currenctime) {

        eid = expId;
        EXP_date = expDt;
        EXP_Amount = expAmount;
        EXP_Des = expDE;
        currency = expCur;
        EXPCV_Amount= expCVAmount;
        budget_name = budgetnm;
        bcurrency = budgetcur;
        timeStamp = currenctime;
    }


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getEXP_Amount() {
        return EXP_Amount;
    }

    public void setEXP_Amount(String EXP_Amount) {
        this.EXP_Amount = EXP_Amount;
    }

    public String getEXPCV_Amount() {
        return EXPCV_Amount;
    }

    public void setEXPCV_Amount(String EXPCV_Amount) {
        this.EXPCV_Amount = EXPCV_Amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEXP_date() {
        return EXP_date;
    }

    public void setEXP_date(String EXP_date) {
        this.EXP_date = EXP_date;
    }

    public String getEXP_Des() {
        return EXP_Des;
    }

    public void setEXP_Des(String EXP_Des) {
        this.EXP_Des = EXP_Des;
    }

    public String getBcurrency() {
        return bcurrency;
    }

    public void setBcurrency(String bcurrency) {
        this.bcurrency = bcurrency;
    }

    public String getBudget_name() {
        return budget_name;
    }

    public void setBudget_name(String budget_name) {
        this.budget_name = budget_name;
    }

}
