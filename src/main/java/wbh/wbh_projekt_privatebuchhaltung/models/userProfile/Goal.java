package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import java.util.Date;

/**
 * Represents a financial goal that a user aims to achieve.
 * A goal is linked to a specific {@link BankAccount} and has a target value.
 */
public class Goal {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private int id;
    private String name;
    private String description;
    private double goalValue;
    private BankAccount bankAccount;
    private Date startDate = new Date();
    private Date endDate;

    /* -------------------------------- */
    /* ------ Constructors       ------ */
    /* -------------------------------- */

    /**
     * Constructs a Goal with an explicit ID.
     *
     * @param id          the unique identifier of the goal
     * @param name        the name of the goal
     * @param description a short description of the goal
     * @param goalValue   the target value to reach
     * @param bankAccount the associated bank account
     * @param startDate   the start date of the goal
     * @param endDate     the end date of the goal
     */
    public Goal(int id, String name, String description, double goalValue, BankAccount bankAccount, Date startDate, Date endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.goalValue = goalValue;
        this.bankAccount = bankAccount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Constructs a Goal without an explicit ID.
     *
     * @param name        the name of the goal
     * @param description a short description of the goal
     * @param goalValue   the target value to reach
     * @param bankAccount the associated bank account
     * @param startDate   the start date of the goal
     * @param endDate     the end date of the goal
     */
    public Goal(String name, String description, double goalValue, BankAccount bankAccount, Date startDate, Date endDate) {
        this.name = name;
        this.description = description;
        this.goalValue = goalValue;
        this.bankAccount = bankAccount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /* -------------------------------- */
    /* ------ Getters & Setters  ------ */
    /* -------------------------------- */

    /**
     * Gets the unique identifier of the goal.
     *
     * @return the goal ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the name of the goal.
     *
     * @return the goal name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the goal.
     *
     * @param name the new name of the goal
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the goal.
     *
     * @return the goal description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the goal.
     *
     * @param description the new description of the goal
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the target monetary value for the goal.
     *
     * @return the target value
     */
    public double getGoalValue() {
        return this.goalValue;
    }

    /**
     * Sets the target monetary value for the goal.
     *
     * @param goalValue the new target value
     */
    public void setGoalValue(double goalValue) {
        this.goalValue = goalValue;
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
     * @param bankAccount the new bank account
     */
    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    /**
     * Gets the start date of the goal.
     *
     * @return the start date
     */
    public Date getStartDate() {
        return this.startDate;
    }

    /**
     * Sets the start date of the goal.
     *
     * @param startDate the new start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date of the goal.
     *
     * @return the end date
     */
    public Date getEndDate() {
        return this.endDate;
    }

    /**
     * Sets the end date of the goal.
     *
     * @param endDate the new end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /* -------------------------------- */
    /* ------ Business Logic     ------ */
    /* -------------------------------- */

    /**
     * Calculates the completion rate of the goal based on the current bank balance.
     *
     * @return a value between 0 and 1 representing the completion percentage
     */
    public double getCompletionRate() {
        if (this.goalValue == 0) {
            return 0; // Avoid division by zero
        }
        return this.bankAccount.getBalance() / this.goalValue;
    }

    /**
     * Checks if the goal has been completed.
     *
     * @return true if the current balance meets or exceeds the goal value, otherwise false
     */
    public boolean isCompleted() {
        return this.bankAccount.getBalance() >= this.goalValue;
    }
}
