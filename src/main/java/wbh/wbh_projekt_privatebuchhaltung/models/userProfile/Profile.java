package wbh.wbh_projekt_privatebuchhaltung.models.userProfile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a user's financial profile, including accounts, transactions, goals, and settings.
 */
public class Profile {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private UserSettings userSettings = new UserSettings();
    private ObservableList<BankAccount> bankAccounts = FXCollections.observableArrayList();
    private ObservableList<Goal> goals = FXCollections.observableArrayList();
    private ObservableList<Income> incomes = FXCollections.observableArrayList();
    private ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private ObservableList<TransactionCategory> categories = FXCollections.observableArrayList();
    private ObservableList<Badge> badges = FXCollections.observableArrayList();

    /* -------------------------------- */
    /* ------ User Settings      ------ */
    /* -------------------------------- */

    /**
     * Gets the user settings.
     *
     * @return the user settings
     */
    public UserSettings getUserSettings() {
        return this.userSettings;
    }

    /**
     * Sets the user settings.
     *
     * @param userSettings the new user settings
     */
    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    /* -------------------------------- */
    /* ------ Bank Accounts      ------ */
    /* -------------------------------- */

    public ObservableList<BankAccount> getBankAccounts() {
        return this.bankAccounts;
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

    /* -------------------------------- */
    /* ------ Goals               ------ */
    /* -------------------------------- */

    public ObservableList<Goal> getGoals() {
        return this.goals;
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

    /* -------------------------------- */
    /* ------ Income Transactions ------ */
    /* -------------------------------- */

    public ObservableList<Income> getIncomes() {
        return this.incomes;
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

    /* -------------------------------- */
    /* ------ Expense Transactions ------ */
    /* -------------------------------- */

    public ObservableList<Expense> getExpenses() {
        return this.expenses;
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

    /* -------------------------------- */
    /* ------ Transaction Categories ------ */
    /* -------------------------------- */

    public ObservableList<TransactionCategory> getCategories() {
        return this.categories;
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

    /* -------------------------------- */
    /* ------ Badges               ------ */
    /* -------------------------------- */

    public ObservableList<Badge> getBadges() {
        return this.badges;
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

    /* -------------------------------- */
    /* ------ toString Method    ------ */
    /* -------------------------------- */

    /**
     * Returns a formatted string representation of the user's profile.
     *
     * @return the formatted profile details
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        // User Settings
        result.append("User Settings:\n");
        result.append("  Name: ").append(this.userSettings.getName()).append("\n");
        result.append("  Birthday: ").append(this.userSettings.getBirthday()).append("\n");
        result.append("  Language: ").append(this.userSettings.getLanguage()).append("\n\n");

        // Categories
        result.append("Categories (").append(this.categories.size()).append("):\n");
        for (TransactionCategory category : this.categories) {
            result.append("  - ").append(category.getName()).append("\n");
        }
        result.append("\n");

        // Bank Accounts
        result.append("Bank Accounts (").append(this.bankAccounts.size()).append("):\n");
        for (BankAccount account : this.bankAccounts) {
            result.append("  - ID: ").append(account.getId())
                    .append(", Name: ").append(account.getName())
                    .append(", Balance: ").append(account.getBalance()).append("\n");
        }
        result.append("\n");

        // Incomes
        result.append("Incomes (").append(this.incomes.size()).append("):\n");
        for (Income income : this.incomes) {
            result.append("  - ").append(income.getDescription()).append("\n");
        }
        result.append("\n");

        // Expenses
        result.append("Expenses (").append(this.expenses.size()).append("):\n");
        for (Expense expense : this.expenses) {
            result.append("  - ").append(expense.getDescription()).append("\n");
        }
        result.append("\n");

        // Goals
        result.append("Goals (").append(this.goals.size()).append("):\n");
        for (Goal goal : this.goals) {
            result.append("  - ").append(goal.getDescription()).append("\n");
        }

        // Badges
        result.append("Badges (").append(this.badges.size()).append("):\n");
        for (Badge badge : this.badges) {
            result.append(" - ").append(badge.getName()).append(", CompletedOn: ").append(badge.getCompletedDate()).append("\n");
        }

        return result.toString();
    }
}
