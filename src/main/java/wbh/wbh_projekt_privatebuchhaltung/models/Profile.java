package wbh.wbh_projekt_privatebuchhaltung.models;

import javafx.collections.ObservableList;

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
    public ObservableList<BankAccount> BankAccounts = javafx.collections.FXCollections.observableArrayList();
    /**
     * The Goals.
     */
    public ObservableList<Goal> Goals= javafx.collections.FXCollections.observableArrayList();
    /**
     * The Incomes.
     */
    public ObservableList<Income> Incomes = javafx.collections.FXCollections.observableArrayList();
    /**
     * The Expenses.
     */
    public ObservableList<Expense> Expenses = javafx.collections.FXCollections.observableArrayList();

    public ObservableList<TransactionCategory> Categories = javafx.collections.FXCollections.observableArrayList();
}