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

    public void addBankAccount(BankAccount bankAccount){
        this.bankAccounts.add(bankAccount);
    }

    public void removeBankAccount(BankAccount bankAccount){
        this.bankAccounts.remove(bankAccount);
    }

    // Getter und Setter für Goals
    public ObservableList<Goal> getGoals() {
        return goals;
    }

    public void setGoals(ObservableList<Goal> goals) {
        this.goals = goals;
    }

    public void addGoal(Goal goal){
        this.goals.add(goal);
    }

    public void removeGoal(Goal goal){
        this.goals.remove(goal);
    }

    // Getter und Setter für Incomes
    public ObservableList<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(ObservableList<Income> incomes) {
        this.incomes = incomes;
    }

    public void addIncome(Income income){
        this.incomes.add(income);
    }

    public void removeIncome(Income income){
        this.incomes.remove(income);
    }

    // Getter und Setter für Expenses
    public ObservableList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(ObservableList<Expense> expenses) {
        this.expenses = expenses;
    }

    public void addExpense(Expense expense){
        this.expenses.add(expense);
    }

    public void removeExpense(Expense expense){
        this.expenses.remove(expense);
    }

    // Getter und Setter für Categories
    public ObservableList<TransactionCategory> getCategories() {
        return categories;
    }

    public void setCategories(ObservableList<TransactionCategory> categories) {
        this.categories = categories;
    }

    public void addCategory(TransactionCategory category){
        this.categories.add(category);
    }

    public void removeCategory(TransactionCategory category){
        if(category.isCreatedByUser())
        {
            this.categories.remove(category);
        }
    }
}
