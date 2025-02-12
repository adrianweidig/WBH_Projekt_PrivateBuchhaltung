package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The type Profile.
 */
public class Profile {

    private UserSettings userSettings = new UserSettings();
    private ObservableList<BankAccount> bankAccounts = FXCollections.observableArrayList();
    private ObservableList<Goal> goals = FXCollections.observableArrayList();
    private ObservableList<Income> incomes = FXCollections.observableArrayList();
    private ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private ObservableList<TransactionCategory> categories = FXCollections.observableArrayList();
    private ObservableList<Badge> badges = FXCollections.observableArrayList();

    // Getter and Setter for UserSettings
    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    // Getter and Setter for BankAccounts
    public ObservableList<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(ObservableList<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public void addBankAccount(BankAccount bankAccount) {
        this.bankAccounts.add(bankAccount);
    }

    public void removeBankAccount(BankAccount bankAccount) {
        this.bankAccounts.remove(bankAccount);
    }

    // Getter and Setter for Goals
    public ObservableList<Goal> getGoals() {
        return goals;
    }

    public void setGoals(ObservableList<Goal> goals) {
        this.goals = goals;
    }

    public void addGoal(Goal goal) {
        this.goals.add(goal);
    }

    public void removeGoal(Goal goal) {
        this.goals.remove(goal);
    }

    // Getter and Setter for Incomes
    public ObservableList<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(ObservableList<Income> incomes) {
        this.incomes = incomes;
    }

    public void addIncome(Income income) {
        this.incomes.add(income);
    }

    public void removeIncome(Income income) {
        this.incomes.remove(income);
    }

    // Getter and Setter for Expenses
    public ObservableList<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(ObservableList<Expense> expenses) {
        this.expenses = expenses;
    }

    public void addExpense(Expense expense) {
        this.expenses.add(expense);
    }

    public void removeExpense(Expense expense) {
        this.expenses.remove(expense);
    }

    // Getter and Setter for Categories
    public ObservableList<TransactionCategory> getCategories() {
        return categories;
    }

    public void setCategories(ObservableList<TransactionCategory> categories) {
        this.categories = categories;
    }

    public void addCategory(TransactionCategory category) {
        this.categories.add(category);
    }

    public void removeCategory(TransactionCategory category) {
        if (category.isCreatedByUser()) {
            this.categories.remove(category);
        }
    }

    //Getter and Setter for Badges
    public ObservableList<Badge> getBadges() {
        return badges;
    }
    public void setBadges(ObservableList<Badge> badges) {
        this.badges = badges;
    }
    public void addBadge(Badge badge) {
        this.badges.add(badge);
    }
    public void removeBadge(Badge badge) {
        this.badges.remove(badge);
    }

    // Override the  toString-Method
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        // User Settings
        result.append("User Settings:\n");
        result.append("  Name: ").append(userSettings.getName()).append("\n");
        result.append("  Birthday: ").append(userSettings.getBirthday()).append("\n");
        result.append("  Language: ").append(userSettings.getLanguage()).append("\n\n");

        // Categories
        result.append("Categories (").append(categories.size()).append("):\n");
        for (TransactionCategory category : categories) {
            result.append("  - ").append(category.getName()).append("\n");
        }
        result.append("\n");

        // Bank Accounts
        result.append("Bank Accounts (").append(bankAccounts.size()).append("):\n");
        for (BankAccount account : bankAccounts) {
            result.append("  - ID: ").append(account.getId())
                    .append(", Name: ").append(account.getName())
                    .append(", Balance: ").append(account.getBalance()).append("\n");
        }
        result.append("\n");

        // Incomes
        result.append("Incomes (").append(incomes.size()).append("):\n");
        for (Income income : incomes) {
            result.append("  - ").append(income.getDescription()).append("\n");
        }
        result.append("\n");

        // Expenses
        result.append("Expenses (").append(expenses.size()).append("):\n");
        for (Expense expense : expenses) {
            result.append("  - ").append(expense.getDescription()).append("\n");
        }
        result.append("\n");

        // Goals
        result.append("Goals (").append(goals.size()).append("):\n");
        for (Goal goal : goals) {
            result.append("  - ").append(goal.getDescription()).append("\n");
        }

        // Badges
        result.append("Badges (").append(badges.size()).append("):\n");
        for (Badge badge : badges) {
            result.append(" - ").append(badge.getName()).append(", CompletedOn: ").append(badge.getCompletedDate()).append("\n");
        }

        return result.toString();
    }
}