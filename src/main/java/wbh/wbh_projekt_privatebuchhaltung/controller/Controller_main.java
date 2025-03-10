package wbh.wbh_projekt_privatebuchhaltung.controller;

import com.jpro.webapi.WebAPI;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import one.jpro.platform.file.ExtensionFilter;
import one.jpro.platform.file.picker.FileSavePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.models.interfaces.ProfileAware;
import wbh.wbh_projekt_privatebuchhaltung.models.userProfile.Profile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

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
    private Button btnSave;

    @FXML
    private VBox vbox_background;

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private final Logger logger = LoggerFactory.getLogger(Controller_main.class);

    private Profile profile = null;

    private WebAPI webAPI = null;

    private HostServices hostServices = null;

    private File tempFile;

    private final Controller_data dataController = new Controller_data();

    /* -------------------------------- */
    /* ------ FXML Methods       ------ */
    /* -------------------------------- */

    /**
     * Initializes the main controller.
     * Sets up the file save picker for saving the profile.
     */
    @FXML
    void initialize() {
        // Create the FileSavePicker (uses the web variant internally if WebAPI is available)
        FileSavePicker fileSavePicker = FileSavePicker.create(btnSave);
        fileSavePicker.initialFileNameProperty().set("profile.sqlite");
        fileSavePicker.getExtensionFilters().clear();
        fileSavePicker.getExtensionFilters().add(ExtensionFilter.of("SQLite Database", ".sqlite"));

        // Set the action to be performed when a file is selected.
        fileSavePicker.setOnFileSelected(targetFile -> {
            try {
                // Get the path of the temporary file containing the profile.
                Path tempFilePath = tempFile.toPath();

                // Copy the temporary SQLite file to the user-selected target location.
                Files.copy(tempFilePath, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("Profile successfully saved: {}", tempFilePath);

                // Delete the temporary file after successful copy.
                if (Files.deleteIfExists(tempFilePath)) {
                    logger.info("Temporary SQLite file deleted: {}", tempFilePath);
                } else {
                    logger.warn("Temporary SQLite file not found for deletion: {}", tempFilePath);
                }
            } catch (IOException e) {
                logger.error("Error while saving the profile", e);
            }
            // Return a completed future as required by the API.
            return CompletableFuture.completedFuture(null);
        });
    }

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
     * Navigates to the Bankaccounts view.
     */
    @FXML
    private void handleBankaccounts() {
        this.logger.info("Navigating to Bankaccounts view.");
        this.loadContent("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_bankaccounts.fxml");
    }

    /* -------------------------------- */
    /* ------ Private Methods    ------ */
    /* -------------------------------- */

    /**
     * Loads an FXML file and displays its content in the contentPane.
     * Applies CSS, resource bundles for internationalization, and injects dependencies.
     *
     * @param fxmlFile the path to the FXML file.
     */
    private void loadContent(String fxmlFile) {
        try {
            this.logger.debug("Loading FXML file: {}", fxmlFile);
            // Clear the current content.
            this.contentPane.getChildren().clear();

            // Load the resource bundle for internationalization.
            ResourceBundle resourceBundle = ResourceBundle.getBundle(
                    "wbh/wbh_projekt_privatebuchhaltung/i18n/text_controls", Locale.GERMAN
            );

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setResources(resourceBundle);

            // Set the location of the FXML file.
            fxmlLoader.setLocation(this.getClass().getResource(fxmlFile));
            Parent fxmlRoot = fxmlLoader.load();

            // Create a new scene with the loaded FXML root.
            Scene scene = new Scene(fxmlRoot);
            // Apply the CSS stylesheet.
            scene.getStylesheets().add(
                    Objects.requireNonNull(this.getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/styles/style_contentpane.css"))
                            .toExternalForm()
            );

            // Retrieve the controller associated with the FXML.
            Object controller = fxmlLoader.getController();

            // Inject dependencies such as Profile, WebAPI, and HostServices.
            if (controller != null) {
                this.injectDependencies(controller);
            }

            // Add the loaded FXML content to the content pane.
            this.contentPane.getChildren().add(fxmlRoot);

            // Force layout update on the content pane.
            javafx.application.Platform.runLater(() -> {
                this.contentPane.applyCss();
                this.contentPane.layout();
            });

        } catch (IOException e) {
            this.logger.error("Failed to load FXML file: {}", fxmlFile, e);
        }
    }

    /**
     * Injects dependencies (Profile, WebAPI, HostServices) into the controller if it implements ProfileAware.
     *
     * @param controller the controller instance.
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
     * Invokes the setWebAPI method on the controller if such a method exists.
     *
     * @param controller the controller instance.
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
     * Invokes the setHostServices method on the controller if such a method exists.
     *
     * @param controller the controller instance.
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
     * Sets the Profile for the main controller.
     *
     * @param profile the Profile to set.
     */
    @Override
    public void setProfile(Profile profile) {
        this.profile = profile;
        this.logger.info("Profile set:\n{}", profile);
    }

    /**
     * Sets the WebAPI instance to be used by this controller.
     * Ensures that the WebAPI is set only once.
     *
     * @param webAPI the WebAPI instance.
     */
    public void setWebAPI(final WebAPI webAPI) {
        if (this.webAPI == null) {
            this.webAPI = webAPI;
        }
    }

    /**
     * Sets the HostServices instance to be used by this controller.
     * Ensures that the HostServices is set only once.
     *
     * @param hostServices the HostServices instance.
     */
    public void setHostServices(final HostServices hostServices) {
        if (this.hostServices == null) {
            this.hostServices = hostServices;
        }
    }

    /**
     * Handles the Save action by saving the current profile to a temporary SQLite file.
     *
     * @param actionEvent the ActionEvent that triggered the save.
     */
    @FXML
    public void handleSave(ActionEvent actionEvent) {
        // Save the profile to a temporary file in the system's temporary directory.
        this.tempFile = new File(System.getProperty("java.io.tmpdir"), "profile_temp.sqlite");
        dataController.saveProfile("jdbc:sqlite:" + tempFile.getAbsolutePath(), profile);
    }
}
