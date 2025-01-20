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
import wbh.wbh_projekt_privatebuchhaltung.models.Profile;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The type Controller start.
 */
public class Controller_start {

    private final Logger logger = LoggerFactory.getLogger(Controller_start.class);

    private WebAPI webAPI = null;
    private HostServices hostServices = null;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btn_createprofile;

    @FXML
    private Button btn_loadprofile;

    @FXML
    private VBox vbox_background;

    /**
     * This method is triggered when the "Create Profile" button is clicked.
     * The current implementation is empty, but it can be extended to handle
     * the profile creation logic.
     *
     * @param event The event triggered by the button click.
     */
    @FXML
    void onaction_createprofile(ActionEvent event) {
        // This is where you would load or create the profile logic.
        // For now, both methods do the same thing.
        Profile profile = new Profile();

        // Load the main controller and pass the profile object
        try {
            this.loadMainController(profile);
        } catch (IOException e) {
            logger.error("Failed to load the main controller with the provided profile.", e);
        }
    }

    /**
     * This method is triggered when the "Load Profile" button is clicked.
     * The current implementation is empty, but it can be extended to handle
     * the logic for loading a profile.
     *
     * @param event The event triggered by the button click.
     */
    @FXML
    void onaction_loadprofile(ActionEvent event) {
        // This is where you would load or create the profile logic.
        // For now, both methods do the same thing.
        Profile profile = new Profile();

        // Load the main controller and pass the profile object
        try {
            this.loadMainController(profile);
        } catch (IOException e) {
            logger.error("Failed to load the main controller with the provided profile.", e);
        }
    }

    /**
     * Loads the main controller and passes the profile object to it.
     *
     * @param profile The profile object to be passed to the main controller.
     * @throws IOException If there is an issue loading the FXML file.
     */
    private void loadMainController(Profile profile) throws IOException {
        logger.info("Main window is loading.");

        Stage primaryStage = (Stage) this.vbox_background.getScene().getWindow();

        // Load resources for internationalization (i18n)
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                "wbh/wbh_projekt_privatebuchhaltung/i18n/text_controls", Locale.ENGLISH
        );

        // Load the FXML file with the ResourceBundle
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setResources(resourceBundle);

        // Configure and load the main scene
        Parent fxmlScene = fxmlLoader.load(
                this.getClass().getResourceAsStream("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_main.fxml")
        );

        // Configure the scene
        Scene scene = new Scene(fxmlScene);
        scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/styles/style_main.css")).toExternalForm());

        // Set the scene and show the stage
        primaryStage.setScene(scene);

        logger.info("Main window loaded successfully.");

        // Get the controller and configure it
        Controller_main mainController = fxmlLoader.getController();

        mainController.setProfile(profile);

        // Use Web-API and HostServices for JPro
        try {
            mainController.setHostServices(this.hostServices);
            mainController.setWebAPI(this.webAPI);
            logger.info("Web-API successfully initialized.");
        } catch (RuntimeException e) {
            logger.warn("NOTE: Application is running as a desktop application.", e);
        }
    }

    /**
     * This method is called when the controller is initialized.
     * It ensures that all FXML components are properly injected and available.
     * If any component is not injected, an assertion error will be thrown.
     */
    @FXML
    void initialize() {
        assert btn_createprofile != null : "fx:id=\"btn_createprofile\" was not injected: check your FXML file 'view_start.fxml'.";
        assert btn_loadprofile != null : "fx:id=\"btn_loadprofile\" was not injected: check your FXML file 'view_start.fxml'.";
        assert vbox_background != null : "fx:id=\"vbox_background\" was not injected: check your FXML file 'view_start.fxml'.";
    }

    // Getter and Setter

    /**
     * Sets the WebAPI instance to be used by this controller.
     * This method ensures that the WebAPI is only set once.
     *
     * @param webAPI The WebAPI instance to be set.
     */
    public void setWebAPI(final WebAPI webAPI) {
        if (this.webAPI == null) {
            this.webAPI = webAPI;
        }
    }

    /**
     * Sets the HostServices instance to be used by this controller.
     * This method ensures that the HostServices is only set once.
     *
     * @param hostServices The HostServices instance to be set.
     */
    public void setHostServices(final HostServices hostServices) {
        if (this.hostServices == null) {
            this.hostServices = hostServices;
        }
    }
}
