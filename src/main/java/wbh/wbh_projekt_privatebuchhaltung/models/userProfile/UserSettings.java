package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import java.util.Date;

/**
 * Represents the user settings, including personal details and language preferences.
 */
public class UserSettings {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private int id;
    private String name;
    private Date birthday;
    private Language language;

    /* -------------------------------- */
    /* ------ Constructors        ------ */
    /* -------------------------------- */

    /**
     * Instantiates a new UserSettings object with all parameters.
     *
     * @param id       the user ID
     * @param name     the user's name
     * @param birthday the user's birthday
     * @param language the preferred language
     */
    public UserSettings(int id, String name, Date birthday, Language language) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.language = language;
    }

    /**
     * Instantiates a new UserSettings object with default values.
     */
    public UserSettings() {
    }

    /* -------------------------------- */
    /* ------ Getters & Setters  ------ */
    /* -------------------------------- */

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the user's name.
     *
     * @return the user's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the user's name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's birthday.
     *
     * @return the birthday as a Date object
     */
    public Date getBirthday() {
        return this.birthday;
    }

    /**
     * Sets the user's birthday.
     *
     * @param birthday the new birthday
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * Gets the preferred language.
     *
     * @return the preferred language
     */
    public Language getLanguage() {
        return this.language;
    }

    /**
     * Sets the preferred language.
     *
     * @param language the new language
     */
    public void setLanguage(Language language) {
        this.language = language;
    }
}
