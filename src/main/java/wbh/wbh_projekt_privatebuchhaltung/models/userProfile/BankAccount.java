package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The type Bank account.
 */
public class BankAccount
{
    private int id;
    private String name;
    private double balance;
    private Date lastInteraction;

    /**
     * Instantiates a new Bank account.
     *
     * @param id              the id
     * @param name            the name
     * @param balance         the balance
     * @param lastInteraction the last interaction
     */
    public BankAccount (int id, String name, double balance, Date lastInteraction) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.lastInteraction = lastInteraction;
    }

    public BankAccount (String name, double balance, Date lastInteraction) {
    this.name = name;
    this.balance = balance;
    this.lastInteraction = lastInteraction;
}

    /**
     * Get id int.
     *
     * @return the int
     */
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
    /**
     * Gets balance.
     *
     * @return the balance
     */
    public double getBalance()
    {
        return balance;
    }

    /**
     * Sets balance.
     *
     * @param newBalance the new balance
     */
    public void setBalance(double newBalance)
    {
        balance = newBalance;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets name.
     *
     * @param newName the new name
     */
    public void setName(String newName)
    {
        name = newName;
    }

    /**
     * Get last interaction date.
     *
     * @return the date
     */
    public Date getLastInteraction(){
        return lastInteraction;
    }

    /**
     * Sets last interaction.
     *
     * @param lastInteraction the last interaction
     */
    public void setLastInteraction(Date lastInteraction) {
        this.lastInteraction = lastInteraction;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

        result.append("Bank Account Details:\n");
        result.append("  ID: ").append(id).append("\n");
        result.append("  Name: ").append(name).append("\n");
        result.append("  Balance: ").append(String.format("%.2f", balance)).append("\n");
        result.append("  Last Interaction: ").append(lastInteraction != null ? dateFormatter.format(lastInteraction) : "N/A").append("\n");

        return result.toString();
    }
}
