package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import java.util.Date;

/**
 * Represents a badge that can be earned by reaching a target number of goals.
 * Each badge tracks its name, completion status, and completion date.
 */
public class Badge {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private int id;
    private String name;
    private int targetReachedGoals;
    private boolean completed;
    private Date completedDate;

    /* -------------------------------- */
    /* ------ Constructors        ------ */
    /* -------------------------------- */

    /**
     * Constructs a new Badge with all attributes.
     *
     * @param id                  the unique identifier of the badge
     * @param name                the name of the badge
     * @param targetReachedGoals  the number of goals required to earn this badge
     * @param completed           whether the badge has been completed
     * @param completedDate       the date when the badge was completed
     */
    public Badge(int id, String name, int targetReachedGoals, boolean completed, Date completedDate) {
        this.id = id;
        this.name = name;
        this.targetReachedGoals = targetReachedGoals;
        this.completed = completed;
        this.completedDate = completedDate;
    }

    /**
     * Constructs a new Badge without an ID.
     *
     * @param name                the name of the badge
     * @param targetReachedGoals  the number of goals required to earn this badge
     * @param completed           whether the badge has been completed
     * @param completedDate       the date when the badge was completed
     */
    public Badge(String name, int targetReachedGoals, boolean completed, Date completedDate) {
        this.name = name;
        this.targetReachedGoals = targetReachedGoals;
        this.completed = completed;
        this.completedDate = completedDate;
    }

    /* -------------------------------- */
    /* ------ Getters and Setters ------ */
    /* -------------------------------- */

    /**
     * Gets the ID of the badge.
     *
     * @return the badge ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the ID of the badge.
     *
     * @param id the badge ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the name of the badge.
     *
     * @return the badge name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the badge.
     *
     * @param name the new badge name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the target number of goals required to earn this badge.
     *
     * @return the number of goals
     */
    public int getTargetReachedGoals() {
        return this.targetReachedGoals;
    }

    /**
     * Sets the target number of goals required to earn this badge.
     *
     * @param targetReachedGoals the number of goals
     */
    public void setTargetReachedGoals(int targetReachedGoals) {
        this.targetReachedGoals = targetReachedGoals;
    }

    /**
     * Checks if the badge has been completed.
     *
     * @return true if the badge is completed, false otherwise
     */
    public boolean isCompleted() {
        return this.completed;
    }

    /**
     * Sets the completion status of the badge.
     *
     * @param completed true if the badge is completed, false otherwise
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Gets the completion date of the badge.
     *
     * @return the completion date
     */
    public Date getCompletedDate() {
        return this.completedDate;
    }

    /**
     * Sets the completion date of the badge.
     *
     * @param completedDate the completion date
     */
    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    /* -------------------------------- */
    /* ------ Overridden Methods ------ */
    /* -------------------------------- */

    /**
     * Returns a string representation of the badge.
     *
     * @return a string containing badge details
     */
    @Override
    public String toString() {
        return "Badge{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", targetReachedGoals=" + this.targetReachedGoals +
                ", completed=" + this.completed +
                ", completedDate=" + this.completedDate +
                '}';
    }
}
