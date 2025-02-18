package wbh.wbh_projekt_privatebuchhaltung.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.enums.EnumGenerals;
import wbh.wbh_projekt_privatebuchhaltung.helpers.DialogButtonHelper;
import wbh.wbh_projekt_privatebuchhaltung.helpers.ValidationHelperFX;
import wbh.wbh_projekt_privatebuchhaltung.models.DeleteDialogButton;
import wbh.wbh_projekt_privatebuchhaltung.models.interfaces.ProfileAware;
import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * The Controller_accountoverview manages the overview of transactions.
 * Transactions (incomes and expenses) are displayed in a TableView and
 * can be manipulated via various dialogs (for adding, editing, and deleting).
 *
 * Important notes:
 * - Validation is performed using the custom ValidationHelperFX, which highlights invalid inputs
 *   with red markings and gentle background fill.
 * - Reusable logic, e.g., for creating dialog buttons, has been delegated to separate methods.
 * - The inner classes (DialogButtonHelper and ValidationHelperFX) currently remain in this file.
 *   TODO: In the future, extract them into separate files.
 *
 * Implements the ProfileAware interface to receive and update the user profile.
 */
public class Controller_accountoverview implements ProfileAware {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private final Logger logger = LoggerFactory.getLogger(Controller_accountoverview.class);
    private Profile profile = new Profile();
    // Modern date formatting: All users share this DateTimeFormatter.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Common constants
    private static final int HBOX_SPACING = 10;

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

    // Instance of DialogButtonHelper (contains user-specific states)
    private DialogButtonHelper dialogButtonHelper = null;

    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    /**
     * Initializes the controller after the FXML root element has been fully loaded.
     * Calls the method to set up the transaction table.
     */
    @FXML
    public void initialize() {
        setupTransactionTable();
        this.dialogButtonHelper = new DialogButtonHelper(this.rootPane);
    }

    /* -------------------------------- */
    /* ------ Private Methods    ------ */
    /* -------------------------------- */

