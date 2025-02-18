package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a bank account with an ID, name, balance, and last interaction date.
 */
public class BankAccount {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private int id;
    private String name;
    private double balance;
    private Date lastInteraction;

    /* -------------------------------- */
    /* ------ Constructors        ------ */
    /* -------------------------------- */

    /**
     * Constructs a new BankAccount with all attributes.
     *
     * @param id              the unique identifier of the bank account
     * @param name            the name of the bank account
     * @param balance         the current balance of the account
     * @param lastInteraction the date of the last interaction with the account
     */
    public BankAccount(int id, String name, double balance, Date lastInteraction) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.lastInteraction = lastInteraction;
    }

    /**
     * Constructs a new BankAccount without an ID.
     *
     * @param name            the name of the bank account
     * @param balance         the current balance of the account
     * @param lastInteraction the date of the last interaction with the account
     */
    public BankAccount(String name, double balance, Date lastInteraction) {
        this.name = name;
        this.balance = balance;
        this.lastInteraction = lastInteraction;
    }

    /* -------------------------------- */
    /* ------ Getters and Setters ------ */
    /* -------------------------------- */

    /**
     * Gets the ID of the bank account.
     *
     * @return the account ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the ID of the bank account.
     *
     * @param id the account ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the current balance of the bank account.
     *
     * @return the account balance
     */
    public double getBalance() {
        return this.balance;
    }

    /**
     * Sets the balance of the bank account.
     *
     * @param newBalance the new account balance
     */
    public void setBalance(double newBalance) {
        this.balance = newBalance;
    }

    /**
     * Gets the name of the bank account.
     *
     * @return the account name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the bank account.
     *
     * @param newName the new account name
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Gets the last interaction date of the bank account.
     *
     * @return the last interaction date
     */
    public Date getLastInteraction() {
        return this.lastInteraction;
    }

    /**
     * Sets the last interaction date of the bank account.
     *
     * @param lastInteraction the last interaction date
     */
    public void setLastInteraction(Date lastInteraction) {
        this.lastInteraction = lastInteraction;
    }

    /* -------------------------------- */
    /* ------ Overridden Methods ------ */
    /* -------------------------------- */

    /**
     * Returns a formatted string representation of the bank account details.
     *
     * @return a formatted string containing bank account information
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

        result.append("Bank Account Details:\n");
        result.append("  ID: ").append(this.id).append("\n");
        result.append("  Name: ").append(this.name).append("\n");
        result.append("  Balance: ").append(String.format("%.2f", this.balance)).append("\n");
        result.append("  Last Interaction: ").append(this.lastInteraction != null ? dateFormatter.format(this.lastInteraction) : "N/A").append("\n");

        return result.toString();
    }
}
