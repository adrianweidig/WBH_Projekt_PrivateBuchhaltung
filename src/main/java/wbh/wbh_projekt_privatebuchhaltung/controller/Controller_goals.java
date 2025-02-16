package wbh.wbh_projekt_privatebuchhaltung.controller;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
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
import wbh.wbh_projekt_privatebuchhaltung.models.interfaces.ProfileAware;
import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;


public class Controller_goals implements ProfileAware {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private final Logger logger = LoggerFactory.getLogger(Controller_goals.class);
    private Profile profile = new Profile();
    // Moderne Datumsformatierung: Alle Nutzer teilen diesen DateTimeFormatter.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // CSS-Klassenkonstanten (global für alle Nutzer)
    private static final String CSS_ROOT = "root";
    private static final String CSS_TABLE_VIEW = "table-view";
    private static final String CSS_DIALOG_BOX = "dialog-box";
    private static final String CSS_DELETE_DIALOG = "delete-dialog";
    private static final String CSS_DIALOG_BUTTON = "dialog-button";
    private static final String CSS_TEXT_FIELD = "text-field";
    private static final String CSS_DATE_PICKER = "date-picker";
    private static final String CSS_COMBO_BOX = "combo-box";
    private static final String CSS_ERROR = "error";
    private static final String CSS_TABLE_COLUMN = "table-column";
    private static final String CSS_DIALOG_ACTION_BUTTON = "dialog-action-button";

    // Gemeinsame Strings
    private static final String DELETE_CONFIRMATION_MESSAGE = "Are you sure you want to delete this goal?";
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

    // Instanz des DialogHelper (enthält benutzerspezifische Zustände)
    private final DialogHelper dialogHelper = new DialogHelper();

    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    @FXML
    public void initialize() {
        setupGoalTable();
    }

    /* -------------------------------- */
    /* ------ Private Methods    ------ */
    /* -------------------------------- */

    private void setupGoalTable() {
        // Configure column data bindings

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        completedColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().isCompleted() ? "Erreicht" : "Nicht erreicht"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        goalValue.setCellValueFactory(new PropertyValueFactory<>("goalValue"));

        bankAccountColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(
                        cellData.getValue().getBankAccount().getName()
                                .concat("\nKontostand: ")
                                .concat((Double.toString(cellData.getValue().getBankAccount().getBalance())))));

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
        goalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        goalTable.setPlaceholder(new Label("No goals found"));

        // Combine incomes and expenses
        goalTable.setItems(profile.getGoals());

        // Add change listeners
        profile.getGoals().addListener((ListChangeListener<Goal>) change -> updateTable());

