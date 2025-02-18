package wbh.wbh_projekt_privatebuchhaltung.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.*;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The Controller_data class handles all database operations for the private accounting project.
 * It provides methods to create tables, load data from the database into a Profile object,
 * and save various entities (UserSettings, BankAccount, TransactionCategory, Transaction, Goal, Badge)
 * into the database. All operations are logged using SLF4J.
 */
public class Controller_data {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private final Logger logger = LoggerFactory.getLogger(Controller_data.class);
    // Date formatter using the pattern "yyyy-MM-dd" and German locale.
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);

    // Log message constants.
    private static final String ERROR_MSG = "Error while {}: {}";
    private static final String SUCCESS_MSG = "{} inserted successfully.";
    private static final String FAILURE_MSG = "No {} were inserted.";
    private static final String NEW_ID_MSG = "New generated id: {}";
    private static final String TABLES_CREATED_MSG = "Tables were created successful.";

    /* -------------------------------- */
    /* ------ Database Helper Methods ------ */
    /* -------------------------------- */

    /**
     * Establishes and returns a database connection using the provided database file path.
     *
     * @param dbFilePath the database file path.
     * @return a Connection object.
     * @throws SQLException if a database access error occurs.
     */
    private Connection getConnection(String dbFilePath) throws SQLException {
        return DriverManager.getConnection(dbFilePath);
    }

    /**
     * Executes the given SQL command using the provided Statement.
     *
     * @param statement the Statement to execute the SQL command.
     * @param sql       the SQL command to execute.
     * @throws SQLException if a database access error occurs.
     */
    private void executeSQL(Statement statement, String sql) throws SQLException {
        statement.execute(sql);
    }

    /* -------------------------------- */
    /* ------ Logging Helper Methods ------ */
    /* -------------------------------- */

    /**
     * Logs an error message for the given operation and exception.
     *
     * @param operation a description of the operation being performed.
     * @param e         the Exception that was thrown.
     */
    private void logError(String operation, Exception e) {
        this.logger.error(ERROR_MSG, operation, e.getMessage(), e);
    }

    /**
     * Logs a success message for the given operation.
     *
     * @param operation a description of the operation performed.
     */
    private void logSuccess(String operation) {
        this.logger.info(SUCCESS_MSG, operation);
    }

    /**
     * Logs a failure message for the given operation.
     *
     * @param operation a description of the operation that failed.
     */
    private void logFailure(String operation) {
        this.logger.error(FAILURE_MSG, operation);
    }

    /* -------------------------------- */
    /* ------ Table Creation Methods ------ */
    /* -------------------------------- */

    /**
     * Creates the necessary database tables. Drops the existing tables if they exist,
     * and then creates new ones. Note: The "Goal" table is intentionally created twice
     * to preserve original functionality.
     *
     * @param path the database file path.
     */
    public void createTables(String path) {
        try (Connection connection = this.getConnection(path);
             Statement statement = connection.createStatement()) {

            // Drop and create BankAccount table.
            this.executeSQL(statement, "DROP TABLE IF EXISTS BankAccount;");
            this.executeSQL(statement, """
                CREATE TABLE IF NOT EXISTS BankAccount (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    Name TEXT NOT NULL,
                    balance REAL NOT NULL,
                    lastInteraction DATE NULL
                )
            """);

            // Drop and create TransactionCategory table.
            this.executeSQL(statement, "DROP TABLE IF EXISTS TransactionCategory;");
            this.executeSQL(statement, """
                CREATE TABLE IF NOT EXISTS TransactionCategory (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    CreatedByUser BIT NOT NULL
                )
            """);

            // Drop and create Transaction table.
            this.executeSQL(statement, "DROP TABLE IF EXISTS [Transaction];");
            this.executeSQL(statement, """
                CREATE TABLE IF NOT EXISTS [Transaction] (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    amount REAL NOT NULL,
                    categoryId INTEGER NULL,
                    bankAccountId INTEGER NOT NULL,
                    date DATE NOT NULL,
                    description TEXT NOT NULL,
                    FOREIGN KEY(categoryId) REFERENCES TransactionCategory(id),
                    FOREIGN KEY(bankAccountId) REFERENCES BankAccount(id)
                )
            """);

            // Drop and create UserSettings table.
            this.executeSQL(statement, "DROP TABLE IF EXISTS [UserSettings];");
            this.executeSQL(statement, """
                CREATE TABLE IF NOT EXISTS [UserSettings] (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    language INTEGER NOT NULL,
                    birthday DATE
                )
            """);

            // Drop and create Goal table.
            this.executeSQL(statement, "DROP TABLE IF EXISTS [Goal];");
            this.executeSQL(statement, """
                CREATE TABLE IF NOT EXISTS Goal (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    goalValue REAL NOT NULL,
                    bankAccountId INTEGER NOT NULL,
                    startDate DATE NOT NULL,
                    endDate DATE,
                    FOREIGN KEY (bankAccountId) REFERENCES BankAccount(id)
                );
            """);

            // Original code: Goal table is created again to preserve functionality.
            this.executeSQL(statement, "DROP TABLE IF EXISTS [Badge];");
            this.executeSQL(statement, """
                CREATE TABLE IF NOT EXISTS Goal (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    goalValue REAL NOT NULL,
                    bankAccountId INTEGER NOT NULL,
                    startDate DATE NOT NULL,
                    endDate DATE,
                    FOREIGN KEY (bankAccountId) REFERENCES BankAccount(id)
                );
            """);

            // Drop and create Badge table.
            this.executeSQL(statement, "DROP TABLE IF EXISTS Badge;");
            this.executeSQL(statement, """
                CREATE TABLE IF NOT EXISTS Badge (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    targetReachedGoals INTEGER NOT NULL,
                    completed BOOLEAN NOT NULL,
                    completedDate DATE
                );
            """);

            this.logger.info(TABLES_CREATED_MSG);
        } catch (SQLException e) {
            this.logError("creating tables", e);
        }
    }

    /* -------------------------------- */
    /* ------ Data Loading Methods ------ */
    /* -------------------------------- */

    /**
     * Loads data from the database into a Profile object.
     * It loads transaction categories, bank accounts, user settings, goals,
     * transactions, and badges.
     *
     * @param path the database file path.
     * @return a Profile object populated with data from the database.
     */
    public Profile loadData(String path) {
        Profile profile = new Profile();

        profile.setCategories(this.getAllTransactionCategories(path));
        profile.setBankAccounts(this.getAllBankAccounts(path));
        profile.setUserSettings(this.getUserSettings(path));

        this.getGoals(path, profile);
        this.getTransactions(path, profile);
        this.getAllBadges(path, profile);

        return profile;
    }

    /**
     * Loads transactions from the database and adds them to the Profile.
     *
     * @param dbFilePath the database file path.
     * @param profile    the Profile object to populate.
     */
    private void getTransactions(String dbFilePath, Profile profile) {
        String query = "SELECT id, [amount], date, description, categoryId, bankAccountId FROM [Transaction]";
        try (Connection connection = this.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                double amount = resultSet.getDouble("amount");
                Date date = this.dateFormat.parse(resultSet.getString("date"));
                String description = resultSet.getString("description");
                int categoryId = resultSet.getInt("categoryId");
                int bankAccountId = resultSet.getInt("bankAccountId");

                // Find the corresponding TransactionCategory.
                TransactionCategory category = profile.getCategories()
                        .stream()
                        .filter(c -> c.getId() == categoryId)
                        .findFirst()
                        .orElse(null);

                // Find the corresponding BankAccount.
                BankAccount account = profile.getBankAccounts()
                        .stream()
                        .filter(a -> a.getId() == bankAccountId)
                        .findFirst()
                        .orElse(null);

                // Determine if the transaction is an Income or an Expense.
                if (amount >= 0) {
                    Income income = new Income(id, amount, category, account, date, description);
                    profile.getIncomes().add(income);
                } else {
                    Expense expense = new Expense(id, amount, category, account, date, description);
                    profile.getExpenses().add(expense);
                }
            }
        } catch (SQLException e) {
            this.logError("loading Transactions from DB", e);
        } catch (ParseException e) {
            this.logError("parsing Date in Transactions", e);
        }
    }

    /**
     * Loads UserSettings from the database.
     *
     * @param dbFilePath the database file path.
     * @return a UserSettings object populated with data from the database, or null if an error occurs.
     */
    private UserSettings getUserSettings(String dbFilePath) {
        String query = "SELECT id, name, birthday, language FROM UserSettings";
        try (Connection connection = this.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            Date birthday = this.dateFormat.parse(resultSet.getString("birthday"));
            Language language = Language.values()[resultSet.getInt("language")];

            return new UserSettings(id, name, birthday, language);
        } catch (SQLException e) {
            this.logError("loading UserSettings from DB", e);
        } catch (ParseException e) {
            this.logError("parsing Date in UserSettings", e);
        }
        return null;
    }

    /**
     * Loads all TransactionCategories from the database.
     *
     * @param dbFilePath the database file path.
     * @return an ObservableList of TransactionCategory objects.
     */
    private ObservableList<TransactionCategory> getAllTransactionCategories(String dbFilePath) {
        ObservableList<TransactionCategory> categories = FXCollections.observableArrayList();
        String query = "SELECT id, name, CreatedByUser FROM TransactionCategory";

        try (Connection connection = this.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                boolean createdByUser = resultSet.getBoolean("CreatedByUser");

                TransactionCategory category = new TransactionCategory(id, name, createdByUser);
                categories.add(category);
            }
        } catch (SQLException e) {
            this.logError("loading TransactionCategories from DB", e);
        }
        return categories;
    }

    /**
     * Loads all BankAccounts from the database.
     *
     * @param dbFilePath the database file path.
     * @return an ObservableList of BankAccount objects.
     */
    private ObservableList<BankAccount> getAllBankAccounts(String dbFilePath) {
        ObservableList<BankAccount> bankAccounts = FXCollections.observableArrayList();
        String query = "SELECT id, Name, balance, lastInteraction FROM BankAccount";

        try (Connection connection = this.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("Name");
                double balance = resultSet.getDouble("balance");
                Date lastInteraction = this.dateFormat.parse(resultSet.getString("lastInteraction"));

                BankAccount account = new BankAccount(id, name, balance, lastInteraction);
                bankAccounts.add(account);
            }
        } catch (SQLException e) {
            this.logError("loading BankAccounts from DB", e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return bankAccounts;
    }

    /**
     * Loads all Goals from the database and adds them to the Profile.
     *
     * @param dbFilePath the database file path.
     * @param profile    the Profile object to populate.
     */
    private void getGoals(String dbFilePath, Profile profile) {
        String query = "SELECT id, name, description, goalValue, bankAccountId, startDate, endDate FROM Goal";
        try (Connection connection = this.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double goalValue = resultSet.getDouble("goalValue");
                int bankAccountId = resultSet.getInt("bankAccountId");
                Date startDate = this.dateFormat.parse(resultSet.getString("startDate"));
                Date endDate = this.dateFormat.parse(resultSet.getString("endDate"));

                // Find the corresponding BankAccount.
                BankAccount bankAccount = profile.getBankAccounts().stream()
                        .filter(account -> account.getId() == bankAccountId)
                        .findFirst()
                        .orElse(null);

                if (bankAccount != null) {
                    Goal goal = new Goal(id, name, description, goalValue, bankAccount, startDate, endDate);
                    profile.getGoals().add(goal);
                }
            }
        } catch (SQLException e) {
            this.logError("loading Goals from DB", e);
        } catch (ParseException e) {
            this.logError("parsing Date in Goals", e);
        }
    }

    /**
     * Loads all Badges from the database and adds them to the Profile.
     *
     * @param dbFilePath the database file path.
     * @param profile    the Profile object to populate.
     */
    private void getAllBadges(String dbFilePath, Profile profile) {
        String query = "SELECT id, name, targetReachedGoals, completed, completedDate FROM Badge";
        try (Connection connection = this.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int targetReachedGoals = resultSet.getInt("targetReachedGoals");
                boolean completed = resultSet.getBoolean("completed");
                Date completedDate = null;
                String completedDateStr = resultSet.getString("completedDate");

                // Parse completedDate if it exists.
                if (completedDateStr != null) {
                    completedDate = this.dateFormat.parse(completedDateStr);
                }

                Badge badge = new Badge(id, name, targetReachedGoals, completed, completedDate);
                profile.addBadge(badge);
            }
        } catch (SQLException e) {
            this.logError("loading Badges from DB", e);
        } catch (ParseException e) {
            this.logError("parsing Date in Badges", e);
        }
    }

    /* -------------------------------- */
    /* ------ Data Saving Methods ------ */
    /* -------------------------------- */

    /**
     * Saves the entire Profile to the database.
     * It creates the necessary tables and then saves UserSettings, BankAccounts,
     * TransactionCategories, Transactions (incomes and expenses), Goals, and Badges.
     *
     * @param dbFilePath the database file path.
     * @param profile    the Profile object to save.
     */
    public void saveProfile(String dbFilePath, Profile profile) {
        this.createTables(dbFilePath);

        // TODO: Implement UserSettings Usage
        this.saveUserSettings(dbFilePath, profile.getUserSettings());

        for (BankAccount account : profile.getBankAccounts()) {
            this.saveBankAccount(dbFilePath, account);
        }

        for (TransactionCategory category : profile.getCategories()) {
            this.saveTransactionCategory(dbFilePath, category);
        }

        for (Transaction transaction : profile.getIncomes()) {
            this.saveTransaction(dbFilePath, transaction);
        }
        for (Transaction transaction : profile.getExpenses()) {
            this.saveTransaction(dbFilePath, transaction);
        }

        for (Goal goal : profile.getGoals()) {
            this.saveGoal(dbFilePath, goal);
        }

        for (Badge badge : profile.getBadges()) {
            this.saveBadge(dbFilePath, badge);
        }
    }

    /**
     * Saves UserSettings to the database.
     *
     * @param dbFilePath   the database file path.
     * @param userSettings the UserSettings object to save.
     */
    public void saveUserSettings(String dbFilePath, UserSettings userSettings) {
        String query = """
            INSERT INTO UserSettings (name, birthday, language)
            VALUES (?, ?, ?);
        """;
        try (Connection connection = this.getConnection(dbFilePath);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userSettings.getName());
            preparedStatement.setString(2, userSettings.getBirthday() != null
                    ? this.dateFormat.format(userSettings.getBirthday())
                    : "1970-01-01");
            preparedStatement.setInt(3, userSettings.getLanguage().ordinal());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                this.logSuccess("UserSettings");
            } else {
                this.logFailure("UserSettings");
            }
        } catch (SQLException e) {
            this.logError("inserting UserSettings into DB", e);
        }
    }

    /**
     * Saves a Badge to the database.
     *
     * @param dbFilePath the database file path.
     * @param badge      the Badge object to save.
     */
    public void saveBadge(String dbFilePath, Badge badge) {
        String query = """
            INSERT INTO Badge (name, targetReachedGoals, completed, completedDate)
            VALUES (?, ?, ?, ?);
        """;
        try (Connection connection = this.getConnection(dbFilePath);
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, badge.getName());
            preparedStatement.setInt(2, badge.getTargetReachedGoals());
            preparedStatement.setBoolean(3, badge.isCompleted());
            if (badge.getCompletedDate() != null) {
                preparedStatement.setString(4, this.dateFormat.format(badge.getCompletedDate()));
            } else {
                preparedStatement.setNull(4, Types.DATE);
            }

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                this.logSuccess("Badge");
                badge.setId(this.getNewId(preparedStatement));
            } else {
                this.logFailure("Badge");
            }
        } catch (SQLException e) {
            this.logError("inserting Badge into DB", e);
        }
    }

    /**
     * Saves a BankAccount to the database.
     *
     * @param dbFilePath the database file path.
     * @param account    the BankAccount object to save.
     */
    public void saveBankAccount(String dbFilePath, BankAccount account) {
        String query = """
            INSERT INTO BankAccount ([Name], balance, lastInteraction)
            VALUES (?, ?, ?);
        """;
        try (Connection connection = this.getConnection(dbFilePath);
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, account.getName());
            preparedStatement.setDouble(2, account.getBalance());
            preparedStatement.setString(3, this.dateFormat.format(account.getLastInteraction()));

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                this.logSuccess("BankAccount");
                account.setId(this.getNewId(preparedStatement));
            } else {
                this.logFailure("BankAccount");
            }
        } catch (SQLException e) {
            this.logError("inserting BankAccount into DB", e);
        }
    }

    /**
     * Saves a TransactionCategory to the database.
     *
     * @param dbFilePath          the database file path.
     * @param transactionCategory the TransactionCategory object to save.
     */
    public void saveTransactionCategory(String dbFilePath, TransactionCategory transactionCategory) {
        String query = """
            INSERT INTO TransactionCategory (name, CreatedByUser)
            VALUES (?, ?);
        """;
        try (Connection connection = this.getConnection(dbFilePath);
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, transactionCategory.getName());
            preparedStatement.setBoolean(2, transactionCategory.isCreatedByUser());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                this.logSuccess("TransactionCategory");
                transactionCategory.setId(this.getNewId(preparedStatement));
            } else {
                this.logFailure("TransactionCategory");
            }
        } catch (SQLException e) {
            this.logError("inserting TransactionCategory into DB", e);
        }
    }

    /**
     * Retrieves the newly generated ID after an insert operation.
     *
     * @param preparedStatement the PreparedStatement used for the insert.
     * @return the generated ID, or -1 if no ID was returned.
     * @throws SQLException if a database access error occurs.
     */
    private int getNewId(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                int newId = generatedKeys.getInt(1);
                this.logger.info(NEW_ID_MSG, newId);
                return newId;
            } else {
                this.logger.error("No ID was returned for the new Object.");
                return -1;
            }
        }
    }

    /**
     * Saves a Transaction to the database.
     *
     * @param dbFilePath  the database file path.
     * @param transaction the Transaction object to save.
     */
    public void saveTransaction(String dbFilePath, Transaction transaction) {
        String query = """
            INSERT INTO [Transaction] (amount, categoryId, bankAccountId, date, description)
            VALUES (?, ?, ?, ?, ?);
        """;
        try (Connection connection = this.getConnection(dbFilePath);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setDouble(1, transaction.getValue());
            preparedStatement.setInt(2, transaction.getCategory().getId());
            preparedStatement.setInt(3, transaction.getBankaccount().getId());
            preparedStatement.setString(4, this.dateFormat.format(transaction.getDate()));
            preparedStatement.setString(5, transaction.getDescription());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                this.logSuccess("Transaction");
            } else {
                this.logFailure("Transaction");
            }
        } catch (SQLException e) {
            this.logError("inserting Transaction into DB", e);
        }
    }

    /**
     * Saves a Goal to the database.
     *
     * @param dbFilePath the database file path.
     * @param goal       the Goal object to save.
     */
    public void saveGoal(String dbFilePath, Goal goal) {
        String query = """
            INSERT INTO Goal (name, description, goalValue, bankAccountId, startDate, endDate)
            VALUES (?, ?, ?, ?, ?, ?);
        """;
        try (Connection connection = this.getConnection(dbFilePath);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, goal.getName());
            preparedStatement.setString(2, goal.getDescription());
            preparedStatement.setDouble(3, goal.getGoalValue());
            preparedStatement.setInt(4, goal.getBankAccount().getId());
            preparedStatement.setString(5, this.dateFormat.format(goal.getStartDate()));
            if (goal.getEndDate() != null) {
                preparedStatement.setString(6, this.dateFormat.format(goal.getEndDate()));
            } else {
                preparedStatement.setNull(6, Types.DATE);
            }

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                this.logSuccess("Goal");
            } else {
                this.logFailure("Goal");
            }
        } catch (SQLException e) {
            this.logError("inserting Goal into DB", e);
        }
    }
}
