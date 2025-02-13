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

public class Controller_data {

    private final Logger logger = LoggerFactory.getLogger(Controller_data.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);

    // Log-Konstanten
    private static final String ERROR_MSG = "Error while {}: {}";
    private static final String SUCCESS_MSG = "{} inserted successfully.";
    private static final String FAILURE_MSG = "No {} were inserted.";
    private static final String NEW_ID_MSG = "New generated id: {}";
    private static final String TABLES_CREATED_MSG = "Tables were created successful.";

    // Hilfsmethode für Datenbankverbindung
    private Connection getConnection(String dbFilePath) throws SQLException {
        return DriverManager.getConnection(dbFilePath);
    }

    // Hilfsmethode zum Ausführen von SQL-Befehlen
    private void executeSQL(Statement statement, String sql) throws SQLException {
        statement.execute(sql);
    }

    // Generische Logger-Hilfsmethode für Fehler
    private void logError(String operation, Exception e) {
        this.logger.error(ERROR_MSG, operation, e.getMessage(), e);
    }

    // Logger-Hilfsmethoden für Erfolg bzw. Misserfolg von DB-Operationen
    private void logSuccess(String operation) {
        this.logger.info(SUCCESS_MSG, operation);
    }

    private void logFailure(String operation) {
        this.logger.error(FAILURE_MSG, operation);
    }

    public void createTables(String path) {
        try (Connection connection = this.getConnection(path);
             Statement statement = connection.createStatement()) {

            this.executeSQL(statement, "DROP TABLE IF EXISTS BankAccount;");
            this.executeSQL(statement, """
                CREATE TABLE IF NOT EXISTS BankAccount (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    Name TEXT NOT NULL,
                    balance REAL NOT NULL,
                    lastInteraction DATE NULL
                )
            """);

            this.executeSQL(statement, "DROP TABLE IF EXISTS TransactionCategory;");
            this.executeSQL(statement, """
                CREATE TABLE IF NOT EXISTS TransactionCategory (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    CreatedByUser BIT NOT NULL
                )
            """);

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

            this.executeSQL(statement, "DROP TABLE IF EXISTS [UserSettings];");
            this.executeSQL(statement, """
                CREATE TABLE IF NOT EXISTS [UserSettings] (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    language INTEGER NOT NULL,
                    birthday DATE
                )
            """);

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

            // Originalcode: Goal wird hier nochmals erstellt – Funktionalität wird beibehalten
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

                TransactionCategory category = profile.getCategories()
                        .stream()
                        .filter(c -> c.getId() == categoryId)
                        .findFirst()
                        .orElse(null);

                BankAccount account = profile.getBankAccounts()
                        .stream()
                        .filter(a -> a.getId() == bankAccountId)
                        .findFirst()
                        .orElse(null);

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

    public void saveProfile(String dbFilePath, Profile profile) {
        this.createTables(dbFilePath);

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

    public void saveUserSettings(String dbFilePath, UserSettings userSettings) {
        String query = """
            INSERT INTO UserSettings (name, birthday, language)
            VALUES (?, ?, ?);
        """;
        try (Connection connection = this.getConnection(dbFilePath);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userSettings.getName());
            preparedStatement.setString(2, this.dateFormat.format(userSettings.getBirthday()));
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

    // Hilfsmethode zum Abrufen der neu generierten ID
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
