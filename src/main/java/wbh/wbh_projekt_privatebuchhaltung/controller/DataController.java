package wbh.wbh_projekt_privatebuchhaltung.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.models.Profile;
import wbh.wbh_projekt_privatebuchhaltung.models.TransactionCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataController {

    private final Logger logger = LoggerFactory.getLogger(DataController.class);

    public void createTables() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:db.sqlite");
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
                    value REAL NOT NULL,
                    categoryId INTEGER NULL,
                    bankAccountId INTEGER NOT NULL,
                    date Date NOT NULL,
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

        } catch (SQLException e) {
            logger.error("Error creating tables: " + e.getMessage());
        }
    }

    public Profile loadData(String path) {
        if(path == null)
        {
            path = "jdbc:sqlite:db.sqlite";
        }
        Profile profile = new Profile();

        profile.Categories = getAllTransactionCategories(path);

        return profile;
    }

    public List<TransactionCategory> getAllTransactionCategories(String dbFilePath){
        List<TransactionCategory> categories = new ArrayList<>();

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