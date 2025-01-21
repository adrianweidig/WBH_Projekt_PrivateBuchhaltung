package wbh.wbh_projekt_privatebuchhaltung.controller;

import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.models.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Controller_data {

    private final Logger logger = LoggerFactory.getLogger(Controller_data.class);

    public void createTables(String path) {
        try (Connection connection = DriverManager.getConnection(path);
         Statement statement = connection.createStatement()) {

            statement.execute("""
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
              INSERT INTO [Transaction] (amount, categoryID, bankAccountId, [date], description)
              VALUES
              (50.00, 1,1,'2023-11-01', 'Einkauf im Supermarkt')
            """);

        } catch (SQLException e) {
            logger.error("Error creating tables: " + e.getMessage());
        }
    }

    public Profile loadData(String path)  {
        Profile profile = new Profile();

        profile.Categories = getAllTransactionCategories(path);
        profile.BankAccounts = getAllBankAccounts(path);

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

                Date date = java.sql.Date.valueOf(LocalDate.of(2025, 1, 20));
                // TODO Solve Date Problems
                String description = resultSet.getString("description");
                int categoryId = resultSet.getInt("categoryId");
                int bankAccountId = resultSet.getInt("bankAccountId");

                TransactionCategory category =  profile.Categories.getFirst();
                BankAccount account = profile.BankAccounts.getFirst();

                if(amount >= 0){
                    Income income = new Income(id, amount ,category, account, date, description);
                    logger.info("Income created");
                    logger.info(id +" "+ String.valueOf(amount)+" " + category.GetName() +" "+ account.getName()+" "+ date.toString()+" "+ description);
                    profile.Incomes.add(income);
                    logger.info("Done");
                    //TODO  InvocationTargetExeption  Cant Add the transactions
                }else{
                    Expense expense = new Expense(id, amount, category, account,date,description);
                    //profile.Expenses.add(expense);
                }
            }
        } catch (SQLException e) {
           logger.error("Fehler beim Abrufen der Transaktionen aus der Datenbank: " + e.getMessage(), e);
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
           logger.error("Fehler beim Abrufen der Kategorien: " + e.getMessage(), e);
        }
        return categories;
    }

    public ObservableList<BankAccount> getAllBankAccounts(String dbFilePath) {
        ObservableList<BankAccount> bankAccounts = javafx.collections.FXCollections.observableArrayList();
        String query = "SELECT id, Name, balance, lastInteraction FROM BankAccount";

        try (Connection connection = DriverManager.getConnection(dbFilePath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            // Iteriere durch das ResultSet, um Kontodaten abzurufen
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("Name");
                double balance = resultSet.getDouble("balance");
                Date lastInteraction = new Date(2025,1,20);
                // TODO How can i get the Date from SQL to Date class

                BankAccount account = new BankAccount(id, name, balance, lastInteraction);
                bankAccounts.add(account);
            }
        } catch (SQLException e) {
           logger.error("Fehler beim Abrufen der Bankkonten: " + e.getMessage(), e);
        }
        return bankAccounts;
    }

    // TODO muss noch getestet werden
    public void addTransactionCategory(TransactionCategory category) {
        String insertSQL = "INSERT INTO TransactionCategory (name) VALUES (?)";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:db.sqlite");
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, category.GetName());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            logger.error("Fehler beim Einfügen der Kategorie: " + e.getMessage(), e);
        }
    }
}