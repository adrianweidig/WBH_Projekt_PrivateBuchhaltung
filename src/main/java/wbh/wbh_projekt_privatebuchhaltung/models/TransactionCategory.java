package wbh.wbh_projekt_privatebuchhaltung.models;

/**
 * The type Transaction category.
 */
public class TransactionCategory
{
    private int id;
    private String name;

    public TransactionCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public int getId(){
        return id;
    }

    public void setName(String name){
        this.name = name;
    }
}