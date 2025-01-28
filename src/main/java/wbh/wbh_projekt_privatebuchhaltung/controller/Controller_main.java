package wbh.wbh_projekt_privatebuchhaltung.controller;

import com.jpro.webapi.WebAPI;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.models.Profile;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for the main application view.
 * Manages navigation between different views and handles global application settings.
 */
public class Controller_main {

    /* -------------------------------- */
    /* ------ FXML Variables     ------ */
    /* -------------------------------- */

    @FXML
    private StackPane contentPane;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnSettings;

    @FXML
    private VBox vbox_background;

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private final Logger logger = LoggerFactory.getLogger(Controller_main.class);

    private Profile profile = null;

    private WebAPI webAPI = null;

    private HostServices hostServices = null;

    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    /**
     * Handles the action of navigating to the Dashboard view.
     * Triggered when the corresponding button is clicked.
     */
    @FXML
    private void handleDashboard() {
        logger.info("Navigating to Dashboard view.");
        loadView("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_dashboard.fxml");
    }

    /**
     * Handles the action of navigating to the Settings view.
     * Triggered when the corresponding button is clicked.
     */
    @FXML
    private void handleSettings() {
        logger.info("Navigating to Settings view.");
        loadView("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_settings.fxml");
    }

    /* -------------------------------- */
    /* ------ Private Methods    ------ */
    /* -------------------------------- */

    /**
     * Loads an FXML file and displays it in the contentPane.
     *
     * @param fxmlFile Path to the FXML file to be loaded.
     */
    private void loadView(String fxmlFile) {
        try {
            logger.debug("Loading FXML file: {}", fxmlFile);
            contentPane.getChildren().clear();

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            contentPane.getChildren().add(loader.load());
        } catch (IOException e) {
            logger.error("Failed to load FXML file: {}", fxmlFile, e);
        }
    }

    /* -------------------------------- */
    /* ------ Public Methods     ------ */
    /* -------------------------------- */

    /**
     * Sets the profile object that is passed from the start screen.
     * This profile will be used throughout the main view.
     *
     * @param profile The Profile object to be set.
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
        logger.info("Profile set");
    }

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
