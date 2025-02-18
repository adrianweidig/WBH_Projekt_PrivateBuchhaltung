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

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * The Controller_goals class manages the display and interaction of goals in the application.
 * It allows users to view, add, edit, and delete goals.
 * Goals are shown in a TableView and changes are reflected in the user's Profile.
 *
 * Implements the ProfileAware interface to receive and update the user profile.
 */
public class Controller_goals implements ProfileAware {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private final Logger logger = LoggerFactory.getLogger(Controller_goals.class);
    private Profile profile = new Profile();
    // Modern date formatting shared by all users.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Common constant for spacing in HBox layouts.
    private static final int HBOX_SPACING = 10;

    @FXML
    private TableView<Goal> goalTable;
    @FXML
    private TableColumn<Goal, String> completedColumn;
    @FXML
    private TableColumn<Goal, String> nameColumn;
    @FXML
    private TableColumn<Goal, Double> goalValue;
    @FXML
    private TableColumn<Goal, String> descriptionColumn;
    @FXML
    private TableColumn<Goal, String> bankAccountColumn;
    @FXML
    private TableColumn<Goal, Void> actionColumn;
    @FXML
    private StackPane rootPane;

    // Instance of DialogButtonHelper (contains user-specific states).
    private DialogButtonHelper dialogButtonHelper = null;

    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    /**
     * Initializes the Controller_goals after the FXML root element has been loaded.
     * It sets up the goal table and initializes the dialog button helper.
     */
    @FXML
    public void initialize() {
        setupGoalTable();
        this.dialogButtonHelper = new DialogButtonHelper(this.rootPane);
    }

    /* -------------------------------- */
    /* ------ Private Methods    ------ */
    /* -------------------------------- */

