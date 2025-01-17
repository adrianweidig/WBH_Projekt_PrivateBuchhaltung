package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Date;

/**
 * The type Income.
 */
public class Income extends Transaction {
    /**
     * Instantiates a new Income.
     *
     * @param value       the value
     * @param category    the category
     * @param bankAccount the bank account
     * @param date        the date
     */
    public Income(double value, TransactionCategory category, BankAccount bankAccount, Date date) {
        super(value, category, bankAccount, date);
    }

    @Override
    public String getTyp() {
        return "Income";
    }
}