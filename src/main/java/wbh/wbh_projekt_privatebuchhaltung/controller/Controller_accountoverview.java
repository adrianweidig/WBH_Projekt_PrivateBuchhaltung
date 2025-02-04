package wbh.wbh_projekt_privatebuchhaltung.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.models.interfaces.ProfileAware;
import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.Profile;
import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.Transaction;

/**
 * Controller for the account overview screen.
 * Handles displaying the transactions (income/expenses) and managing actions on them (edit, delete).
 */
public class Controller_accountoverview implements ProfileAware {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private final Logger logger = LoggerFactory.getLogger(Controller_accountoverview.class);

    private Profile profile = new Profile();

    /* -------------------------------- */
    /* ------ FXML Variables     ------ */
    /* -------------------------------- */

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

    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    /**
     * Initializes the controller by setting up the transaction table.
     * This method is called automatically after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        setupTransactionTable();
    }

    /* -------------------------------- */
    /* ------ Private Methods    ------ */
    /* -------------------------------- */

    /**
     * Sets up the transaction table with data from the profile.
     * This method configures the table's columns and binds them to the relevant data from the profile.
     */
    private void setupTransactionTable() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        transactions.addAll(profile.getIncomes());
        transactions.addAll(profile.getExpenses());

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        typeColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTyp()));

        addActionButtons();

        transactionTable.setItems(transactions);
        logger.info("Genutztes Profil:" + profile);
        logger.debug("Transaction table initialized with {} transactions", transactions.size());
    }

    /**
     * Adds the action buttons (edit and delete) to each row in the action column.
     * These buttons allow users to edit or delete a transaction.
     */
    private void addActionButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("", new FontIcon("fas-edit"));
            private final Button deleteButton = new Button("", new FontIcon("fas-trash"));

            {
                editButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    logger.info("Editing transaction: {}", transaction);
                    // TODO Implement your editing functionality here
                });

                deleteButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(transaction);
                    logger.info("Deleted transaction: {}", transaction);
                    // TODO Optionally, implement a confirmation prompt before deletion
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

    /* -------------------------------- */
    /* ------ Public Methods     ------ */
    /* -------------------------------- */

    /**
     * Sets the profile object to be used by this controller.
     * This method will populate the transaction table with the profile's transaction data.
     *
     * @param profile The Profile object to be set.
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
        logger.info("Profile set for account overview: {}", profile);
        setupTransactionTable(); // Refresh table with new profile data
    }
}
