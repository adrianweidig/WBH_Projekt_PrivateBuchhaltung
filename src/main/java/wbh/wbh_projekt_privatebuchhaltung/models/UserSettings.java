package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Date;

/**
 * The type User settings.
 */
public class UserSettings
{
    private int id;
    private String name;
    private Date birthday;
    private Language language;

    /**
     * Instantiates a new User settings.
     *
     * @param id       the id
     * @param name     the name
     * @param birthday the birthday
     * @param language the language
     */
    public UserSettings (int id, String name, Date birthday, Language language)
    {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.language = language;
    }

    /**
     * Get id int.
     *
     * @return the int
     */
    public int GetId(){
        return id;
    }

    /**
     * Get name string.
     *
     * @return the string
     */
    public String GetName(){
        return name;
    }

    /**
     * Set name.
     *
     * @param name the name
     */
    public void SetName(String name){
        this.name = name;
    }

    /**
     * Get birthday date.
     *
     * @return the date
     */
    public Date GetBirthday(){
        return birthday;
    }

    /**
     * Set birthday.
     *
     * @param birthday the birthday
     */
    public void SetBirthday(Date birthday){
        this.birthday = birthday;
    }

    /**
     * Get language language.
     *
     * @return the language
     */
    public Language GetLanguage(){
        return language;
    }

    /**
     * Set language.
     *
     * @param language the language
     */
    public void SetLanguage(Language language){
        this.language = language;
    }
}