    /**
     * Sets up the transaction table by binding the columns to the corresponding properties
     * of a transaction and populating the table with transactions from the profile.
     * Additionally, CSS styles are applied and listeners are registered to update the table
     * when changes occur in the profile.
     *
     * TODO: Further logic for populating and listener registration could be extracted into separate methods.
     */
    private void setupTransactionTable() {
        // Configure column data bindings
        dateColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(formatDate(cellData.getValue().getDate()))
        );
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Enable text wrapping for description column
        descriptionColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setWrapText(true);
                    setPrefHeight(Region.USE_COMPUTED_SIZE);
                }
            }
        });

        // Add action buttons column
        actionColumn.setCellFactory(createActionCellFactory());

        // Configure table behavior
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        transactionTable.setPlaceholder(new Label("Keine Transaktionen gefunden"));

        // Combine incomes and expenses
        ObservableList<Transaction> allTransactions = FXCollections.observableArrayList();
        allTransactions.addAll(profile.getIncomes());
        allTransactions.addAll(profile.getExpenses());
        transactionTable.setItems(allTransactions);

        // Add change listeners to update table when incomes or expenses change
        profile.getIncomes().addListener((ListChangeListener<Transaction>) change -> updateTable());
        profile.getExpenses().addListener((ListChangeListener<Transaction>) change -> updateTable());

        // Apply styling
        transactionTable.getStyleClass().add(EnumGenerals.CSS_TABLE_VIEW);
        rootPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/styles/style_contentpane.css"))
                        .toExternalForm()
        );

        // Apply CSS classes to columns
        dateColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);
        descriptionColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);
        amountColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);
        typeColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);
        actionColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);

        // Force layout update
        Platform.runLater(() -> {
            rootPane.applyCss();
            rootPane.layout();
        });
    }

    /**
     * Creates and returns a cell factory for the action column.
     * Each non-empty cell receives two buttons (Edit and Delete).
     *
     * @return Callback for cell creation.
     */
    private Callback<TableColumn<Transaction, Void>, TableCell<Transaction, Void>> createActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("", new FontIcon("fas-edit"));
            private final Button deleteButton = new Button("", new FontIcon("fas-trash"));

            {
                // Apply CSS classes to buttons.
                editButton.getStyleClass().add(EnumGenerals.CSS_DIALOG_BUTTON);
                deleteButton.getStyleClass().add(EnumGenerals.CSS_DIALOG_BUTTON);
                // Open the corresponding dialog when clicked.
                editButton.setOnAction(event -> showEditDialog(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> showDeleteConfirmation(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                HBox hBox = new HBox(HBOX_SPACING, editButton, deleteButton);
                hBox.setAlignment(Pos.CENTER);
                setGraphic(empty ? null : hBox);
            }
        };
    }

    /**
     * Re-aggregates incomes and expenses and updates the transaction table.
     *
     * TODO: Check if a diff-based update mechanism would improve performance.
     */
    private void updateTable() {
        ObservableList<Transaction> allTransactions = FXCollections.observableArrayList(profile.getIncomes());
        allTransactions.addAll(profile.getExpenses());
        transactionTable.setItems(allTransactions);
        transactionTable.refresh();
    }

    /**
     * Displays a confirmation dialog to delete the specified transaction.
     * The dialog is styled with CSS classes and added to the root pane.
     *
     * TODO: The dialog creation could be further delegated to a builder.
     *
     * @param transaction the transaction to be deleted.
     */
    private void showDeleteConfirmation(Transaction transaction) {
        DeleteDialogButton deleteDialogButton = new DeleteDialogButton(this.rootPane);
        deleteDialogButton.show("diese Transaktion", v -> {
            boolean success = true;
            // Determine the type of transaction and remove it from the corresponding list.
            if (transaction instanceof Income) {
                profile.getIncomes().remove(transaction);
            } else if (transaction instanceof Expense) {
                profile.getExpenses().remove(transaction);
            } else {
                success = false;
            }
            if (success) {
                updateTable();
                logger.debug("Transaction deleted: {}", transaction);
            }
        });
    }

    /**
     * Displays an edit dialog for the specified transaction.
     * In the dialog, the description, date, amount, and category can be modified.
     * A DatePicker is used for date selection.
     *
     * TODO: Additional layout logic could be extracted if needed.
     *
     * @param transaction the transaction to be edited.
     */
    private void showEditDialog(Transaction transaction) {
        VBox dialogContent = new VBox(10);
        dialogContent.getStyleClass().add(EnumGenerals.CSS_DIALOG_BOX);

        TextField descriptionField = new TextField(transaction.getDescription());
        descriptionField.getStyleClass().add(EnumGenerals.CSS_TEXT_FIELD);

        DatePicker datePicker = new DatePicker();
        configureDatePicker(datePicker);
        // Convert java.util.Date to LocalDate
        LocalDate localDate = new java.sql.Date(transaction.getDate().getTime()).toLocalDate();
        datePicker.setValue(localDate);

        TextField amountField = new TextField(Double.toString(transaction.getValue()));
        amountField.getStyleClass().add(EnumGenerals.CSS_TEXT_FIELD);

        ComboBox<TransactionCategory> typeComboBox = new ComboBox<>(this.profile.getCategories());
        typeComboBox.getStyleClass().add(EnumGenerals.CSS_COMBO_BOX);
        typeComboBox.setValue(transaction.getCategory());

        Button saveButton = dialogButtonHelper.createActionButton("Speichern");
        Button closeButton = dialogButtonHelper.createActionButton("Schließen");

        saveButton.setOnAction(e -> {
            boolean valid;
            ValidationHelperFX validationHelper = new ValidationHelperFX();

            valid = validationHelper.validateMandatoryFieldsFX(descriptionField, datePicker, amountField, typeComboBox);
            valid = valid && validationHelper.isValidAmount(amountField);
            if (valid) {
                try {
                    Date newDate = java.sql.Date.valueOf(datePicker.getValue());
                    transaction.setDescription(descriptionField.getText());
                    transaction.setDate(newDate);
                    transaction.setValue(Double.parseDouble(amountField.getText()));
                    transaction.setCategory(typeComboBox.getValue());
                    transactionTable.refresh();
                    dialogButtonHelper.removeDialog(dialogContent);
                    logger.debug("Transaktion aktualisiert: {}", transaction);
                } catch (Exception ex) {
                    logger.error("Ungültige Eingabe.", ex);
                }
            }
        });
        closeButton.setOnAction(e -> dialogButtonHelper.removeDialog(dialogContent));

        dialogContent.getChildren().addAll(
                dialogButtonHelper.createLabeledControl("Beschreibung:", descriptionField),
                dialogButtonHelper.createLabeledControl("Datum:", datePicker),
                dialogButtonHelper.createLabeledControl("Betrag:", amountField),
                dialogButtonHelper.createLabeledControl("Kategorie:", typeComboBox),
                new HBox(HBOX_SPACING, saveButton, closeButton)
        );
        dialogButtonHelper.showDialog(dialogContent);
    }

    /**
     * Displays a dialog for adding a new transaction.
     * A DatePicker is used for date selection and all mandatory fields are validated.
     * The transaction type (income or expense) is determined based on the amount.
     *
     * @see Income
     * @see Expense
     */
    @FXML
    private void showAddTransactionForm() {
        VBox dialogContent = new VBox(10);
        dialogContent.getStyleClass().add(EnumGenerals.CSS_DIALOG_BOX);

        TextField descriptionField = new TextField();
        descriptionField.getStyleClass().add(EnumGenerals.CSS_TEXT_FIELD);
        descriptionField.setPromptText("Transaktionsbeschreibung eingeben");

        DatePicker datePicker = new DatePicker();
        configureDatePicker(datePicker);
        datePicker.setPromptText("Datum auswählen");

        TextField amountField = new TextField();
        amountField.getStyleClass().add(EnumGenerals.CSS_TEXT_FIELD);
        amountField.setPromptText("Betrag eingeben (positiv = Einnahme, negativ = Ausgabe)");

        ComboBox<TransactionCategory> categoryComboBox = new ComboBox<>(profile.getCategories());
        categoryComboBox.getStyleClass().add(EnumGenerals.CSS_COMBO_BOX);
        categoryComboBox.setPromptText("Kategorie auswählen");

        ComboBox<BankAccount> accountComboBox = new ComboBox<>(profile.getBankAccounts());
        accountComboBox.getStyleClass().add(EnumGenerals.CSS_COMBO_BOX);
        accountComboBox.setPromptText("Bankkonto auswählen");

        Button saveButton = dialogButtonHelper.createActionButton("Speichern");
        Button closeButton = dialogButtonHelper.createActionButton("Schließen");

        saveButton.setOnAction(e -> {
            boolean valid;
            ValidationHelperFX validationHelper = new ValidationHelperFX();

            valid = validationHelper.validateMandatoryFieldsFX(descriptionField, datePicker, amountField, categoryComboBox, accountComboBox);
            valid = valid && validationHelper.isValidAmount(amountField);
            if (valid) {
                try {
                    double value = Double.parseDouble(amountField.getText());
                    Date date = java.sql.Date.valueOf(datePicker.getValue());
                    TransactionCategory category = categoryComboBox.getValue();
                    BankAccount account = accountComboBox.getValue();
                    String description = descriptionField.getText();
                    // Determine transaction type based on amount: positive for income, negative for expense.
                    if (value >= 0) {
                        profile.getIncomes().add(new Income(value, category, account, date, description));
                    } else {
                        profile.getExpenses().add(new Expense(Math.abs(value), category, account, date, description));
                    }
                    updateTable();
                    dialogButtonHelper.removeDialog(dialogContent);
                } catch (Exception ex) {
                    logger.error("Ungültiges Eingabeformat!", ex);
                }
            }
        });
        closeButton.setOnAction(e -> dialogButtonHelper.removeDialog(dialogContent));

        dialogContent.getChildren().addAll(
                dialogButtonHelper.createLabeledControl("Beschreibung:", descriptionField),
                dialogButtonHelper.createLabeledControl("Datum:", datePicker),
                dialogButtonHelper.createLabeledControl("Betrag:", amountField),
                dialogButtonHelper.createLabeledControl("Kategorie:", categoryComboBox),
                dialogButtonHelper.createLabeledControl("Bankkonto:", accountComboBox),
                new HBox(HBOX_SPACING, saveButton, closeButton)
        );
        dialogButtonHelper.showDialog(dialogContent);
    }

    /**
     * Formats a date using the specified date format.
     * Uses Java 8's DateTimeFormatter for modern date handling.
     *
     * @param date the date to format
     * @return the formatted date string or an empty string if the date is null
     */
    private String formatDate(Date date) {
        String result = "";
        result = (date == null) ? "" : DATE_FORMATTER.format(
                new java.sql.Date(date.getTime()).toLocalDate()
        );
        return result;
    }

    /**
     * Configures a DatePicker by applying the appropriate CSS class
     * and ensuring that clicking on the editor opens the calendar.
     *
     * @param datePicker the DatePicker to be configured
     */
    private void configureDatePicker(DatePicker datePicker) {
        datePicker.getStyleClass().add(EnumGenerals.CSS_DATE_PICKER);
        datePicker.getEditor().setOnMouseClicked(event -> datePicker.show());
    }

    /* -------------------------------- */
    /* ------ Interface Methods   ------ */
    /* -------------------------------- */

    /**
     * Sets the profile for this controller and updates the transaction table accordingly.
     *
     * @param profile the profile to set
     *
     * TODO: Validate the profile before updating and, if necessary, notify other components.
     */
    @Override
    public void setProfile(Profile profile) {
        if (profile != null) {
            this.profile = profile;
            updateTable();
        } else {
            logger.error("Null-Profil übergeben. Update abgebrochen.");
        }
    }
}
