package wbh.wbh_projekt_privatebuchhaltung.controller;

import com.jpro.webapi.WebAPI;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import one.jpro.platform.file.ExtensionFilter;
import one.jpro.platform.file.picker.FileOpenPicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for the start view of the application.
 * Manages profile creation, loading, and transitions to the main view.
 */
public class Controller_start {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private final Logger logger = LoggerFactory.getLogger(Controller_start.class);
    private final Controller_data dataController = new Controller_data();
    private WebAPI webAPI = null;
    private HostServices hostServices = null;

    /* -------------------------------- */
    /* ------ FXML Variables ------ */
    /* -------------------------------- */

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btn_createprofile;

    @FXML
    private Button btn_loadprofile;

    @FXML
    private Button btn_createsampleprofile;

    @FXML
    private VBox vbox_background;
    private File tempFile;

    /* -------------------------------- */
    /* ------ FXML Methods ------ */
    /* -------------------------------- */

    /**
     * Handles the action for creating a new profile.
     * Initializes a new profile with sample badges and transitions to the main view.
     *
     * @param event the button click event.
     */
    @FXML
    void onaction_createprofile(ActionEvent event) {
        Profile profile = new Profile();
        // Add sample badges to the profile
        profile.addBadge(new Badge("1st Goal reached!", 1, false, null));
        profile.addBadge(new Badge("3rd Goal reached!", 3, false, null));
        profile.addBadge(new Badge("5th Goal reached!", 5, false, null));
        profile.addBadge(new Badge("10th Goal reached!", 10, false, null));

        try {
            this.loadMainController(profile);
        } catch (IOException e) {
            logger.error("Failed to load the main controller with the provided profile.", e);
        }
    }

    /**
     * Handles the action for saving a profile.
     * Initializes a sample profile, saves it to a temporary file,
     * and then opens a file save picker for the user to select the target location.
     *
     * @param event the button click event.
     * @throws ParseException if date parsing fails.
     */
    @FXML
    void onaction_createSampleProfile(ActionEvent event) throws ParseException {
        Profile profile = new Profile();

        // For testing the save function: populate the profile with example data
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

        profile.getUserSettings().setBirthday(dateFormat.parse("1990-01-05"));
        profile.getUserSettings().setLanguage(Language.German);
        profile.getUserSettings().setName("Wbh");

        BankAccount bankAccount = new BankAccount("Sparkasse", 9999.99, dateFormat.parse("2025-01-10"));
        profile.addBankAccount(bankAccount);
        profile.addBankAccount(new BankAccount("Volksbank", 1234567.89, dateFormat.parse("2024-12-31")));

        TransactionCategory category = new TransactionCategory(1, "Sonstiges");
        profile.addCategory(category);

        profile.addIncome(new Income(111.99, category, bankAccount, dateFormat.parse("2023-12-31"), "Test-Income1"));
        profile.addIncome(new Income(222.99, category, bankAccount, dateFormat.parse("2024-12-31"), "Test-Income2"));
        profile.addExpense(new Expense(-333.99, category, bankAccount, dateFormat.parse("2025-12-31"), "Test-Expense1"));

        profile.addGoal(new Goal("Test", "Mein Sparziel bis 2026", 99999.99, bankAccount, dateFormat.parse("2025-01-31"), dateFormat.parse("2025-12-31")));

        profile.addBadge(new Badge("1st Goal reached!", 1, false, null));
        profile.addBadge(new Badge("3rd Goal reached!", 3, false, null));
        profile.addBadge(new Badge("5th Goal reached!", 5, false, null));
        profile.addBadge(new Badge("10th Goal reached!", 10, false, null));

        // Save the profile to a temporary file
        this.tempFile = new File(System.getProperty("java.io.tmpdir"), "profile_temp.sqlite");
        dataController.saveProfile("jdbc:sqlite:" + tempFile.getAbsolutePath(), profile);

        try {
            this.loadMainController(profile);
        } catch (IOException e) {
            logger.error("Failed to load the main controller with the provided profile.", e);
        }
    }

