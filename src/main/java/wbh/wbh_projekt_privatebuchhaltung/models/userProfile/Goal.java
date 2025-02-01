package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import java.util.Date;

/**
 * The type Goal.
 */
public class Goal
{
    private int id;
    private String name;
    private String description;
    private double goalValue;
    private BankAccount bankAccount;
    private Date startDate = new Date();
    private Date endDate;

    /**
     * Instantiates a new Goal.
     *
     * @param id          the id
     * @param name        the name
     * @param description the description
     * @param goalValue   the goal value
     * @param bankAccount the bank account
     * @param startDate   the start date
     * @param endDate     the end date
     */
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

    public Goal(
            String name,
            String description,
            double goalValue,
            BankAccount bankAccount,
            Date startDate,
            Date endDate)
    {
        this.name = name;
        this.description = description;
        this.goalValue = goalValue;
        this.bankAccount = bankAccount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Get id int.
     *
     * @return the int
     */
    public int getId (){
        return id;
    }

    /**
     * Get name string.
     *
     * @return the string
     */
    public String  getName (){
        return name;
    }

    /**
     * Set name.
     *
     * @param name the name
     */
    public void setName (String name){
        this.name = name;
    }

    /**
     * Get description string.
     *
     * @return the string
     */
    public String getDescription (){
        return description;
    }

    /**
     * Set description.
     *
     * @param description the description
     */
    public void setDescription (String description){
        this.description = description;
    }

    /**
     * Get goal value double.
     *
     * @return the double
     */
    public double getGoalValue (){
        return goalValue;
    }

    /**
     * Set goal value.
     *
     * @param goalValue the goal value
     */
    public void setGoalValue (double goalValue){
        this.goalValue = goalValue;
    }

    public BankAccount getBankAccount () {
       return bankAccount;
    }

    public void setBankAccount (BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Get completion rate double.
     *
     * @return the double
     */
    public double getCompletionRate(){
        return 1 / goalValue * bankAccount.getBalance();
    }

    public boolean isCompleted(){
        return bankAccount.getBalance() >= goalValue;
    }
}