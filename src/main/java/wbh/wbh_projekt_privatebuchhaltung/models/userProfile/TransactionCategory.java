package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

/**
 * The type Transaction category.
 */
public class TransactionCategory
{
    private int id;
    private String name;
    private boolean createdByUser;

    public TransactionCategory(int id, String name, boolean createdByUser) {
        this.id = id;
        this.name = name;
        this.createdByUser = createdByUser;
    }

    public TransactionCategory(int id, String name) {
        this.id = id;
        this.name = name;
        this.createdByUser = true;
    }

    public TransactionCategory(String name) {
        this.name = name;
        this.createdByUser = true;
    }

    public String getName(){
        return name;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){ this.id = id; }

    public void setName(String name){
        this.name = name;
    }

    public boolean isCreatedByUser() {
        return createdByUser;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("Category Details:\n");
        result.append("  ID: ").append(id).append("\n");
        result.append("  Name: ").append(name).append("\n");
        result.append("  Created By User: ").append(createdByUser ? "Yes" : "No").append("\n");

        return result.toString();
    }
}