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
 * Controller_accountoverview verwaltet die Übersicht über Transaktionen.
 * Es werden Transaktionen (Einnahmen und Ausgaben) in einer TableView angezeigt und
 * über verschiedene Dialoge (zum Hinzufügen, Bearbeiten und Löschen) bearbeitet.
 *
 * Wichtige Hinweise:
 * - Die Validierung erfolgt über den selbst implementierten ValidationHelperFX, der fehlerhafte Eingaben
 *   durch rote Markierung und sanfte Hintergrundfüllung hervorhebt.
 * - Wiederverwendbare Logik, z.B. zum Erzeugen von Dialog-Buttons, wurde in eigene Methoden ausgelagert.
 * - Die inneren Klassen (DialogButtonHelper und ValidationHelperFX) bleiben vorerst in dieser Datei.
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

    // Gemeinsame Strings
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

    // Instanz des DialogButtonHelper (enthält benutzerspezifische Zustände)
    private DialogButtonHelper dialogButtonHelper = null;

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
        this.dialogButtonHelper = new DialogButtonHelper(this.rootPane);
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
        transactionTable.setPlaceholder(new Label("Keine Transaktionen gefunden"));

        // Combine incomes and expenses
        ObservableList<Transaction> allTransactions = FXCollections.observableArrayList();
        allTransactions.addAll(profile.getIncomes());
        allTransactions.addAll(profile.getExpenses());
        transactionTable.setItems(allTransactions);

        // Add change listeners
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
                editButton.getStyleClass().add(EnumGenerals.CSS_DIALOG_BUTTON);
                deleteButton.getStyleClass().add(EnumGenerals.CSS_DIALOG_BUTTON);
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
        DeleteDialogButton deleteDialogButton = new DeleteDialogButton(this.rootPane);
        deleteDialogButton.show("diese Transaktion", v -> {
            boolean success = true;
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
     * Zeigt einen Bearbeitungsdialog für die angegebene Transaktion an.
     * Im Dialog können Beschreibung, Datum, Betrag und Kategorie geändert werden.
     * Es wird ein DatePicker zur Datumsauswahl verwendet.
     *
     * TODO: Weitere Layoutlogik könnte bei Bedarf ausgelagert werden.
     *
     * @param transaction die zu bearbeitende Transaktion.
     */
    private void showEditDialog(Transaction transaction) {
        VBox dialogContent = new VBox(10);
        dialogContent.getStyleClass().add(EnumGenerals.CSS_DIALOG_BOX);

        TextField descriptionField = new TextField(transaction.getDescription());
        descriptionField.getStyleClass().add(EnumGenerals.CSS_TEXT_FIELD);

        DatePicker datePicker = new DatePicker();
        configureDatePicker(datePicker);
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
     * Zeigt einen Dialog zum Hinzufügen einer neuen Transaktion an.
     * Es wird ein DatePicker zur Datumsauswahl verwendet und alle Pflichtfelder werden validiert.
     * Der Transaktionstyp (Einnahme oder Ausgabe) wird anhand des Betrags bestimmt.
     *
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
        datePicker.getStyleClass().add(EnumGenerals.CSS_DATE_PICKER);
        datePicker.getEditor().setOnMouseClicked(event -> datePicker.show());
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
