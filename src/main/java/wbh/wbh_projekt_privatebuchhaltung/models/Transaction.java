package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Date;

public class Transaction
{
    public int Id;
    public String Name;
    public String Description;
    public Double Value;
    public TransactionCategory Category;
    public BankAccount BankAccount;
    public Date TransactionDate;
}
