package wbh.wbh_projekt_privatebuchhaltung.models;

import java.util.Collections;
import java.util.List;

/**
 * The type Profile.
 */
public class Profile {
    /**
     * The User settings.
     */
    public UserSettings UserSettings;
    /**
     * The Bank accounts.
     */
    public List<BankAccount> BankAccounts = Collections.emptyList();
    /**
     * The Goals.
     */
    public List<Goal> Goals = Collections.emptyList();
    /**
     * The Incomes.
     */
    public List<Income> Incomes = Collections.emptyList();
    /**
     * The Expenses.
     */
    public List<Expense> Expenses = Collections.emptyList();
}