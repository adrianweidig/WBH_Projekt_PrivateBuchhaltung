package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Date;

public class Goal
{
    public int id;
    public String name;
    public String description;
    public double goalValue;
    public BankAccount bankAccount;
    public Date startDate = new Date();
    public Date endDate;
    public boolean completed;

    public double GetCompletionRate(){
        return 1/ goalValue * bankAccount.balance;
    }
}
