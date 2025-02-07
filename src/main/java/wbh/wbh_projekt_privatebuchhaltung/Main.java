package wbh.wbh_projekt_privatebuchhaltung;

import com.jpro.webapi.JProApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.controller.Controller_start;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Application Starting Point for the Private Buchhaltung Project.
 * Initializes the JavaFX application, loads the start view, and sets up the main stage.
 */
public class Main extends JProApplication {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private final Logger logger = LoggerFactory.getLogger(Main.class);

    /* -------------------------------- */
    /* ------ Application Start ------ */
    /* -------------------------------- */

    /**
     * The entry point for the JavaFX application.
     * Initializes the primary stage, sets up the title, loads the FXML view, and manages the window.
     *
     * @param primaryStage The primary stage for the JavaFX application.
     * @throws Exception If an error occurs during the loading of the FXML or other resources.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Application is starting.");

        // Some OS have problems with temp folders not existing in dev environments so we set them up
        Path path = Paths.get("/tmp/lang");
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Verzeichnis wurde erstellt: " + path);
            } else {
                logger.info("Verzeichnis existiert bereits: " + path);
            }
        } catch (IOException e) {
            logger.error("Fehler beim Erstellen des Verzeichnisses: " + e.getMessage(), e);
        }



        // Load internationalization resources (i18n)
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                "wbh/wbh_projekt_privatebuchhaltung/i18n/text_controls", Locale.GERMAN
        );

        // Set application title from the resource bundle
        String appTitle = resourceBundle.getString("app.title");
        primaryStage.setTitle(appTitle);

        // Set the application icon
        primaryStage.getIcons().add(new Image("/wbh/wbh_projekt_privatebuchhaltung/images/32x32/icon_buchhaltung_32x32.png"));

        // Load the FXML file with the ResourceBundle
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setResources(resourceBundle);
        fxmlLoader.setLocation(this.getClass().getResource("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_start.fxml"));
        Parent fxmlScene = fxmlLoader.load();


        // Configure the scene and apply the corresponding styles
        Scene scene = new Scene(fxmlScene);
        scene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("styles/style_start.css")).toExternalForm());

        // Set the scene and show the stage
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);  // Allow window resizing
        primaryStage.show();

        logger.info("Start window loaded successfully.");

        // Initialize the controller and set additional dependencies
        Controller_start startController = fxmlLoader.getController();
        try {
            startController.setHostServices(this.getHostServices());
            startController.setWebAPI(this.getWebAPI());
            logger.info("Web-API successfully initialized.");
        } catch (RuntimeException e) {
            logger.warn("NOTE: Application is running as a desktop application.", e);
        }
    }

    /* -------------------------------- */
    /* ------ Main Method ----------- */
    /* -------------------------------- */

    /**
     * Main entry point for running the application.
     * Launches the JPro application, logging the start and shutdown events.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("Main method is being executed.");
        launch(args);
        logger.info("Application has been closed.");
    }

    /* -------------------------------- */
    /* ------ Application Stop ------ */
    /* -------------------------------- */

    /**
     * Called when the application is stopped.
     * Currently logs the stop event and calls the superclass's stop method to handle the default shutdown behavior.
     *
     * @throws Exception If an error occurs while stopping the application.
     */
    @Override
    public void stop() throws Exception {
        logger.info("Application is stopping.");
        super.stop();  // Call the superclass stop method to handle default shutdown behavior.
    }
}
