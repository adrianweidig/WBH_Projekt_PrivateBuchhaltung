package wbh.wbh_projekt_privatebuchhaltung.controller;

import com.jpro.webapi.WebAPI;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.models.*;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for the start view of the application.
 * Manages profile creation and loading, and transitions to the main view.
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
    /* ------ FXML Variables     ------ */
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
    private Button btn_saveprofile;

    @FXML
    private VBox vbox_background;

    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    /**
     * Handles the action for creating a new profile.
     * Navigates to the main view after initializing a new profile.
     *
     * @param event The button click event.
     */
    @FXML
    void onaction_createprofile(ActionEvent event) {
        Profile profile = new Profile();

        try {
            this.loadMainController(profile);
        } catch (IOException e) {
            logger.error("Failed to load the main controller with the provided profile.", e);
        }
    }

    /**
     * Handles the action for loading an existing profile.
     * Navigates to the main view after initializing a profile.
     *
     * @param event The button click event.
     */
    @FXML
    void onaction_loadprofile(ActionEvent event) {
        Profile profile = dataController.loadData("jdbc:sqlite:db.sqlite");

        try {
            this.loadMainController(profile);
        } catch (IOException e) {
            logger.error("Failed to load the main controller with the provided profile.", e);
        }
    }

    /**
     * Handles the action for saving a profile.
     *
     * @param event The button click event.
     */
    @FXML
    void onaction_saveprofile(ActionEvent event) throws ParseException {
        Profile profile = new Profile();

        //for testing the save function: write Example Data in Profile
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);

        profile.getUserSettings().setBirthday(dateFormat.parse("1990-01-05"));
        profile.getUserSettings().setLanguage(Language.German);
        profile.getUserSettings().setName("Wbh");

        BankAccount bankAccount = new BankAccount("Sparkasse", 9999.99 , dateFormat.parse("2025-01-10"));
        profile.addBankAccount(bankAccount);
        profile.addBankAccount(new BankAccount("Volksbank", 1234567.89, dateFormat.parse("2024-12-31")));

        TransactionCategory category = new TransactionCategory(1,"Sonstiges");
        profile.addCategory(category);

        profile.addIncome(new Income(111.99, category,bankAccount,dateFormat.parse("2023-12-31"),"Test-Income1"));
        profile.addIncome(new Income(222.99, category,bankAccount,dateFormat.parse("2024-12-31"),"Test-Income2"));
        profile.addExpense(new Expense(-333.99, category,bankAccount,dateFormat.parse("2025-12-31"),"Test-Expense1"));

        profile.addGoal(new Goal("Test", "Mein Sparziel bis 2026", 99999.99, bankAccount, dateFormat.parse("2025-01-31"),dateFormat.parse("2025-12-31") ));

        dataController.saveProfile("jdbc:sqlite:db.sqlite", profile);
    }

    /**
     * Ensures that all FXML components are properly injected and available.
     * Called during the initialization phase of the controller.
     */
    @FXML
    void initialize() {
        assert btn_createprofile != null : "fx:id=\"btn_createprofile\" was not injected: check your FXML file 'view_start.fxml'.";
        assert btn_loadprofile != null : "fx:id=\"btn_loadprofile\" was not injected: check your FXML file 'view_start.fxml'.";
        assert btn_saveprofile != null : "fx:id=\"btn_saveprofile\" was not injected: check your FXML file 'view_start.fxml'.";
        assert vbox_background != null : "fx:id=\"vbox_background\" was not injected: check your FXML file 'view_start.fxml'.";
    }

    /* -------------------------------- */
    /* ------ Private Methods    ------ */
    /* -------------------------------- */

    /**
     * Loads the main controller and initializes it with the given profile.
     *
     * @param profile The profile object to pass to the main controller.
     * @throws IOException If there is an issue loading the FXML file.
     */
    private void loadMainController(Profile profile) throws IOException {
        logger.info("Main window is loading.");

        Stage primaryStage = (Stage) this.vbox_background.getScene().getWindow();

        // Load internationalized resources
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                "wbh/wbh_projekt_privatebuchhaltung/i18n/text_controls", Locale.ENGLISH
        );

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setResources(resourceBundle);

        // Load the main view FXML file
        fxmlLoader.setLocation(this.getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_main.fxml"));
        Parent fxmlScene = fxmlLoader.load();

        // Configure and set the scene
        Scene scene = new Scene(fxmlScene);
        scene.getStylesheets().add(
                Objects.requireNonNull(this.getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/styles/style_main.css"))
                        .toExternalForm()
        );

        primaryStage.setScene(scene);
        logger.info("Main window loaded successfully.");

        // Initialize the main controller
        Controller_main mainController = fxmlLoader.getController();
        mainController.setProfile(profile);

        // Pass WebAPI and HostServices to the main controller (for JPro)
        try {
            mainController.setHostServices(this.hostServices);
            mainController.setWebAPI(this.webAPI);
            logger.info("Web-API successfully initialized.");
        } catch (RuntimeException e) {
            logger.warn("NOTE: Application is running as a desktop application.", e);
        }
    }

    /* -------------------------------- */
    /* ------ Public Methods     ------ */
    /* -------------------------------- */

    /**
     * Sets the WebAPI instance for this controller.
     * Ensures the WebAPI is only initialized once.
     *
     * @param webAPI The WebAPI instance to set.
     */
    public void setWebAPI(final WebAPI webAPI) {
        if (this.webAPI == null) {
            this.webAPI = webAPI;
        }
    }

    /**
     * Sets the HostServices instance for this controller.
     * Ensures the HostServices is only initialized once.
     *
     * @param hostServices The HostServices instance to set.
     */
    public void setHostServices(final HostServices hostServices) {
        if (this.hostServices == null) {
            this.hostServices = hostServices;
        }
    }
}
