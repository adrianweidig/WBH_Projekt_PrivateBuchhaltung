package wbh.wbh_projekt_privatebuchhaltung.controller;

import com.jpro.webapi.WebAPI;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.models.interfaces.ProfileAware;
import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.Profile;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for the main application view.
 * Manages navigation between different views and handles global application settings.
 */
public class Controller_main implements ProfileAware {

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
    private Button btnAccountOverview;

    @FXML
    private Button btnGoals;

    @FXML
    private Button btnNetwork;

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
     * Navigates to the Dashboard view.
     */
    @FXML
    public void handleDashboard() {
        this.logger.info("Navigating to Dashboard view.");
        this.loadContent("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_dashboard.fxml");
    }

    /**
     * Navigates to the Settings view.
     */
    @FXML
    private void handleSettings() {
        this.logger.info("Navigating to Settings view.");
        this.loadContent("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_settings.fxml");
    }

    /**
     * Navigates to the Account Overview view.
     */
    @FXML
    private void handleAccountOverview() {
        this.logger.info("Navigating to Account Overview view.");
        this.loadContent("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_accountoverview.fxml");
    }

    /**
     * Navigates to the Goals view.
     */
    @FXML
    private void handleGoals() {
        this.logger.info("Navigating to Goals view.");
        this.loadContent("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_goals.fxml");
    }

    /**
     * Navigates to the Network view.
     */
    @FXML
    private void handleNetwork() {
        this.logger.info("Navigating to Network view.");
        this.loadContent("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_network.fxml");
    }

    /* -------------------------------- */
    /* ------ Private Methods    ------ */
    /* -------------------------------- */

    /**
     * Loads an FXML file and displays it in the contentPane.
     * Applies the appropriate CSS, internationalization,
     * and invokes methods like setProfile, setWebAPI, and setHostServices.
     *
     * @param fxmlFile Path to the FXML file to be loaded.
     */
    private void loadContent(String fxmlFile) {
        try {
            this.logger.debug("Loading FXML file: {}", fxmlFile);
            this.contentPane.getChildren().clear();

            ResourceBundle resourceBundle = ResourceBundle.getBundle(
                    "wbh/wbh_projekt_privatebuchhaltung/i18n/text_controls", Locale.ENGLISH
            );

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(resourceBundle);

            fxmlLoader.setLocation(this.getClass().getResource(fxmlFile));
            Parent fxmlRoot = fxmlLoader.load();

            Scene scene = new Scene(fxmlRoot);
            scene.getStylesheets().add(
                    Objects.requireNonNull(this.getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/styles/style_contentpane.css"))
                            .toExternalForm()
            );

            // Retrieve the controller of the loaded FXML
            Object controller = fxmlLoader.getController();

            if (controller != null) {
                this.injectDependencies(controller);
            }

            this.contentPane.getChildren().add(fxmlRoot);

            // Ensures that the GUI gets reloaded after the FX-Thread
            javafx.application.Platform.runLater(() -> {
                this.contentPane.applyCss();
                this.contentPane.layout();
            });

        } catch (IOException e) {
            this.logger.error("Failed to load FXML file: {}", fxmlFile, e);
        }
    }

    /**
     * Injects dependencies (Profile, WebAPI, HostServices) into the controller if applicable.
     *
     * @param controller The controller instance to receive dependencies.
     */
    private void injectDependencies(Object controller) {
        if (controller instanceof ProfileAware profileAwareController) {
            profileAwareController.setProfile(this.profile);
            this.logger.debug("Profile set for controller: {}", controller.getClass().getName());
        }
        this.invokeSetWebAPI(controller);
        this.invokeSetHostServices(controller);
    }

    /**
     * Invokes the setWebAPI method if it exists in the controller.
     */
    private void invokeSetWebAPI(Object controller) {
        try {
            var setWebAPIMethod = controller.getClass().getMethod("setWebAPI", WebAPI.class);
            setWebAPIMethod.invoke(controller, this.webAPI);
            this.logger.debug("setWebAPI successfully invoked for controller: {}", controller.getClass().getName());
        } catch (NoSuchMethodException e) {
            this.logger.debug("No 'setWebAPI' method found in controller: {}", controller.getClass().getName());
        } catch (Exception e) {
            this.logger.error("Error invoking 'setWebAPI' in controller: {}", controller.getClass().getName(), e);
        }
    }

    /**
     * Invokes the setHostServices method if it exists in the controller.
     */
    private void invokeSetHostServices(Object controller) {
        try {
            var setHostServicesMethod = controller.getClass().getMethod("setHostServices", HostServices.class);
            setHostServicesMethod.invoke(controller, this.hostServices);
            this.logger.debug("setHostServices successfully invoked for controller: {}", controller.getClass().getName());
        } catch (NoSuchMethodException e) {
            this.logger.debug("No 'setHostServices' method found in controller: {}", controller.getClass().getName());
        } catch (Exception e) {
            this.logger.error("Error invoking 'setHostServices' in controller: {}", controller.getClass().getName(), e);
        }
    }

    /* -------------------------------- */
    /* ------ Public Methods     ------ */
    /* -------------------------------- */

    /**
     * Sets the profile object to be used throughout the main view.
     *
     * @param profile The Profile object to be set.
     */
    @Override
    public void setProfile(Profile profile) {
        this.profile = profile;
        this.logger.info("Profile set:\n{}", profile);
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
