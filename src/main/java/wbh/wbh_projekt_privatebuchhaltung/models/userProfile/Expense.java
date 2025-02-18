package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import java.util.Date;

/**
 * Represents an expense transaction.
 * This class extends {@link Transaction} and categorizes transactions as expenses.
 */
public class Expense extends Transaction {

    /* -------------------------------- */
    /* ------ Constructor         ------ */
    /* -------------------------------- */

    /**
     * Constructs an Expense with an explicit ID.
     *
     * @param id          the unique identifier of the expense
     * @param value       the monetary value of the expense
     * @param category    the category of the transaction
     * @param bankaccount the associated bank account
     * @param date        the date of the expense
     * @param description a textual description of the expense
     */
    public Expense(int id, double value, TransactionCategory category, BankAccount bankaccount, Date date, String description) {
        super(id, value, category, bankaccount, date, description);
    }

    /**
     * Constructs an Expense without an explicit ID.
     *
     * @param value       the monetary value of the expense
     * @param category    the category of the transaction
     * @param bankaccount the associated bank account
     * @param date        the date of the expense
     * @param description a textual description of the expense
     */
    public Expense(double value, TransactionCategory category, BankAccount bankaccount, Date date, String description) {
        super(value, category, bankaccount, date, description);
    }

    /* -------------------------------- */
    /* ------ Overridden Methods ------ */
    /* -------------------------------- */

    /**
     * Returns the type of transaction.
     *
     * @return the string "Expense"
     */
    @Override
    public String getTyp() {
        return "Expense";
    }
}
