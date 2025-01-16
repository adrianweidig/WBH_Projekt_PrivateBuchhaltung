package wbh.wbh_projekt_privatebuchhaltung;

import com.jpro.webapi.JProApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import wbh.wbh_projekt_privatebuchhaltung.controller.Controller_start;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main extends JProApplication {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set the title and an icon for the application
        primaryStage.setTitle("Haushaltsbuch");  // Title is kept as "Haushaltsbuch"
        primaryStage.getIcons().add(new Image("/wbh/wbh_projekt_privatebuchhaltung/images/32x32/icon_buchhaltung_32x32.png"));

        // Load resources for internationalization (i18n)
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                "wbh/wbh_projekt_privatebuchhaltung/i18n/text_controls", Locale.getDefault()
        );

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

        // Get the controller and configure it
        Controller_start startController = fxmlLoader.getController();

        // Use Web-API and HostServices for JPro
        try {
            startController.setWebAPI(this.getWebAPI());
        } catch (RuntimeException e) {
            System.err.println("HINT: Application running as desktop application");
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
