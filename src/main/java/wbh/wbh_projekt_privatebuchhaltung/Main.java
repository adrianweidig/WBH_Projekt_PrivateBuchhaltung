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

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Application Starting Point
 */
public class Main extends JProApplication {

    // Logger defined as an instance variable
    private final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * This method is the entry point for the JavaFX application.
     * It initializes the main application window, sets the title,
     * loads the FXML view, and manages the primary stage.
     *
     * @param primaryStage The primary stage for the JavaFX application.
     * @throws Exception If an error occurs during the loading of the FXML or other resources.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Application is starting.");

        // Load resources for internationalization (i18n)
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                "wbh/wbh_projekt_privatebuchhaltung/i18n/text_controls", Locale.ENGLISH
        );

        // Set the title from the resource bundle
        String appTitle = resourceBundle.getString("app.title");
        primaryStage.setTitle(appTitle);

        // Set the application icon
        primaryStage.getIcons().add(new Image("/wbh/wbh_projekt_privatebuchhaltung/images/32x32/icon_buchhaltung_32x32.png"));

        // Load the FXML file with the ResourceBundle
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setResources(resourceBundle);

        // Configure and load the main scene
        Parent fxmlScene = fxmlLoader.load(
                this.getClass().getResourceAsStream("/wbh/wbh_projekt_privatebuchhaltung/fxml/view_start.fxml")
        );

        // Configure the scene
        Scene scene = new Scene(fxmlScene);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/style_start.css")).toExternalForm());

        // Set the scene and show the stage
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        logger.info("Start window loaded successfully.");

        // Get the controller and configure it
        Controller_start startController = fxmlLoader.getController();

        // Use Web-API and HostServices for JPro
        try {
            startController.setHostServices(this.getHostServices());
            startController.setWebAPI(this.getWebAPI());
            logger.info("Web-API successfully initialized.");
        } catch (RuntimeException e) {
            logger.warn("NOTE: Application is running as a desktop application.", e);
        }
    }

    /**
     * This is the main entry point for running the application.
     * It launches the JPro application and logs the starting and closing events.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("Main method is being executed.");
        launch(args);
        logger.info("Application has been closed.");
    }

    /**
     * This method is called when the application is stopped.
     * It has been overridden to allow additional functionality to be added in the future
     * when the application is being shut down. Currently, it logs the stopping event
     * and calls the superclass's stop method to handle the default shutdown behavior.
     *
     * @throws Exception If an error occurs while stopping the application.
     */
    @Override
    public void stop() throws Exception {
        logger.info("Application is stopping.");
        super.stop();  // Call the superclass stop method to handle default shutdown behavior.
    }

}
