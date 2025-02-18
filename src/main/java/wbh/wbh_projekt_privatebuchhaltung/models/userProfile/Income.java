package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import java.util.Date;

/**
 * Represents an income transaction within the financial system.
 * An income is a type of {@link Transaction} that increases the balance of an associated {@link BankAccount}.
 */
public class Income extends Transaction {

    /* -------------------------------- */
    /* ------ Constructors       ------ */
    /* -------------------------------- */

    /**
     * Constructs an Income transaction with an explicit ID.
     *
     * @param id          the unique identifier of the transaction
     * @param value       the monetary value of the income
     * @param category    the category of the income
     * @param bankAccount the associated bank account
     * @param date        the date of the transaction
     * @param description a brief description of the income
     */
    public Income(int id, double value, TransactionCategory category, BankAccount bankAccount, Date date, String description) {
        super(id, value, category, bankAccount, date, description);
    }

    /**
     * Constructs an Income transaction without an explicit ID.
     *
     * @param value       the monetary value of the income
     * @param category    the category of the income
     * @param bankAccount the associated bank account
     * @param date        the date of the transaction
     * @param description a brief description of the income
     */
    public Income(double value, TransactionCategory category, BankAccount bankAccount, Date date, String description) {
        super(value, category, bankAccount, date, description);
    }

    /* -------------------------------- */
    /* ------ Business Logic     ------ */
    /* -------------------------------- */

    /**
     * Returns the transaction type as a string.
     *
     * @return "Income" indicating this transaction is an income
     */
    @Override
    public String getTyp() {
        return "Income";
    }
}
