package wbh.wbh_projekt_privatebuchhaltung.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The type Profile.
 */
public class Profile {

    // Private Felder
    private UserSettings userSettings = new UserSettings();
    private ObservableList<BankAccount> bankAccounts = FXCollections.observableArrayList();
    private ObservableList<Goal> goals = FXCollections.observableArrayList();
    private ObservableList<Income> incomes = FXCollections.observableArrayList();
    private ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private ObservableList<TransactionCategory> categories = FXCollections.observableArrayList();

    // Getter und Setter für UserSettings
    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    // Getter und Setter für BankAccounts
    public ObservableList<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(ObservableList<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    // Getter und Setter für Goals
    public ObservableList<Goal> getGoals() {
        return goals;
    }

    public void setGoals(ObservableList<Goal> goals) {
        this.goals = goals;
    }

    // Getter und Setter für Incomes
    public ObservableList<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(ObservableList<Income> incomes) {
        this.incomes = incomes;
    }

    // Getter und Setter für Expenses
    public ObservableList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(ObservableList<Expense> expenses) {
        this.expenses = expenses;
    }

    // Getter und Setter für Categories
    public ObservableList<TransactionCategory> getCategories() {
        return categories;
    }

    public void setCategories(ObservableList<TransactionCategory> categories) {
        this.categories = categories;
    }
}