    /**
     * Configures the goal table by binding table columns to Goal properties,
     * setting up cell factories for custom formatting, and applying CSS styles.
     */
    private void setupGoalTable() {
        // Bind the "name" property to the nameColumn.
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Bind the "completed" status to the completedColumn, displaying text based on boolean value.
        completedColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().isCompleted() ? "Erreicht" : "Nicht erreicht"));

        // Bind the "description" property to the descriptionColumn.
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Bind the "goalValue" property to the goalValue column.
        goalValue.setCellValueFactory(new PropertyValueFactory<>("goalValue"));

        // Bind bank account details to bankAccountColumn.
        bankAccountColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(
                        cellData.getValue().getBankAccount().getName()
                                .concat("\nKontostand: ")
                                .concat(Double.toString(cellData.getValue().getBankAccount().getBalance()))
                ));

        // Enable text wrapping for the description column to handle long text.
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

        // Set up the action column with edit and delete buttons.
        actionColumn.setCellFactory(createActionCellFactory());

        // Configure table behavior.
        goalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        goalTable.setPlaceholder(new Label("No goals found"));

        // Set the table items to the goals from the profile.
        goalTable.setItems(profile.getGoals());

        // Register a listener to update the table when the goals list changes.
        profile.getGoals().addListener((ListChangeListener<Goal>) change -> updateTable());

        // Apply styling to the table and columns.
        goalTable.getStyleClass().add(EnumGenerals.CSS_TABLE_VIEW);
        rootPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/styles/style_contentpane.css"))
                        .toExternalForm()
        );
        nameColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);
        descriptionColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);
        bankAccountColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);
        actionColumn.getStyleClass().add(EnumGenerals.CSS_TABLE_COLUMN);

        // Force layout update on the root pane.
        Platform.runLater(() -> {
            rootPane.applyCss();
            rootPane.layout();
        });
    }

    /**
     * Creates and returns a cell factory for the action column.
     * Each cell includes an edit and a delete button.
     *
     * @return a Callback for creating TableCell objects for the action column.
     */
    private Callback<TableColumn<Goal, Void>, TableCell<Goal, Void>> createActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("", new FontIcon("fas-edit"));
            private final Button deleteButton = new Button("", new FontIcon("fas-trash"));

            {
                // Apply CSS classes to the buttons.
                editButton.getStyleClass().add(EnumGenerals.CSS_DIALOG_BUTTON);
                deleteButton.getStyleClass().add(EnumGenerals.CSS_DIALOG_BUTTON);

                // Set action for edit button to open the edit dialog.
                editButton.setOnAction(event -> showEditDialog(getTableView().getItems().get(getIndex())));
                // Set action for delete button to open the delete confirmation dialog.
                deleteButton.setOnAction(event -> showDeleteConfirmation(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                // Create an HBox to hold both buttons with proper spacing.
                HBox hBox = new HBox(HBOX_SPACING, editButton, deleteButton);
                hBox.setAlignment(Pos.CENTER);
                setGraphic(empty ? null : hBox);
            }
        };
    }

    /**
     * Updates the goal table by aggregating the current goals from the profile and refreshing the table view.
     */
    private void updateTable() {
        ObservableList<Goal> allGoals = FXCollections.observableArrayList(profile.getGoals());
        goalTable.setItems(allGoals);
        goalTable.refresh();
    }

    /**
     * Displays a confirmation dialog for deleting the specified goal.
     *
     * @param goal the Goal to be deleted.
     */
    private void showDeleteConfirmation(Goal goal) {
        DeleteDialogButton deleteDialogButton = new DeleteDialogButton(this.rootPane);
        deleteDialogButton.show("dieses Ziel", v -> {
            // Remove the goal from the profile and update the table.
            profile.getGoals().remove(goal);
            Controller_goals.this.updateTable();
        });
    }

    /**
     * Displays an edit dialog for the specified goal, allowing the user to modify its properties.
     *
     * @param goal the Goal to be edited.
     */
    private void showEditDialog(Goal goal) {
        VBox dialogContent = new VBox(10);
        dialogContent.getStyleClass().add(EnumGenerals.CSS_DIALOG_BOX);

        // Create input fields pre-populated with the current goal values.
        TextField nameField = new TextField(goal.getName());
        TextField descriptionField = new TextField(goal.getDescription());
        DatePicker startDatePicker = new DatePicker(goal.getStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        DatePicker endDatePicker = new DatePicker(goal.getEndDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        TextField goalValueField = new TextField(Double.toString(goal.getGoalValue()));

        // Create a ComboBox for selecting a bank account.
        ComboBox<BankAccount> bankAccountComboBox = new ComboBox<>(profile.getBankAccounts());
        bankAccountComboBox.setValue(goal.getBankAccount());

        Button saveButton = dialogButtonHelper.createActionButton("Speichern");
        Button closeButton = dialogButtonHelper.createActionButton("Schließen");

        // Set the save action with field validation.
        saveButton.setOnAction(e -> {
            ValidationHelperFX validationHelper = new ValidationHelperFX();
            if (validationHelper.validateMandatoryFieldsFX(nameField, descriptionField, bankAccountComboBox, startDatePicker, endDatePicker)
                    && validationHelper.isValidAmount(goalValueField)) {

                // Update the goal with the new values.
                goal.setName(nameField.getText());
                goal.setDescription(descriptionField.getText());
                goal.setStartDate(java.sql.Date.valueOf(startDatePicker.getValue()));
                goal.setEndDate(java.sql.Date.valueOf(endDatePicker.getValue()));
                goal.setGoalValue(Double.parseDouble(goalValueField.getText()));

                goalTable.refresh();
                dialogButtonHelper.removeDialog(dialogContent);
            }
        });

        // Close the dialog without saving changes.
        closeButton.setOnAction(e -> dialogButtonHelper.removeDialog(dialogContent));

        // Build the dialog content layout.
        dialogContent.getChildren().addAll(
                dialogButtonHelper.createLabeledControl("Name:", nameField),
                dialogButtonHelper.createLabeledControl("Beschreibung:", descriptionField),
                dialogButtonHelper.createLabeledControl("Konto:", bankAccountComboBox),
                dialogButtonHelper.createLabeledControl("Kontostand:", goalValueField),
                dialogButtonHelper.createLabeledControl("Start Datum:", startDatePicker),
                dialogButtonHelper.createLabeledControl("End Datum:", endDatePicker),
                new HBox(HBOX_SPACING, saveButton, closeButton)
        );

        dialogButtonHelper.showDialog(dialogContent);
    }

    /**
     * Displays a form dialog to add a new goal.
     * The form collects necessary information and validates the inputs before creating a new Goal.
     */
    @FXML
    private void showAddGoalForm() {
        VBox dialogContent = new VBox(10);
        dialogContent.getStyleClass().add(EnumGenerals.CSS_DIALOG_BOX);

        // Create empty input fields for a new goal.
        TextField nameField = new TextField();
        nameField.getStyleClass().add(EnumGenerals.CSS_TEXT_FIELD);

        TextField descriptionField = new TextField();
        descriptionField.getStyleClass().add(EnumGenerals.CSS_TEXT_FIELD);

        DatePicker startDatePicker = new DatePicker();
        configureDatePicker(startDatePicker);

        DatePicker endDatePicker = new DatePicker();
        configureDatePicker(endDatePicker);

        TextField goalValueField = new TextField();
        goalValueField.getStyleClass().add(EnumGenerals.CSS_TEXT_FIELD);

        // ComboBox for selecting a bank account.
        ComboBox<BankAccount> bankAccountComboBox = new ComboBox<>(this.profile.getBankAccounts());
        bankAccountComboBox.getStyleClass().add(EnumGenerals.CSS_COMBO_BOX);

        Button saveButton = dialogButtonHelper.createActionButton("Speichern");
        Button closeButton = dialogButtonHelper.createActionButton("Schließen");

        // Save button action: validate fields and create a new Goal.
        saveButton.setOnAction(e -> {
            boolean valid;
            ValidationHelperFX validationHelper = new ValidationHelperFX();

            valid = validationHelper.validateMandatoryFieldsFX(nameField, descriptionField, bankAccountComboBox, startDatePicker, endDatePicker);
            valid = valid && validationHelper.isValidAmount(goalValueField);
            if (valid) {
                try {
                    String name = nameField.getText();
                    String description = descriptionField.getText();
                    Date newStartDate = java.sql.Date.valueOf(startDatePicker.getValue());
                    Date newEndDate = java.sql.Date.valueOf(endDatePicker.getValue());
                    double goalValue = Double.parseDouble(goalValueField.getText());

                    // Create a new Goal with the provided values.
                    Goal goal = new Goal(name, description, goalValue, bankAccountComboBox.getValue(), newStartDate, newEndDate);
                    profile.addGoal(goal);

                    goalTable.refresh();
                    updateTable();
                    dialogButtonHelper.removeDialog(dialogContent);
                    logger.debug("Goal inserted: {}", goal);
                } catch (Exception ex) {
                    logger.error("Invalid input.", ex);
                }
            }
        });

        // Close the dialog without adding a goal.
        closeButton.setOnAction(e -> dialogButtonHelper.removeDialog(dialogContent));

        // Build the dialog layout.
        dialogContent.getChildren().addAll(
                dialogButtonHelper.createLabeledControl("Name:", nameField),
                dialogButtonHelper.createLabeledControl("Beschreibung:", descriptionField),
                dialogButtonHelper.createLabeledControl("Ziel Account:", bankAccountComboBox),
                dialogButtonHelper.createLabeledControl("Zielwert:", goalValueField),
                dialogButtonHelper.createLabeledControl("Start:", startDatePicker),
                dialogButtonHelper.createLabeledControl("Ende:", endDatePicker),
                new HBox(HBOX_SPACING, saveButton, closeButton)
        );

        dialogButtonHelper.showDialog(dialogContent);
    }

    /**
     * Configures a DatePicker by adding the appropriate CSS class and ensuring that
     * clicking on the editor opens the calendar popup.
     *
     * @param datePicker the DatePicker to configure.
     */
    private void configureDatePicker(DatePicker datePicker) {
        datePicker.getStyleClass().add(EnumGenerals.CSS_DATE_PICKER);
        datePicker.getEditor().setOnMouseClicked(event -> datePicker.show());
    }

    /* -------------------------------- */
    /* ------ Interface Methods   ------ */
    /* -------------------------------- */

    /**
     * Sets the user profile for this controller and updates the goal table accordingly.
     *
     * @param profile the Profile to set.
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
