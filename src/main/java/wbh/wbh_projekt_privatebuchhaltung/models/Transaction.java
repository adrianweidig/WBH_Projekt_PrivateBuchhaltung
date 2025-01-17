package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Date;

/**
 * The type Transaction.
 */
public abstract class Transaction {
    private double value;
    private TransactionCategory category;
    private BankAccount bankaccount;
    private Date date;

    /**
     * Instantiates a new Transaction.
     *
     * @param value       the value
     * @param category    the category
     * @param bankaccount the bankaccount
     * @param date        the date
     */
    public Transaction(double value, TransactionCategory category, BankAccount bankaccount, Date date) {
        this.value = value;
        this.category = category;
        this.bankaccount = bankaccount;
        this.date = date;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public TransactionCategory getCategory() {
        return category;
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    public void setCategory(TransactionCategory category) {
        this.category = category;
    }

    /**
     * Gets bankaccount.
     *
     * @return the bankaccount
     */
    public BankAccount getBankaccount() {
        return bankaccount;
    }

    /**
     * Sets bank account.
     *
     * @param bankaccount the bankaccount
     */
    public void setBankAccount(BankAccount bankaccount) {
        this.bankaccount = bankaccount;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets typ.
     *
     * @return the typ
     */
    public abstract String getTyp();
}