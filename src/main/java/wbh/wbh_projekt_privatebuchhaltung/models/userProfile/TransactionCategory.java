package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

/**
 * Represents a transaction category, which can be created by the user or predefined.
 */
public class TransactionCategory {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private int id;
    private String name;
    private boolean createdByUser;

    /* -------------------------------- */
    /* ------ Constructors        ------ */
    /* -------------------------------- */

    /**
     * Instantiates a new TransactionCategory with all parameters.
     *
     * @param id            the category ID
     * @param name          the category name
     * @param createdByUser indicates whether the category was created by the user
     */
    public TransactionCategory(int id, String name, boolean createdByUser) {
        this.id = id;
        this.name = name;
        this.createdByUser = createdByUser;
    }

    /**
     * Instantiates a new TransactionCategory with a predefined ID and name.
     * By default, the category is considered user-created.
     *
     * @param id   the category ID
     * @param name the category name
     */
    public TransactionCategory(int id, String name) {
        this.id = id;
        this.name = name;
        this.createdByUser = true;
    }

    /**
     * Instantiates a new TransactionCategory with only a name.
     * The category is considered user-created and has no predefined ID.
     *
     * @param name the category name
     */
    public TransactionCategory(String name) {
        this.name = name;
        this.createdByUser = true;
    }

    /* -------------------------------- */
    /* ------ Getters & Setters  ------ */
    /* -------------------------------- */

    /**
     * Gets the category name.
     *
     * @return the category name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the category name.
     *
     * @param name the new category name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the category ID.
     *
     * @return the category ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the category ID.
     *
     * @param id the new category ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Checks if the category was created by the user.
     *
     * @return true if created by the user, otherwise false
     */
    public boolean isCreatedByUser() {
        return this.createdByUser;
    }

    /* -------------------------------- */
    /* ------ toString Method    ------ */
    /* -------------------------------- */

    /**
     * Returns the category name.
     * This ensures that the category can be properly displayed in dropdown menus.
     *
     * @return the category name
     */
    @Override
    public String toString() {
        return this.name;
    }
}