    /**
     * Initializes FXML components. This method is automatically called after the FXML file is loaded.
     */
    @FXML
    void initialize() {
        assert btn_createprofile != null : "fx:id=\"btn_createprofile\" was not injected: check your FXML file 'view_start.fxml'.";
        assert btn_loadprofile != null : "fx:id=\"btn_loadprofile\" was not injected: check your FXML file 'view_start.fxml'.";
        assert btn_createsampleprofile != null : "fx:id=\"btn_createsampleprofile\" was not injected: check your FXML file 'view_start.fxml'.";
        assert vbox_background != null : "fx:id=\"vbox_background\" was not injected: check your FXML file 'view_start.fxml'.";

        /*
         * Handles the action for loading an existing profile.
         * Opens a file picker to select a SQLite database file and loads the profile.
         */

        // Create the FileOpenPicker (internally uses the web variant if WebAPI is available)
        FileOpenPicker fileOpenPicker = FileOpenPicker.create(btn_loadprofile);
        ExtensionFilter sqliteFilter = ExtensionFilter.of("SQLite Database", ".sqlite");
        fileOpenPicker.getExtensionFilters().clear();
        fileOpenPicker.getExtensionFilters().add(sqliteFilter);
        fileOpenPicker.setSelectedExtensionFilter(sqliteFilter);
        fileOpenPicker.setSelectionMode(SelectionMode.SINGLE);

        fileOpenPicker.setOnFilesSelected(fileSources -> {
            if (!fileSources.isEmpty()) {
                fileSources.getFirst().uploadFileAsync().thenAccept(file -> {
                    // Get the absolute file path
                    String filePath = file.getAbsolutePath();

                    // Load the profile from the selected SQLite file
                    Profile profile = dataController.loadData("jdbc:sqlite:" + filePath);

                    // Delete the SQLite file and its folder if empty
                    try {
                        Path fileToDelete = Path.of(filePath);

                        // Delete the file if it exists
                        Files.deleteIfExists(fileToDelete);
                        logger.info("SQLite file deleted: " + filePath);

                        // Check if the parent directory is empty after deleting the file
                        Path parentDir = fileToDelete.getParent();
                        if (parentDir != null && Files.isDirectory(parentDir) && Files.list(parentDir).findAny().isEmpty()) {
                            // Delete the directory if it is empty
                            Files.deleteIfExists(parentDir);
                            logger.info("Directory deleted: " + parentDir);
                        }
                    } catch (IOException e) {
                        logger.error("Error while deleting SQLite file or directory", e);
                    }

                    // Load the main controller with the loaded profile
                    try {
                        loadMainController(profile);
                    } catch (IOException e) {
                        logger.error("Error while loading the main controller", e);
                    }
                }).exceptionally(ex -> {
                    // Handle exceptions that occur during file upload or processing
                    logger.error("Error while loading the file", ex);
                    return null;
                });
            } else {
                // Log a warning if no file was selected
                logger.warn("No file selected.");
            }
        });
    }
    /* -------------------------------- */
    /* ------ Private Methods ------ */
    /* -------------------------------- */

    /**
     * Loads the main controller and initializes it with the given profile.
     *
     * @param profile the profile object to pass to the main controller.
     * @throws IOException if there is an issue loading the FXML file.
     */
    protected void loadMainController(Profile profile) throws IOException {
        logger.info("Main window is loading.");

        // Get the current stage from the background VBox
        Stage primaryStage = (Stage) this.vbox_background.getScene().getWindow();

        // Load resource bundle for internationalization
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                "wbh/wbh_projekt_privatebuchhaltung/i18n/text_controls", Locale.GERMAN
        );

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setResources(resourceBundle);
        fxmlLoader.setLocation(this.getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_main.fxml"));
        Parent fxmlRoot = fxmlLoader.load();

        // Set up the scene with CSS styling
        Scene scene = new Scene(fxmlRoot);
        scene.getStylesheets().add(
                Objects.requireNonNull(this.getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/styles/style_main.css"))
                        .toExternalForm()
        );

        primaryStage.setScene(scene);
        logger.info("Main window loaded successfully.");

        // Get the main controller instance and initialize it with the profile
        Controller_main mainController = fxmlLoader.getController();
        mainController.setProfile(profile);
        mainController.handleDashboard();

        try {
            mainController.setHostServices(this.hostServices);
            mainController.setWebAPI(this.webAPI);
            logger.info("Web-API successfully initialized.");
        } catch (RuntimeException e) {
            logger.warn("NOTE: Application is running as a desktop application.", e);
        }
    }

    /* -------------------------------- */
    /* ------ Public Methods ------ */
    /* -------------------------------- */

    /**
     * Sets the WebAPI instance for this controller.
     * Ensures that the WebAPI is only initialized once.
     *
     * @param webAPI the WebAPI instance to set.
     */
    public void setWebAPI(final WebAPI webAPI) {
        if (this.webAPI == null) {
            this.webAPI = webAPI;
        }
    }

    /**
     * Sets the HostServices instance for this controller.
     * Ensures that HostServices is only initialized once.
     *
     * @param hostServices the HostServices instance to set.
     */
    public void setHostServices(final HostServices hostServices) {
        if (this.hostServices == null) {
            this.hostServices = hostServices;
        }
    }
}
