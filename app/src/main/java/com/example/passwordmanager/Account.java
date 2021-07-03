package com.example.passwordmanager;

public class Account{
    public String accountID;
    public String accountTitle;
    public String accountPassword;
    public int color;

    public Account() {}

    public Account(String accountID, String accountTitle, String accountPpassword, int color) {
        this.accountID = accountID;
        this.accountTitle = accountTitle;
        this.accountPassword = accountPpassword;
        this.color = color;
    }

    public Account(String accountTitle, String accountPpassword, int color) {
        this.accountTitle = accountTitle;
        this.accountPassword = accountPpassword;
        this.color = color;
    }
}