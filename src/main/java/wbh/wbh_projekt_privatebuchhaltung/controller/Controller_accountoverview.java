package wbh.wbh_projekt_privatebuchhaltung.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.models.interfaces.ProfileAware;
import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Controller_accountoverview implements ProfileAware {

    private final Logger logger = LoggerFactory.getLogger(Controller_accountoverview.class);
    private Profile profile = new Profile();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, String> typeColumn;

    @FXML
    private TableColumn<Transaction, Void> actionColumn;

    @FXML
    private StackPane rootPane;

    @FXML
    public void initialize() {
        setupTransactionTable();
    }

    private void setupTransactionTable() {
        dateColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(formatDate(cellData.getValue().getDate()))
        );

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        addActionButtons();

        ObservableList<Transaction> allTransactions = FXCollections.observableArrayList();
        allTransactions.addAll(profile.getIncomes());
        allTransactions.addAll(profile.getExpenses());
        transactionTable.setItems(allTransactions);

        profile.getIncomes().addListener((ListChangeListener<Transaction>) change -> {
            allTransactions.setAll(profile.getIncomes());
            allTransactions.addAll(profile.getExpenses());
            transactionTable.refresh();
        });

        profile.getExpenses().addListener((ListChangeListener<Transaction>) change -> {
            allTransactions.setAll(profile.getIncomes());
            allTransactions.addAll(profile.getExpenses());
            transactionTable.refresh();
        });

        logger.debug("Transaction table initialized with {} transactions", transactionTable.getItems().size());
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("", new FontIcon("fas-edit"));
            private final Button deleteButton = new Button("", new FontIcon("fas-trash"));

            {
                editButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    showEditDialog(transaction);
                });

                deleteButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(transaction);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actionButtons = new HBox(5, editButton, deleteButton);
                    setGraphic(actionButtons);
                }
            }
        });
    }

    private void showEditDialog(Transaction transaction) {
        VBox dialogContent = new VBox(10);
        dialogContent.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-border-color: black;");

        TextField descriptionField = new TextField(transaction.getDescription());
        TextField dateField = new TextField(dateFormatter.format(transaction.getDate()));
        TextField amountField = new TextField(Double.toString(transaction.getValue()));
        ComboBox<TransactionCategory> typeComboBox = new ComboBox<>(this.profile.getCategories());

        descriptionField.setPromptText("Enter a new description...");
        dateField.setPromptText("Enter date (dd.MM.yyyy)");
        amountField.setPromptText("Enter amount...");
        typeComboBox.setPromptText("Select category...");
        typeComboBox.setValue(transaction.getCategory());

        Button saveButton = new Button("Save");
        Button closeButton = new Button("Close");

        saveButton.setOnAction(e -> {
            try {
                // Parse the date and handle possible exceptions
                Date newDate = dateFormatter.parse(dateField.getText());
                transaction.setDescription(descriptionField.getText());
                transaction.setDate(newDate);
                transaction.setValue(Double.parseDouble(amountField.getText()));
                transaction.setCategory(typeComboBox.getValue());

                transactionTable.refresh();
                rootPane.getChildren().remove(dialogContent);
                logger.debug("Transaction updated: {}", transaction);
            } catch (Exception ex) {
                logger.error("Invalid input. Please check your input and try again.");
            }
        });

        closeButton.setOnAction(e -> rootPane.getChildren().remove(dialogContent));

        dialogContent.getChildren().addAll(new Label("Edit Date:"), dateField, new Label("Edit Description:"), descriptionField, new Label("Edit Type:"), typeComboBox, new Label("Edit Amount:"), amountField, new HBox(10, saveButton, closeButton));
        rootPane.getChildren().add(dialogContent);
    }

    private void showDeleteConfirmation(Transaction transaction) {
        VBox dialogContent = new VBox(10);
        dialogContent.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-border-color: black;");

        Label confirmationLabel = new Label("Are you sure you want to delete this transaction?");
        Button confirmButton = new Button("Delete");
        Button cancelButton = new Button("Cancel");

        confirmButton.setOnAction(e -> {
            if (transaction.getTyp().equalsIgnoreCase("Income")) {
                Income incomeTransaction = (Income) transaction;
                profile.getIncomes().remove(incomeTransaction);
            } else if (transaction.getTyp().equalsIgnoreCase("Expense")) {
                Expense expenseTransaction = (Expense) transaction;
                profile.getExpenses().remove(expenseTransaction);
            }
            rootPane.getChildren().remove(dialogContent);
            logger.debug("Transaction deleted: {}", transaction);
        });

        cancelButton.setOnAction(e -> rootPane.getChildren().remove(dialogContent));

        dialogContent.getChildren().addAll(confirmationLabel, new HBox(10, confirmButton, cancelButton));
        rootPane.getChildren().add(dialogContent);

        this.setupTransactionTable();
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return dateFormatter.format(date);
    }

    @Override
    public void setProfile(Profile profile) {
        this.profile = profile;
        ObservableList<Transaction> allTransactions = FXCollections.observableArrayList();
        allTransactions.addAll(profile.getIncomes());
        allTransactions.addAll(profile.getExpenses());
        transactionTable.setItems(allTransactions);
        logger.info("Profile set for account overview: {}", profile);
    }
}
