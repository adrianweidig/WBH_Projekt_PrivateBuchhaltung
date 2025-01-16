package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Date;

public class UserSettings
{
    private int id;
    private String name;
    private Date birthday;
    private Language language;

    public UserSettings (int id, String name, Date birthday, Language language)
    {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.language = language;
    }

    public int GetId(){
        return id;
    }

    public String GetName(){
        return name;
    }

    public void SetName(String name){
        this.name = name;
    }

    public Date GetBirthday(){
        return birthday;
    }

    public void SetBirthday(Date birthday){
        this.birthday = birthday;
    }

    public Language GetLanguage(){
        return language;
    }

    public void SetLanguage(Language language){
        this.language = language;
    }
}