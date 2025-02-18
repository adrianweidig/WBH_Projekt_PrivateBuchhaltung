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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.enums.EnumGenerals;
import wbh.wbh_projekt_privatebuchhaltung.helpers.ValidationHelperFX;
import wbh.wbh_projekt_privatebuchhaltung.models.interfaces.ProfileAware;
import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.BankAccount;
import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.Profile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

import static wbh.wbh_projekt_privatebuchhaltung.enums.EnumGenerals.CSS_TABLE_VIEW;

public class Controller_bankAccount implements ProfileAware {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private final Logger logger = LoggerFactory.getLogger(Controller_bankAccount.class);
    private Profile profile = new Profile();
    // Date formatter for display
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    // Gemeinsame Strings
    private static final int HBOX_SPACING = 10;

    @FXML private TableView<BankAccount> bankAccountTable;
    @FXML private TableColumn<BankAccount, String> nameColumn;
    @FXML private TableColumn<BankAccount, Double> balanceColumn;
    @FXML private TableColumn<BankAccount, String> lastInteractionColumn;
    @FXML private TableColumn<BankAccount, Void> actionColumn;
    @FXML private StackPane rootPane;

    private final DialogHelper dialogHelper = new DialogHelper();

    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    @FXML
    public void initialize() {
        setupBankAccountTable();
    }

    /* -------------------------------- */
    /* ------ Private Methods    ------ */
    /* -------------------------------- */

    private void setupBankAccountTable() {
        // Configure columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        lastInteractionColumn.setCellValueFactory(cellData ->
            new ReadOnlyStringWrapper(formatDate(cellData.getValue().getLastInteraction()))
        );

        actionColumn.setCellFactory(createActionCellFactory());

        // Configure table behavior
        bankAccountTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        bankAccountTable.setPlaceholder(new Label("No bank accounts found"));

        // Combine data from the profile
        bankAccountTable.setItems(profile.getBankAccounts());

        // Add change listeners
        profile.getBankAccounts().addListener((ListChangeListener<BankAccount>) change -> updateTable());

