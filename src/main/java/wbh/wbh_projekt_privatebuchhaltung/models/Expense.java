package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Date;

public class Expense extends Transaction {
    public Expense(double value, TransactionCategory category, BankAccount bankaccount, Date date) {
        super(value, category, bankaccount, date);
    }

    @Override
    public String getTyp() {
        return "Expense";
    }
}