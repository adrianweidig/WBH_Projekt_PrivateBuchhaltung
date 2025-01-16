package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Date;

public class BankAccount
{
    private int id;
    private String name;
    private double balance;
    private Date lastInteraction;

    public BankAccount (int id, String name, double balance, Date lastInteraction) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.lastInteraction = lastInteraction;
    }

    public int getId(){
        return id;
    }

    public double getBalance()
    {
        return balance;
    }

    public void setBalance(double newBalance)
    {
        balance = newBalance;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String newName)
    {
        name = newName;
    }

    public Date getLastInteraction(){
        return lastInteraction;
    }

    public void setLastInteraction(Date lastInteraction) {
        this.lastInteraction = lastInteraction;
    }
}
