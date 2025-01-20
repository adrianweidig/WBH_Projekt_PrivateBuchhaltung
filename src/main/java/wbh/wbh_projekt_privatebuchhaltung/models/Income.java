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
    public Income(int id, double value, TransactionCategory category, BankAccount bankAccount, Date date, String description) {
        super(id, value, category, bankAccount, date, description);
    }

    @Override
    public String getTyp() {
        return "Income";
    }
}