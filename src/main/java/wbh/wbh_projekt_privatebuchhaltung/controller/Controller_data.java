package wbh.wbh_projekt_privatebuchhaltung.controller;

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

    public void createTables(String path) {
        try (Connection connection = DriverManager.getConnection(path);
             Statement statement = connection.createStatement()) {

            statement.execute( """
                drop Table If Exists BankAccount;
            """);

            statement.execute("""                          
                CREATE TABLE IF NOT EXISTS BankAccount (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    Name TEXT NOT NULL,
                    balance REAL NOT NULL,
                    lastInteraction DATE Null
                    )
            """);

            statement.execute("""
                 Drop Table if Exists TransactionCategory;
            """);

            statement.execute("""             
                CREATE TABLE IF NOT EXISTS TransactionCategory (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    CreatedByUser bit NOT NULL
                )
            """);

            statement.execute("""
              Drop Table if Exists [Transaction];
            """);

            statement.execute("""                
                CREATE TABLE IF NOT EXISTS [Transaction] (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    amount REAL NOT NULL,
                    categoryId INTEGER NULL,
                    bankAccountId INTEGER NOT NULL,
                    date Date NOT NULL,
                    description TEXT not null,
                    FOREIGN KEY(categoryId) REFERENCES TransactionCategory(id),
                    FOREIGN KEY(bankAccountId) REFERENCES BankAccount(id)
                )
            """);

            statement.execute("""
              Drop Table if Exists [UserSettings];
            """);

            statement.execute("""                
                CREATE TABLE IF NOT EXISTS [UserSettings] (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    language INTEGER NOT NULL,
                    birthday Date
                )
            """);

            statement.execute("""
            DROP TABLE IF EXISTS [Goal];
            """);

            statement.execute("""
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

            statement.execute("""
            DROP TABLE IF EXISTS [Badge];
            """);

            statement.execute("""
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

            statement.execute("""
                DROP TABLE IF EXISTS Badge;
            """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS Badge (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    targetReachedGoals INTEGER NOT NULL,
                    completed BOOLEAN NOT NULL,
                    completedDate DATE
                );
            """);

            logger.info("Tables were created successful.");
        } catch (SQLException e) {
            logger.error("Error creating tables: " + e.getMessage());
        }
    }

    public Profile loadData(String path)  {
        Profile profile = new Profile();

        profile.setCategories(getAllTransactionCategories(path));
        profile.setBankAccounts(getAllBankAccounts(path));
        profile.setUserSettings(getUserSettings(path));

        getGoals(path, profile);
        getTransactions(path, profile);
        getAllBadges(path, profile);

        return profile;
    }

    private void getTransactions(String dbFilePath, Profile profile) {
        String query = "SELECT id, [amount], date, description, categoryId, bankAccountId FROM [Transaction]";

        try (Connection connection = DriverManager.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {


            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                double amount = resultSet.getDouble("amount");
                Date date = dateFormat.parse(resultSet.getString("date"));
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
                        .findFirst().orElse(null);

                if(amount >= 0){
                    Income income = new Income(id, amount ,category, account, date, description);
                    profile.getIncomes().add(income);
                }else{
                    Expense expense = new Expense(id, amount, category, account,date,description);
                    profile.getExpenses().add(expense);
                }
            }
        } catch (SQLException e) {
           logger.error("Error while loading Transactions from DB. " + e.getMessage(), e);
        } catch (ParseException e) {
            logger.error("Error while parsing Date: " + e.getMessage(), e);
        }
    }

    private UserSettings getUserSettings(String dbFilePath) {
        String query = "SELECT id, name, birthday, language FROM UserSettings";

        try (Connection connection = DriverManager.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Date birthday = dateFormat.parse(resultSet.getString("birthday"));
                Language language = Language.values()[resultSet.getInt("language")];

                return new UserSettings(id, name, birthday, language);
        } catch (SQLException e) {
            logger.error("Error while loading UserSettings from DB: " + e.getMessage(), e);
        } catch (ParseException e) {
            logger.error("Error while parsing Date: " + e.getMessage(), e);
        }
        return null;
    }

    private ObservableList<TransactionCategory> getAllTransactionCategories(String dbFilePath){
        ObservableList<TransactionCategory> categories = javafx.collections.FXCollections.observableArrayList() ;

        String query = "SELECT id, name, CreatedByUser FROM TransactionCategory";

        try (Connection connection = DriverManager.getConnection(dbFilePath);
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
           logger.error("Error while loading TransactionCategories from DB: " + e.getMessage(), e);
        }
        return categories;
    }

    private ObservableList<BankAccount> getAllBankAccounts(String dbFilePath) {
        ObservableList<BankAccount> bankAccounts = javafx.collections.FXCollections.observableArrayList();
        String query = "SELECT id, Name, balance, lastInteraction FROM BankAccount";

        try (Connection connection = DriverManager.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("Name");
                double balance = resultSet.getDouble("balance");
                Date lastInteraction = dateFormat.parse(resultSet.getString("lastInteraction"));

                BankAccount account = new BankAccount(id, name, balance, lastInteraction);
                bankAccounts.add(account);
            }
        } catch (SQLException e) {
           logger.error("Error while loading BankAccount from DB. " + e.getMessage(), e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return bankAccounts;
    }

    private void getGoals(String dbFilePath, Profile profile) {

        String query = "SELECT id, name, description, goalValue, bankAccountId, startDate, endDate FROM Goal";

        try (Connection connection = DriverManager.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double goalValue = resultSet.getDouble("goalValue");
                int bankAccountId = resultSet.getInt("bankAccountId");
                Date startDate = dateFormat.parse(resultSet.getString("startDate"));
                Date endDate = dateFormat.parse(resultSet.getString("endDate"));

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
            logger.error("Error while loading Goals from DB: " + e.getMessage());
        } catch (ParseException e) {
            logger.error("Error while parsing Date: " + e.getMessage());
        }
    }

    private void getAllBadges(String dbFilePath, Profile profile) {

        String query = "SELECT id, name, targetReachedGoals, completed, completedDate FROM Badge";

        try (Connection connection = DriverManager.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int targetReachedGoals = resultSet.getInt("targetReachedGoals");
                boolean completed = resultSet.getBoolean("completed");
                Date completedDate = null;

                // Parse completedDate if not null
                if (resultSet.getString("completedDate") != null) {
                    completedDate = dateFormat.parse(resultSet.getString("completedDate"));
                }

                // Create and add the Badge to the list
                Badge badge = new Badge(id, name, targetReachedGoals, completed, completedDate);
                profile.addBadge(badge);
            }
        } catch (SQLException e) {
            logger.error("Error while loading Badges from DB: " + e.getMessage());
        } catch (ParseException e) {
            logger.error("Error while parsing Date: " + e.getMessage());
        }
    }

    public void saveProfile(String dbFilePath, Profile profile) {
        createTables(dbFilePath);

        saveUserSettings(dbFilePath, profile.getUserSettings());

        for (BankAccount account : profile.getBankAccounts()) {
           saveBankAccount(dbFilePath, account);
        }

        for(TransactionCategory category : profile.getCategories()){
            saveTransactionCategory(dbFilePath, category);
        }

        for (Transaction transaction : profile.getIncomes()) {
            saveTransaction(dbFilePath, transaction);
        }
        for (Transaction transaction : profile.getExpenses()) {
           saveTransaction(dbFilePath, transaction);
        }

        for (Goal goal : profile.getGoals()) {
            saveGoal(dbFilePath, goal);
        }

        for(Badge badge : profile.getBadges()){
            saveBadge(dbFilePath, badge);
        }
    }

    public void saveUserSettings(String dbFilePath, UserSettings userSettings) {
        String query = """
            INSERT INTO UserSettings (name, birthday, language)
            VALUES (?, ?, ?);
        """;

        try (Connection connection = DriverManager.getConnection(dbFilePath);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, userSettings.getName());
                preparedStatement.setString(2,  dateFormat.format(userSettings.getBirthday()));
                preparedStatement.setInt(3, userSettings.getLanguage().ordinal());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    logger.info("UserSettings inserted successfully.");
                } else {
                    logger.error("No UserSettings were inserted.");
            }

        } catch (SQLException e) {
            logger.error("Error while inserting UserSettings into DB: " + e.getMessage());
        }
    }

    public void saveBadge(String dbFilePath, Badge badge) {
        String query = """
        INSERT INTO Badge (name, targetReachedGoals, completed, completedDate)
        VALUES (?, ?, ?, ?);
    """;

        try (Connection connection = DriverManager.getConnection(dbFilePath);
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, badge.getName());
            preparedStatement.setInt(2, badge.getTargetReachedGoals());
            preparedStatement.setBoolean(3, badge.isCompleted());

            if (badge.getCompletedDate() != null) {
                preparedStatement.setString(4, dateFormat.format(badge.getCompletedDate()));
            } else {
                preparedStatement.setNull(4, Types.DATE);
            }

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Badge inserted successfully.");
                badge.setId(getNewId(preparedStatement));
            } else {
                logger.error("No Badge was inserted.");
            }

        } catch (SQLException e) {
            logger.error("Error while inserting Badge into DB: " + e.getMessage(), e);
        }
    }

    public void saveBankAccount(String dbFilePath, BankAccount account) {
        String query = """
            INSERT INTO BankAccount ([Name],balance, lastInteraction)
            VALUES (?, ?, ?);
        """;

        try (Connection connection = DriverManager.getConnection(dbFilePath);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, account.getName());
            preparedStatement.setDouble(2,  account.getBalance());
            preparedStatement.setString(3, dateFormat.format(account.getLastInteraction()));

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("BankAccount inserted successfully.");
                account.setId(getNewId(preparedStatement));
            } else {
            logger.error("BankAccount wasn't inserted.");
            }
        } catch (SQLException e) {
            logger.error("Error while inserting BankAccount into DB: " + e.getMessage());
        }
    }

    public void saveTransactionCategory(String dbFilePath, TransactionCategory transactionCategory) {
        String query = """
            INSERT INTO TransactionCategory (name, CreatedByUser)
            VALUES (?, ?);
        """;

        try (Connection connection = DriverManager.getConnection(dbFilePath);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1,  transactionCategory.getName());
            preparedStatement.setBoolean(2,  transactionCategory.isCreatedByUser());


            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("TransactionCategory inserted successfully.");

                  transactionCategory.setId(getNewId(preparedStatement));

            } else {
                logger.error("TransactionCategory wasn't inserted.");
            }

        } catch (SQLException e) {
            logger.error("Error while inserting TransactionCategory into DB: " + e.getMessage());
        }
    }

     private int getNewId(PreparedStatement preparedStatement) throws SQLException {
         try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
             if (generatedKeys.next()) {
                 int newId = generatedKeys.getInt(1);
                 logger.info("New generated id : " + newId);
                 return newId;
             } else {
                 logger.error("No ID was returned for the new Object.");
                 return -1;
             }
         }
     }

    public void saveTransaction(String dbFilePath, Transaction transaction) {
        String query = """
            INSERT INTO [Transaction] (amount, categoryId , bankAccountId, date, description)
            VALUES (?, ?, ?,?,?);
        """;

        try (Connection connection = DriverManager.getConnection(dbFilePath);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setDouble(1, transaction.getValue());

            preparedStatement.setInt(2,  transaction.getCategory().getId());
            preparedStatement.setInt(3,  transaction.getBankaccount().getId());
            preparedStatement.setString(4, dateFormat.format(transaction.getDate()));
            preparedStatement.setString(5, transaction.getDescription());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Transaction inserted successfully.");
            } else {
                logger.error("Transaction wasn't inserted.");
            }

        } catch (SQLException e) {
            logger.error("Error while inserting Transaction into DB: " + e.getMessage());
        }
    }

    public void saveGoal(String dbFilePath, Goal goal) {
        String query = """
            INSERT INTO Goal (name, description, goalValue, bankAccountId, startDate, endDate)
            VALUES (?, ?, ?, ?, ?, ?);
        """;

        try (Connection connection = DriverManager.getConnection(dbFilePath);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, goal.getName());
            preparedStatement.setString(2, goal.getDescription());
            preparedStatement.setDouble(3, goal.getGoalValue());
            preparedStatement.setInt(4, goal.getBankAccount().getId());
            preparedStatement.setString(5, dateFormat.format(goal.getStartDate()));

            if (goal.getEndDate() != null) {
                preparedStatement.setString(6, dateFormat.format(goal.getEndDate()));
            } else {
                preparedStatement.setNull(6, Types.DATE);
            }

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Goal inserted successfully.");
            } else {
                logger.error("Goal wasn't inserted.");
            }
        } catch (SQLException e) {
            logger.error("Error while inserting Goal into DB: " + e.getMessage());
        }
    }
}