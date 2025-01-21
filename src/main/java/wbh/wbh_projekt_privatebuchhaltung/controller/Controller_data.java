package wbh.wbh_projekt_privatebuchhaltung.controller;

import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.models.*;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Controller_data {

    private final Logger logger = LoggerFactory.getLogger(Controller_data.class);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);

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
                    name TEXT NOT NULL
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

            logger.info("Tables were created successful.");

            /// Testimport:
            statement.execute("""
            Insert into TransactionCategory ([Name]) Values ('Gehalt');
            """);
            statement.execute("""
            Insert into TransactionCategory ([Name]) Values ('Lebensmittel');
            """);

            statement.execute("""     
            INSERT INTO BankAccount (Name, balance, lastInteraction) VALUES
            ('Sparkonto', 1500.75, '2023-01-01');
            """);

            statement.execute("""               
              INSERT INTO [Transaction] ([amount], categoryID, bankAccountId, [date], description)
              VALUES
              (50.00, 1,1,'2023-11-01', 'Einkauf im Supermarkt')
            """);
            statement.execute("""               
              INSERT INTO [Transaction] ([amount], categoryID, bankAccountId, [date], description)
              VALUES
              (-99.00, 2,1,'2025-01-21', 'Essen')
            """);

        } catch (SQLException e) {
            logger.error("Error creating tables: " + e.getMessage());
        }
    }

    public Profile loadData(String path)  {
        Profile profile = new Profile();

        profile.setCategories(getAllTransactionCategories(path));
        profile.setBankAccounts(getAllBankAccounts(path));

        getTransactions(profile, path);

        return profile;
    }

    public void getTransactions(Profile profile, String dbFilePath) {
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
            throw new RuntimeException(e);
        }
    }

    public ObservableList<TransactionCategory> getAllTransactionCategories(String dbFilePath){
        ObservableList<TransactionCategory> categories = javafx.collections.FXCollections.observableArrayList() ;

        String query = "SELECT id, name FROM TransactionCategory";

        try (Connection connection = DriverManager.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                TransactionCategory category = new TransactionCategory(id, name);
                categories.add(category);
            }
        } catch (SQLException e) {
           logger.error("Error while loading TransactionCategories from DB: " + e.getMessage(), e);
        }
        return categories;
    }

    public ObservableList<BankAccount> getAllBankAccounts(String dbFilePath) {
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
}