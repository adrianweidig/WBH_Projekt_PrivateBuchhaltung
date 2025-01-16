package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Date;

public abstract class Transaction {
    private double value;
    private TransactionCategory category;
    private BankAccount bankaccount;
    private Date date;

    public Transaction(double value, TransactionCategory category, BankAccount bankaccount, Date date) {
        this.value = value;
        this.category = category;
        this.bankaccount = bankaccount;
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public TransactionCategory getCategory() {
        return category;
    }

    public void setCategory(TransactionCategory category) {
        this.category = category;
    }

    public BankAccount getBankaccount() {
        return bankaccount;
    }

    public void setBankAccount(BankAccount bankaccount) {
        this.bankaccount = bankaccount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public abstract String getTyp();
}