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

    public String GetName(){
        return name;
    }

    public int GetId(){
        return id;
    }

    public void SetName(String name){
        this.name = name;
    }
}