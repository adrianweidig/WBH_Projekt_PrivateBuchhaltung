package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Date;

public class Goal
{
    private int id;
    private String name;
    private String description;
    private double goalValue;
    private BankAccount bankAccount;
    private Date startDate = new Date();
    private Date endDate;
    private boolean completed;

    public Goal(
            int id,
            String name,
            String description,
            double goalValue,
            BankAccount bankAccount,
            Date startDate,
            Date endDate)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.goalValue = goalValue;
        this.bankAccount = bankAccount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId (){
        return id;
    }

    public String  getName (){
        return name;
    }

    public void setName (String name){
        this.name = name;
    }

    public String getDescription (){
        return description;
    }

    public void setDescription (String description){
        this.description = description;
    }

    public double getGoalValue (){
        return goalValue;
    }

    public void setGoalValue (double goalValue){
        this.goalValue = goalValue;
    }


    public double getCompletionRate(){
        return 1 / goalValue * bankAccount.getBalance();
    }
}
