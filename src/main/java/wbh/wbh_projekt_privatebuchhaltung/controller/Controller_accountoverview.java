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
import wbh.wbh_projekt_privatebuchhaltung.models.interfaces.ProfileAware;
import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * Controller_accountoverview verwaltet die Übersicht über Transaktionen.
 * Es werden Transaktionen (Einnahmen und Ausgaben) in einer TableView angezeigt und
 * über verschiedene Dialoge (zum Hinzufügen, Bearbeiten und Löschen) bearbeitet.
 *
 * Wichtige Hinweise:
 * - Die Validierung erfolgt über den selbst implementierten ValidationHelperFX, der fehlerhafte Eingaben
 *   durch rote Markierung und sanfte Hintergrundfüllung hervorhebt.
 * - Wiederverwendbare Logik, z.B. zum Erzeugen von Dialog-Buttons, wurde in eigene Methoden ausgelagert.
 * - Die inneren Klassen (DialogHelper und ValidationHelperFX) bleiben vorerst in dieser Datei.
 *   TODO: In Zukunft in separate Dateien auslagern.
 *
 * Implementiert das ProfileAware-Interface, um das Benutzerprofil zu empfangen und zu aktualisieren.
 */
public class Controller_accountoverview implements ProfileAware {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private final Logger logger = LoggerFactory.getLogger(Controller_accountoverview.class);
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
    private static final String DELETE_CONFIRMATION_MESSAGE = "Are you sure you want to delete this transaction?";
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

    // Instanz des DialogHelper (enthält benutzerspezifische Zustände)
    private final DialogHelper dialogHelper = new DialogHelper();

    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    /**
     * Initialisiert den Controller, nachdem das FXML-Root-Element vollständig geladen wurde.
     * Ruft die Methode zur Einrichtung der Transaktionstabelle auf.
     */
    @FXML
    public void initialize() {
        setupTransactionTable();
    }

    /* -------------------------------- */
    /* ------ Private Methods    ------ */
    /* -------------------------------- */

