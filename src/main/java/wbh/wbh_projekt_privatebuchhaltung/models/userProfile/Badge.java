package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import java.util.Date;

public class Badge {
    private int id;
    private String name;
    private int targetReachedGoals;
    private boolean completed;
    private Date completedDate;

    // Constructor
    public Badge(int id, String name, int targetReachedGoals, boolean completed, Date completedDate) {
        this.id = id;
        this.name = name;
        this.targetReachedGoals = targetReachedGoals;
        this.completed = completed;
        this.completedDate = completedDate;
    }

    public Badge(String name, int targetReachedGoals, boolean completed, Date completedDate) {
        this.name = name;
        this.targetReachedGoals = targetReachedGoals;
        this.completed = completed;
        this.completedDate = completedDate;
    }


    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTargetReachedGoals() {
        return targetReachedGoals;
    }

    public void setTargetReachedGoals(int targetReachedGoals) {
        this.targetReachedGoals = targetReachedGoals;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompleteDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    // toString Method
    @Override
    public String toString() {
        return "Badge{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", targetReachedGoals=" + targetReachedGoals +
                ", completed=" + completed +
                ", completedDay=" + completedDate +
                '}';
    }
}