        // Apply styling
        goalTable.getStyleClass().add(CSS_TABLE_VIEW);
        rootPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/styles/style_contentpane.css"))
                        .toExternalForm()
        );

        // Apply CSS classes to columns
        nameColumn.getStyleClass().add(CSS_TABLE_COLUMN);
        descriptionColumn.getStyleClass().add(CSS_TABLE_COLUMN);
        bankAccountColumn.getStyleClass().add(CSS_TABLE_COLUMN);

        actionColumn.getStyleClass().add(CSS_TABLE_COLUMN);

        // Force layout update
        Platform.runLater(() -> {
            rootPane.applyCss();
            rootPane.layout();
        });
    }

    private Callback<TableColumn<Goal, Void>, TableCell<Goal, Void>> createActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("", new FontIcon("fas-edit"));
            private final Button deleteButton = new Button("", new FontIcon("fas-trash"));

            {
                // CSS-Klassen anwenden.
                editButton.getStyleClass().add(CSS_DIALOG_BUTTON);
                deleteButton.getStyleClass().add(CSS_DIALOG_BUTTON);
                // Beim Klick den entsprechenden Dialog öffnen.
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
        ObservableList<Goal> allGoals = FXCollections.observableArrayList(profile.getGoals());
        goalTable.setItems(allGoals);
        goalTable.refresh();
    }

    private void showDeleteConfirmation(Goal goal) {
        VBox dialogContent = dialogHelper.createDialogContainer();
        dialogContent.getStyleClass().add(CSS_DELETE_DIALOG);

        Label confirmationLabel = new Label(DELETE_CONFIRMATION_MESSAGE);
        Button confirmButton = dialogHelper.createActionButton("Delete");
        Button cancelButton = dialogHelper.createActionButton("Cancel");

        confirmButton.setOnAction(e -> {
            profile.getGoals().remove(goal);

            dialogHelper.removeDialog(dialogContent);
            updateTable();
            logger.debug("Goal deleted: {}", goal);

        });
        cancelButton.setOnAction(e -> dialogHelper.removeDialog(dialogContent));

        dialogContent.getChildren().addAll(confirmationLabel, new HBox(HBOX_SPACING, confirmButton, cancelButton));
        dialogHelper.showDialog(dialogContent);
    }

    private void showEditDialog(Goal goal) {
        VBox dialogContent = dialogHelper.createDialogContainer();

        TextField nameField = new TextField(goal.getName());
        nameField.getStyleClass().add(CSS_TEXT_FIELD);

        TextField descriptionField = new TextField(goal.getDescription());
        descriptionField.getStyleClass().add(CSS_TEXT_FIELD);

        DatePicker startDatePicker = new DatePicker();
        configureDatePicker(startDatePicker);
        LocalDate localStartDate = new java.sql.Date(goal.getStartDate().getTime()).toLocalDate();
        startDatePicker.setValue(localStartDate);

        DatePicker endDatePicker = new DatePicker();
        configureDatePicker(endDatePicker);
        LocalDate localEndDate = new java.sql.Date(goal.getEndDate().getTime()).toLocalDate();
        endDatePicker.setValue(localEndDate);

        TextField goalValueField = new TextField(Double.toString(goal.getGoalValue()));
        goalValueField.getStyleClass().add(CSS_TEXT_FIELD);

        ComboBox<BankAccount> bankAccountComboBox = new ComboBox<>(this.profile.getBankAccounts());
        bankAccountComboBox.getStyleClass().add(CSS_COMBO_BOX);
        bankAccountComboBox.setValue(goal.getBankAccount());

        Button saveButton = dialogHelper.createActionButton("Save");
        Button closeButton = dialogHelper.createActionButton("Close");

        saveButton.setOnAction(e -> {
            boolean valid = true;
            valid = valid && ValidationHelperFX.validateMandatoryFieldsFX(nameField, descriptionField, bankAccountComboBox, startDatePicker, endDatePicker);
            valid = valid && ValidationHelperFX.isValidAmount(goalValueField.getText());
            if (valid) {
                try {
                    Date newStartDate = java.sql.Date.valueOf(startDatePicker.getValue());
                    Date newEndDate = java.sql.Date.valueOf(endDatePicker.getValue());

                    goal.setDescription(descriptionField.getText());
                    goal.setStartDate(newStartDate);
                    goal.setEndDate(newEndDate);
                    goal.setGoalValue(Double.parseDouble(goalValueField.getText()));
                    goal.setName(nameField.getText());
                    goalTable.refresh();
                    dialogHelper.removeDialog(dialogContent);
                    logger.debug("Goal updated: {}", goal);
                    valid = true;
                } catch (Exception ex) {
                    logger.error("Ungültige Eingabe.", ex);
                    valid = false;
                }
            } else {
                if (!ValidationHelperFX.isValidAmount(goalValueField.getText())) {
                    if (!goalValueField.getStyleClass().contains(CSS_ERROR)) {
                        goalValueField.getStyleClass().add(CSS_ERROR);
                    }
                } else {
                    goalValueField.getStyleClass().removeAll(CSS_ERROR);
                }
            }
        });
        closeButton.setOnAction(e -> dialogHelper.removeDialog(dialogContent));

        dialogContent.getChildren().addAll(
                dialogHelper.createLabeledControl("Name:", nameField),
                dialogHelper.createLabeledControl("Beschreibung:", descriptionField),
                dialogHelper.createLabeledControl("Ziel-Konto:", bankAccountComboBox),
                dialogHelper.createLabeledControl("Ziel-Wert:", goalValueField),
                dialogHelper.createLabeledControl("Start Datum:", startDatePicker),
                dialogHelper.createLabeledControl("End Datum:", endDatePicker),

                new HBox(HBOX_SPACING, saveButton, closeButton)
        );
        dialogHelper.showDialog(dialogContent);
    }

    @FXML
    private void showAddGoalForm() {

        //________________
        VBox dialogContent = dialogHelper.createDialogContainer();

        TextField nameField = new TextField();
        nameField.getStyleClass().add(CSS_TEXT_FIELD);

        TextField descriptionField = new TextField();
        descriptionField.getStyleClass().add(CSS_TEXT_FIELD);

        DatePicker startDatePicker = new DatePicker();
        configureDatePicker(startDatePicker);

        DatePicker endDatePicker = new DatePicker();
        configureDatePicker(endDatePicker);

        TextField goalValueField = new TextField();
        goalValueField.getStyleClass().add(CSS_TEXT_FIELD);

        ComboBox<BankAccount> bankAccountComboBox = new ComboBox<>(this.profile.getBankAccounts());
        bankAccountComboBox.getStyleClass().add(CSS_COMBO_BOX);


        Button saveButton = dialogHelper.createActionButton("Save");
        Button closeButton = dialogHelper.createActionButton("Close");

        saveButton.setOnAction(e -> {
            boolean valid = true;
            valid = valid && ValidationHelperFX.validateMandatoryFieldsFX(nameField, descriptionField, bankAccountComboBox, startDatePicker, endDatePicker);
            valid = valid && ValidationHelperFX.isValidAmount(goalValueField.getText());
            if (valid) {
                try {
                    String name = nameField.getText();
                    String description =descriptionField.getText();

                    Date newStartDate = java.sql.Date.valueOf(startDatePicker.getValue());
                    Date newEndDate = java.sql.Date.valueOf(endDatePicker.getValue());

                    double goalvalue = (Double.parseDouble(goalValueField.getText()));

                    Goal goal  = new Goal( name,description,goalvalue, bankAccountComboBox.getValue(), newStartDate, newEndDate);
                    profile.addGoal(goal);

                    goalTable.refresh();
                    updateTable();
                    dialogHelper.removeDialog(dialogContent);
                    logger.debug("Goal inserted: {}", goal);
                    valid = true;
                } catch (Exception ex) {
                    logger.error("Ungültige Eingabe.", ex);
                    valid = false;
                }
            } else {
                if (!ValidationHelperFX.isValidAmount(goalValueField.getText())) {
                    if (!goalValueField.getStyleClass().contains(CSS_ERROR)) {
                        goalValueField.getStyleClass().add(CSS_ERROR);
                    }
                } else {
                    goalValueField.getStyleClass().removeAll(CSS_ERROR);
                }
            }
        });
        closeButton.setOnAction(e -> dialogHelper.removeDialog(dialogContent));

        dialogContent.getChildren().addAll(
                dialogHelper.createLabeledControl("Name:", nameField),
                dialogHelper.createLabeledControl("Beschreibung:", descriptionField),
                dialogHelper.createLabeledControl("Ziel-Konto:", bankAccountComboBox),
                dialogHelper.createLabeledControl("Ziel-Wert:", goalValueField),
                dialogHelper.createLabeledControl("Start Datum:", startDatePicker),
                dialogHelper.createLabeledControl("End Datum:", endDatePicker),
                new HBox(HBOX_SPACING, saveButton, closeButton)
        );
        dialogHelper.showDialog(dialogContent);

    }

    private String formatDate(Date date) {
        String result = "";
        result = (date == null) ? "" : DATE_FORMATTER.format(
                new java.sql.Date(date.getTime()).toLocalDate()
        );
        return result;
    }

    private void configureDatePicker(DatePicker datePicker) {
        datePicker.getStyleClass().add(CSS_DATE_PICKER);
        datePicker.getEditor().setOnMouseClicked(event -> datePicker.show());
    }

    /* -------------------------------- */
    /* ------ Inner Classes      ------ */
    /* -------------------------------- */

    private class DialogHelper {
        VBox createDialogContainer() {
            VBox dialog = new VBox(10);
            dialog.getStyleClass().add(CSS_DIALOG_BOX);
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
            button.getStyleClass().add(CSS_DIALOG_ACTION_BUTTON);
            return button;
        }
    }

    private static class ValidationHelperFX {
        static boolean validateMandatoryFieldsFX(Control... controls) {
            boolean valid = true;
            for (Control c : controls) {
                if (c instanceof TextField) {
                    TextField tf = (TextField) c;
                    valid = valid && (tf.getText() != null && !tf.getText().trim().isEmpty());
                    if (!(tf.getText() != null && !tf.getText().trim().isEmpty())) {
                        if (!tf.getStyleClass().contains(CSS_ERROR)) {
                            tf.getStyleClass().add(CSS_ERROR);
                        }
                    } else {
                        tf.getStyleClass().removeAll(CSS_ERROR);
                    }
                } else if (c instanceof DatePicker) {
                    DatePicker dp = (DatePicker) c;
                    valid = valid && (dp.getValue() != null);
                    if (dp.getValue() == null) {
                        if (!dp.getStyleClass().contains(CSS_ERROR)) {
                            dp.getStyleClass().add(CSS_ERROR);
                        }
                    } else {
                        dp.getStyleClass().removeAll(CSS_ERROR);
                    }
                } else if (c instanceof ComboBox<?>) {
                    ComboBox<?> cb = (ComboBox<?>) c;
                    valid = valid && (cb.getValue() != null);
                    if (cb.getValue() == null) {
                        if (!cb.getStyleClass().contains(CSS_ERROR)) {
                            cb.getStyleClass().add(CSS_ERROR);
                        }
                    } else {
                        cb.getStyleClass().removeAll(CSS_ERROR);
                    }
                }
            }
            return valid;
        }

        static boolean isValidAmount(String input) {
            boolean valid;
            try {
                double value = Double.parseDouble(input);
                valid = (value != 0);
            } catch (NumberFormatException e) {
                valid = false;
            }
            return valid;
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
            logger.error("Null-Profil übergeben. Update abgebrochen.");
        }
    }
}