        // Apply styling
        bankAccountTable.getStyleClass().add(CSS_TABLE_VIEW);
        nameColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);
        balanceColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);
        lastInteractionColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);
        actionColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);
        rootPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/styles/style_contentpane.css"))
                        .toExternalForm()
        );

        Platform.runLater(() -> {
            rootPane.applyCss();
            rootPane.layout();
        });
    }

    private Callback<TableColumn<BankAccount, Void>, TableCell<BankAccount, Void>> createActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("", new FontIcon("fas-edit"));
            private final Button deleteButton = new Button("", new FontIcon("fas-trash"));

            {
                editButton.getStyleClass().add(EnumGenerals.CSS_DIALOG_BUTTON);
                deleteButton.getStyleClass().add(EnumGenerals.CSS_DIALOG_BUTTON);

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

    private void updateTable() {
        ObservableList<BankAccount> allAccounts = FXCollections.observableArrayList(profile.getBankAccounts());
        bankAccountTable.setItems(allAccounts);
        bankAccountTable.refresh();
    }

    private void showDeleteConfirmation(BankAccount bankAccount) {
        VBox dialogContent = dialogHelper.createDialogContainer();

        Label confirmationLabel = new Label("Are you sure you want to delete this bank account?");
        Button confirmButton = dialogHelper.createActionButton("Delete");
        Button cancelButton = dialogHelper.createActionButton("Cancel");

        confirmButton.setOnAction(e -> {
            profile.getBankAccounts().remove(bankAccount);
            dialogHelper.removeDialog(dialogContent);
            updateTable();
            logger.debug("Bank account deleted: {}", bankAccount);
        });

        cancelButton.setOnAction(e -> dialogHelper.removeDialog(dialogContent));

        dialogContent.getChildren().addAll(confirmationLabel, new HBox(HBOX_SPACING, confirmButton, cancelButton));
        dialogHelper.showDialog(dialogContent);
    }

    private void showEditDialog(BankAccount bankAccount) {
        VBox dialogContent = dialogHelper.createDialogContainer();

        TextField nameField = new TextField(bankAccount.getName());
        TextField balanceField = new TextField(Double.toString(bankAccount.getBalance()));

        DatePicker lastInteractionDatePicker = new DatePicker();
        configureDatePicker(lastInteractionDatePicker);
        LocalDate lastInteractionDate = new java.sql.Date(bankAccount.getLastInteraction().getTime()).toLocalDate();
        lastInteractionDatePicker.setValue(lastInteractionDate);

        Button saveButton = dialogHelper.createActionButton("Save");
        Button closeButton = dialogHelper.createActionButton("Close");

        saveButton.setOnAction(e -> {
            boolean valid = true;
            ValidationHelperFX validationHelper = new ValidationHelperFX();

            valid = valid && validationHelper.validateMandatoryFieldsFX(nameField, lastInteractionDatePicker);
            valid = valid && validationHelper.isValidAmount(balanceField.getText());

            if (valid) {
                try {
                    bankAccount.setName(nameField.getText());
                    bankAccount.setBalance(Double.parseDouble(balanceField.getText()));
                    bankAccount.setLastInteraction(java.sql.Date.valueOf(lastInteractionDatePicker.getValue()));

                    bankAccountTable.refresh();
                    dialogHelper.removeDialog(dialogContent);
                    logger.debug("Bank account updated: {}", bankAccount);

                } catch (Exception ex) {
                    logger.error("Invalid input.", ex);
                }
            }
        });

        closeButton.setOnAction(e -> dialogHelper.removeDialog(dialogContent));

        dialogContent.getChildren().addAll(
                dialogHelper.createLabeledControl("Name:", nameField),
                dialogHelper.createLabeledControl("Balance:", balanceField),
                dialogHelper.createLabeledControl("Last Interaction Date:", lastInteractionDatePicker),
                new HBox(HBOX_SPACING, saveButton, closeButton)
        );

        dialogHelper.showDialog(dialogContent);
    }

    @FXML
    private void showAddBankAccountForm() {
        VBox dialogContent = dialogHelper.createDialogContainer();

        TextField nameField = new TextField();
        TextField balanceField = new TextField();
        DatePicker lastInteractionDatePicker = new DatePicker();
        configureDatePicker(lastInteractionDatePicker);

        Button saveButton = dialogHelper.createActionButton("Save");
        Button closeButton = dialogHelper.createActionButton("Close");

        saveButton.setOnAction(e -> {
            boolean valid;
            ValidationHelperFX validationHelper = new ValidationHelperFX();

            valid = validationHelper.validateMandatoryFieldsFX(nameField, lastInteractionDatePicker);
            valid = valid && validationHelper.isValidAmount(balanceField.getText());

            if (valid) {
                try {
                    String name = nameField.getText();
                    double balance = Double.parseDouble(balanceField.getText());
                    Date expirationDate = java.sql.Date.valueOf(lastInteractionDatePicker.getValue());

                    BankAccount bankAccount = new BankAccount(name, balance, expirationDate);
                    profile.addBankAccount(bankAccount);

                    bankAccountTable.refresh();
                    updateTable();
                    dialogHelper.removeDialog(dialogContent);

                    logger.debug("Bank account added: {}", bankAccount);

                } catch (Exception ex) {
                    logger.error("Invalid input.", ex);
                }
            }
        });

        closeButton.setOnAction(e -> dialogHelper.removeDialog(dialogContent));

        dialogContent.getChildren().addAll(
                dialogHelper.createLabeledControl("Name:", nameField),
                dialogHelper.createLabeledControl("Balance:", balanceField),
                dialogHelper.createLabeledControl("Last Interaction Date:", lastInteractionDatePicker),
                new HBox(HBOX_SPACING, saveButton, closeButton)
        );

        dialogHelper.showDialog(dialogContent);
    }

    private void configureDatePicker(DatePicker datePicker) {
        datePicker.getStyleClass().add(EnumGenerals.CSS_DATE_PICKER);
        datePicker.getEditor().setOnMouseClicked(event -> datePicker.show());
    }

    private String formatDate(Date date) {
        return date == null ? "" : DATE_FORMAT.format(date);
    }

    /* -------------------------------- */
    /* ------ Inner Classes      ------ */
    /* -------------------------------- */

    private class DialogHelper {
        VBox createDialogContainer() {
            VBox dialog = new VBox(10);
            dialog.getStyleClass().add(EnumGenerals.CSS_DIALOG_BOX);
            StackPane.setAlignment(dialog, Pos.CENTER);
            return dialog;
        }

        void showDialog(VBox dialog) {
            rootPane.getChildren().add(dialog);
        }

        void removeDialog(VBox dialog) {
            rootPane.getChildren().remove(dialog);
        }

        VBox createLabeledControl(String labelText, Control control) {
            Label label = new Label(labelText);
            return new VBox(5, label, control);
        }

        Button createActionButton(String text) {
            Button button = new Button(text);
            button.getStyleClass().clear();
            button.getStyleClass().add(EnumGenerals.CSS_DIALOG_BUTTON);
            return button;
        }
    }

    /* -------------------------------- */
    /* ------ Interface Methods   ------ */
    /* -------------------------------- */
    @Override
    public void setProfile(Profile profile) {
        if (profile != null) {
            this.profile = profile;
            updateTable();
        } else {
            logger.error("Null profile provided. Update aborted.");
        }
    }
}