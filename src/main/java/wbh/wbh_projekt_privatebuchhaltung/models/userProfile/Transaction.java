package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The type Transaction.
 */
public abstract class Transaction {
    private int id;
    private double value;
    private TransactionCategory category;
    private BankAccount bankaccount;
    private Date date;
    private String description;

    /**
     * Instantiates a new Transaction.
     *
     * @param value       the value
     * @param category    the category
     * @param bankaccount the bankaccount
     * @param date        the date
     */
    protected Transaction(double value, TransactionCategory category, BankAccount bankaccount, Date date, String description) {
        this.value = value;
        this.category = category;
        this.bankaccount = bankaccount;
        this.date = date;
        this.description = description;
    }

    protected Transaction(int id, double value, TransactionCategory category, BankAccount bankaccount, Date date, String description) {
        this.id = id;
        this.value = value;
        this.category = category;
        this.bankaccount = bankaccount;
        this.date = date;
        this.description = description;
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

    public String getDescription(){
        return description;
    }

    public void setDescription (String description){
        this.description = description;
    }
    /**
     * Gets typ.
     *
     * @return the typ
     */
    public abstract String getTyp();

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

        result.append("Transaction Details:\n");
        result.append("  ID: ").append(id).append("\n");
        result.append("  Value: ").append(String.format("%.2f", value)).append("\n");
        result.append("  Category: ").append(category.getName()).append("\n");
        result.append("  Bank Account: ").append(bankaccount.getName()).append(" (ID: ").append(bankaccount.getId()).append(")\n");
        result.append("  Date: ").append(dateFormatter.format(date)).append("\n");
        result.append("  Description: ").append(description).append("\n");

        return result.toString();
    }
}