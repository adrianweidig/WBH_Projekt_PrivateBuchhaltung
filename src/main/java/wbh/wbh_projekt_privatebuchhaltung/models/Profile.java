package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Collections;
import java.util.List;

public class Profile {
    public UserSettings UserSettings;
    public List<BankAccount> BankAccounts = Collections.emptyList();
    public List<Goal> Goals = Collections.emptyList();
    public List<Income> Incomes = Collections.emptyList();
    public List<Expense> Expenses = Collections.emptyList();
}