    /**
     * Richtet die Transaktionstabelle ein, indem die Spalten an die entsprechenden Eigenschaften
     * der Transaktion gebunden und die Tabelle mit den Transaktionen aus dem Profil befüllt wird.
     * Zudem werden CSS-Stile angewendet und Listener registriert, die die Tabelle bei Änderungen im Profil aktualisieren.
     *
     * TODO: Weitere Logik zur Befüllung und Listener-Registrierung könnte in separate Methoden ausgelagert werden.
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
        transactionTable.setPlaceholder(new Label("No transactions found"));

        // Combine incomes and expenses
        ObservableList<Transaction> allTransactions = FXCollections.observableArrayList();
        allTransactions.addAll(profile.getIncomes());
        allTransactions.addAll(profile.getExpenses());
        transactionTable.setItems(allTransactions);

        // Add change listeners
        profile.getIncomes().addListener((ListChangeListener<Transaction>) change -> updateTable());
        profile.getExpenses().addListener((ListChangeListener<Transaction>) change -> updateTable());

        // Apply styling
        transactionTable.getStyleClass().add(CSS_TABLE_VIEW);
        rootPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/styles/style_contentpane.css"))
                        .toExternalForm()
        );

        // Apply CSS classes to columns
        dateColumn.getStyleClass().add(CSS_TABLE_COLUMN);
        descriptionColumn.getStyleClass().add(CSS_TABLE_COLUMN);
        amountColumn.getStyleClass().add(CSS_TABLE_COLUMN);
        typeColumn.getStyleClass().add(CSS_TABLE_COLUMN);
        actionColumn.getStyleClass().add(CSS_TABLE_COLUMN);

        // Force layout update
        Platform.runLater(() -> {
            rootPane.applyCss();
            rootPane.layout();
        });
    }

    /**
     * Erzeugt und liefert eine Cell-Factory für die Aktionsspalte.
     * Jede nicht-leere Zelle erhält zwei Buttons (Bearbeiten und Löschen).
     *
     * @return Callback für die Zellen-Erzeugung.
     */
    private Callback<TableColumn<Transaction, Void>, TableCell<Transaction, Void>> createActionCellFactory() {
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

    /**
     * Aggregiert Einnahmen und Ausgaben neu und aktualisiert die Transaktionstabelle.
     *
     * TODO: Prüfen, ob ein diff-basierter Update-Mechanismus die Performance verbessert.
     */
    private void updateTable() {
        ObservableList<Transaction> allTransactions = FXCollections.observableArrayList(profile.getIncomes());
        allTransactions.addAll(profile.getExpenses());
        transactionTable.setItems(allTransactions);
        transactionTable.refresh();
    }

    /**
     * Zeigt einen Bestätigungsdialog zum Löschen der angegebenen Transaktion an.
     * Der Dialog wird mit CSS-Klassen gestylt und dem Root-Pane hinzugefügt.
     *
     * TODO: Die Dialogerstellung könnte weiter in einen Builder ausgelagert werden.
     *
     * @param transaction die zu löschende Transaktion.
     */
    private void showDeleteConfirmation(Transaction transaction) {
        VBox dialogContent = dialogHelper.createDialogContainer();
        dialogContent.getStyleClass().add(CSS_DELETE_DIALOG);

        Label confirmationLabel = new Label(DELETE_CONFIRMATION_MESSAGE);
        Button confirmButton = dialogHelper.createActionButton("Delete");
        Button cancelButton = dialogHelper.createActionButton("Cancel");

        confirmButton.setOnAction(e -> {
            boolean success = true;
            if (transaction instanceof Income) {
                profile.getIncomes().remove(transaction);
            } else if (transaction instanceof Expense) {
                profile.getExpenses().remove(transaction);
            } else {
                success = false;
            }
            if (success) {
                dialogHelper.removeDialog(dialogContent);
                updateTable();
                logger.debug("Transaktion gelöscht: {}", transaction);
            }
        });
        cancelButton.setOnAction(e -> dialogHelper.removeDialog(dialogContent));

        dialogContent.getChildren().addAll(confirmationLabel, new HBox(HBOX_SPACING, confirmButton, cancelButton));
        dialogHelper.showDialog(dialogContent);
    }

    /**
     * Zeigt einen Bearbeitungsdialog für die angegebene Transaktion an.
     * Im Dialog können Beschreibung, Datum, Betrag und Kategorie geändert werden.
     * Es wird ein DatePicker zur Datumsauswahl verwendet.
     *
     * TODO: Weitere Layoutlogik könnte bei Bedarf ausgelagert werden.
     *
     * @param transaction die zu bearbeitende Transaktion.
     */
    private void showEditDialog(Transaction transaction) {
        VBox dialogContent = dialogHelper.createDialogContainer();

        TextField descriptionField = new TextField(transaction.getDescription());
        descriptionField.getStyleClass().add(CSS_TEXT_FIELD);

        DatePicker datePicker = new DatePicker();
        configureDatePicker(datePicker);
        LocalDate localDate = new java.sql.Date(transaction.getDate().getTime()).toLocalDate();
        datePicker.setValue(localDate);

        TextField amountField = new TextField(Double.toString(transaction.getValue()));
        amountField.getStyleClass().add(CSS_TEXT_FIELD);

        ComboBox<TransactionCategory> typeComboBox = new ComboBox<>(this.profile.getCategories());
        typeComboBox.getStyleClass().add(CSS_COMBO_BOX);
        typeComboBox.setValue(transaction.getCategory());

        Button saveButton = dialogHelper.createActionButton("Save");
        Button closeButton = dialogHelper.createActionButton("Close");

        saveButton.setOnAction(e -> {
            boolean valid = true;
            valid = valid && ValidationHelperFX.validateMandatoryFieldsFX(descriptionField, datePicker, amountField, typeComboBox);
            valid = valid && ValidationHelperFX.isValidAmount(amountField.getText());
            if (valid) {
                try {
                    Date newDate = java.sql.Date.valueOf(datePicker.getValue());
                    transaction.setDescription(descriptionField.getText());
                    transaction.setDate(newDate);
                    transaction.setValue(Double.parseDouble(amountField.getText()));
                    transaction.setCategory(typeComboBox.getValue());
                    transactionTable.refresh();
                    dialogHelper.removeDialog(dialogContent);
                    logger.debug("Transaktion aktualisiert: {}", transaction);
                    valid = true;
                } catch (Exception ex) {
                    logger.error("Ungültige Eingabe.", ex);
                    valid = false;
                }
            } else {
                if (!ValidationHelperFX.isValidAmount(amountField.getText())) {
                    if (!amountField.getStyleClass().contains(CSS_ERROR)) {
                        amountField.getStyleClass().add(CSS_ERROR);
                    }
                } else {
                    amountField.getStyleClass().removeAll(CSS_ERROR);
                }
            }
        });
        closeButton.setOnAction(e -> dialogHelper.removeDialog(dialogContent));

        dialogContent.getChildren().addAll(
                dialogHelper.createLabeledControl("Edit Beschreibung:", descriptionField),
                dialogHelper.createLabeledControl("Edit Datum:", datePicker),
                dialogHelper.createLabeledControl("Edit Betrag:", amountField),
                dialogHelper.createLabeledControl("Edit Kategorie:", typeComboBox),
                new HBox(HBOX_SPACING, saveButton, closeButton)
        );
        dialogHelper.showDialog(dialogContent);
    }

    /**
     * Zeigt einen Dialog zum Hinzufügen einer neuen Transaktion an.
     * Es wird ein DatePicker zur Datumsauswahl verwendet und alle Pflichtfelder werden validiert.
     * Der Transaktionstyp (Einnahme oder Ausgabe) wird anhand des Betrags bestimmt.
     *
     * TODO: In Zukunft könnte das Dialoglayout in eine separate FXML-Datei ausgelagert werden.
     *
     * @see Income
     * @see Expense
     */
    @FXML
    private void showAddTransactionForm() {
        VBox dialogContent = dialogHelper.createDialogContainer();

        TextField descriptionField = new TextField();
        descriptionField.getStyleClass().add(CSS_TEXT_FIELD);
        descriptionField.setPromptText("Transaktionsbeschreibung eingeben");

        DatePicker datePicker = new DatePicker();
        configureDatePicker(datePicker);
        datePicker.setPromptText("Datum auswählen");

        TextField amountField = new TextField();
        amountField.getStyleClass().add(CSS_TEXT_FIELD);
        amountField.setPromptText("Betrag eingeben (positiv = Einnahme, negativ = Ausgabe)");

        ComboBox<TransactionCategory> categoryComboBox = new ComboBox<>(profile.getCategories());
        categoryComboBox.getStyleClass().add(CSS_COMBO_BOX);
        categoryComboBox.setPromptText("Kategorie auswählen");

        ComboBox<BankAccount> accountComboBox = new ComboBox<>(profile.getBankAccounts());
        accountComboBox.getStyleClass().add(CSS_COMBO_BOX);
        accountComboBox.setPromptText("Bankkonto auswählen");

        Button saveButton = dialogHelper.createActionButton("Save");
        Button closeButton = dialogHelper.createActionButton("Close");

        saveButton.setOnAction(e -> {
            boolean valid = true;
            valid = valid && ValidationHelperFX.validateMandatoryFieldsFX(descriptionField, datePicker, amountField, categoryComboBox, accountComboBox);
            valid = valid && ValidationHelperFX.isValidAmount(amountField.getText());
            if (valid) {
                try {
                    double value = Double.parseDouble(amountField.getText());
                    Date date = java.sql.Date.valueOf(datePicker.getValue());
                    TransactionCategory category = categoryComboBox.getValue();
                    BankAccount account = accountComboBox.getValue();
                    String description = descriptionField.getText();
                    if (value >= 0) {
                        profile.getIncomes().add(new Income(value, category, account, date, description));
                    } else {
                        profile.getExpenses().add(new Expense(Math.abs(value), category, account, date, description));
                    }
                    updateTable();
                    dialogHelper.removeDialog(dialogContent);
                    valid = true;
                } catch (Exception ex) {
                    logger.error("Ungültiges Eingabeformat!", ex);
                    valid = false;
                }
            } else {
                if (!ValidationHelperFX.isValidAmount(amountField.getText())) {
                    if (!amountField.getStyleClass().contains(CSS_ERROR)) {
                        amountField.getStyleClass().add(CSS_ERROR);
                    }
                } else {
                    amountField.getStyleClass().removeAll(CSS_ERROR);
                }
            }
        });
        closeButton.setOnAction(e -> dialogHelper.removeDialog(dialogContent));

        dialogContent.getChildren().addAll(
                dialogHelper.createLabeledControl("Beschreibung:", descriptionField),
                dialogHelper.createLabeledControl("Datum:", datePicker),
                dialogHelper.createLabeledControl("Betrag:", amountField),
                dialogHelper.createLabeledControl("Kategorie:", categoryComboBox),
                dialogHelper.createLabeledControl("Bankkonto:", accountComboBox),
                new HBox(HBOX_SPACING, saveButton, closeButton)
        );
        dialogHelper.showDialog(dialogContent);
    }

    /**
     * Formatiert ein Datum mithilfe des angegebenen Datumsformats.
     * Verwendet Java 8's DateTimeFormatter für moderne Datumsbehandlung.
     *
     * @param date das zu formatierende Datum
     * @return der formatierte Datum-String oder ein leerer String, wenn das Datum null ist
     */
    private String formatDate(Date date) {
        String result = "";
        result = (date == null) ? "" : DATE_FORMATTER.format(
                new java.sql.Date(date.getTime()).toLocalDate()
        );
        return result;
    }

    /**
     * Konfiguriert einen DatePicker, indem die entsprechende CSS-Klasse angewendet wird
     * und sichergestellt wird, dass beim Klicken auf den Editor der Kalender geöffnet wird.
     *
     * @param datePicker der zu konfigurierende DatePicker
     */
    private void configureDatePicker(DatePicker datePicker) {
        datePicker.getStyleClass().add(CSS_DATE_PICKER);
        datePicker.getEditor().setOnMouseClicked(event -> datePicker.show());
    }

    /* -------------------------------- */
    /* ------ Inner Classes      ------ */
    /* -------------------------------- */

    /**
     * DialogHelper kapselt Methoden zur Erstellung und Verwaltung von Dialogen.
     * Diese innere Klasse gruppiert Methoden, die Dialog-Container, beschriftete Steuerelemente
     * erstellen und das Anzeigen sowie Entfernen von Dialogen aus dem Root-Pane übernehmen.
     */
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

        /**
         * Erstellt einen Dialog-Action-Button mit dem angegebenen Text.
         * Dieser Button erhält die CSS-Klasse "dialog-action-button", die in der CSS-Datei definiert sein sollte.
         *
         * @param text der Text des Buttons
         * @return ein neu erstellter Button
         */
        Button createActionButton(String text) {
            Button button = new Button(text);
            button.getStyleClass().clear();
            button.getStyleClass().add(CSS_DIALOG_ACTION_BUTTON);
            return button;
        }
    }

    /**
     * ValidationHelperFX kapselt Methoden zur Validierung von Benutzereingaben.
     * Diese Methoden verwenden einen Akkumulator, um sicherzustellen, dass jede Methode nur einen Rückgabewert besitzt.
     * Diese Klasse ist als static deklariert, da sie keine benutzerspezifischen Zustände benötigt.
     */
    private static class ValidationHelperFX {
        /**
         * Validiert, dass die übergebenen Steuerelemente nicht leer sind.
         *
         * @param controls ein Array von Steuerelementen, die validiert werden sollen
         * @return true, wenn alle Steuerelemente gültig sind; false sonst
         */
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

        /**
         * Validiert, dass der eingegebene Betrag ein gültiger Geldwert ist (numerisch und ungleich 0).
         *
         * @param input der eingegebene Betrag als String
         * @return true, wenn der Betrag gültig ist; false sonst
         */
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

    /**
     * Setzt das Profil für diesen Controller und aktualisiert die Transaktionstabelle entsprechend.
     *
     * @param profile das zu setzende Profil
     *
     * TODO: Das Profil vor dem Update validieren und ggf. andere Komponenten benachrichtigen.
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
