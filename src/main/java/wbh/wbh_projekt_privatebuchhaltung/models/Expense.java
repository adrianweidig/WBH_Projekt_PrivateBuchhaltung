package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Date;

/**
 * The type Expense.
 */
public class Expense extends Transaction {
    /**
     * Instantiates a new Expense.
     *
     * @param value       the value
     * @param category    the category
     * @param bankaccount the bankaccount
     * @param date        the date
     */
    public Expense(int id,double value, TransactionCategory category, BankAccount bankaccount, Date date,String description) {
        super(id, value, category, bankaccount, date,description);
    }

    public Expense(double value, TransactionCategory category, BankAccount bankaccount, Date date,String description) {
        super(value, category, bankaccount, date,description);
    }

    @Override
    public String getTyp() {
        return "Expense";
    }
}