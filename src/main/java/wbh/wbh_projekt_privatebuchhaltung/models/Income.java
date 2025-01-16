package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Date;

public class Income extends Transaction {
    public Income(double value, TransactionCategory category, BankAccount bankAccount, Date date) {
        super(value, category, bankAccount, date);
    }

    @Override
    public String getTyp() {
        return "Income";
    }
}