package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a financial transaction.
 * This is an abstract base class for different transaction types like income and expenses.
 */
public abstract class Transaction {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private int id;
    private double value;
    private TransactionCategory category;
    private BankAccount bankAccount;
    private Date date;
    private String description;

    /* -------------------------------- */
    /* ------ Constructors        ------ */
    /* -------------------------------- */

    /**
     * Instantiates a new Transaction without an ID.
     *
     * @param value       the transaction amount
     * @param category    the transaction category
     * @param bankAccount the associated bank account
     * @param date        the transaction date
     * @param description the transaction description
     */
    protected Transaction(double value, TransactionCategory category, BankAccount bankAccount, Date date, String description) {
        this.value = value;
        this.category = category;
        this.bankAccount = bankAccount;
        this.date = date;
        this.description = description;
    }

    /**
     * Instantiates a new Transaction with an ID.
     *
     * @param id          the transaction ID
     * @param value       the transaction amount
     * @param category    the transaction category
     * @param bankAccount the associated bank account
     * @param date        the transaction date
     * @param description the transaction description
     */
    protected Transaction(int id, double value, TransactionCategory category, BankAccount bankAccount, Date date, String description) {
        this.id = id;
        this.value = value;
        this.category = category;
        this.bankAccount = bankAccount;
        this.date = date;
        this.description = description;
    }

    /* -------------------------------- */
    /* ------ Getters & Setters  ------ */
    /* -------------------------------- */

    /**
     * Gets the transaction value.
     *
     * @return the transaction amount
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Sets the transaction value.
     *
     * @param value the transaction amount
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Gets the transaction category.
     *
     * @return the category
     */
    public TransactionCategory getCategory() {
        return this.category;
    }

    /**
     * Sets the transaction category.
     *
     * @param category the category
     */
    public void setCategory(TransactionCategory category) {
        this.category = category;
    }

    /**
     * Gets the associated bank account.
     *
     * @return the bank account
     */
    public BankAccount getBankAccount() {
        return this.bankAccount;
    }

    /**
     * Sets the associated bank account.
     *
     * @param bankAccount the bank account
     */
    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    /**
     * Gets the transaction date.
     *
     * @return the date
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Sets the transaction date.
     *
     * @param date the transaction date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the transaction description.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the transaction description.
     *
     * @param description the transaction description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /* -------------------------------- */
    /* ------ Abstract Method    ------ */
    /* -------------------------------- */

    /**
     * Gets the type of transaction.
     * Must be implemented by subclasses.
     *
     * @return the transaction type
     */
    public abstract String getTyp();

    /* -------------------------------- */
    /* ------ toString Method    ------ */
    /* -------------------------------- */

    /**
     * Returns a formatted string representation of the transaction.
     *
     * @return the transaction details as a formatted string
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

        result.append("Transaction Details:\n");
        result.append("  ID: ").append(this.id).append("\n");
        result.append("  Value: ").append(String.format("%.2f", this.value)).append("\n");
        result.append("  Category: ").append(this.category.getName()).append("\n");
        result.append("  Bank Account: ").append(this.bankAccount.getName()).append(" (ID: ").append(this.bankAccount.getId()).append(")\n");
        result.append("  Date: ").append(dateFormatter.format(this.date)).append("\n");
        result.append("  Description: ").append(this.description).append("\n");

        return result.toString();
    }
}
