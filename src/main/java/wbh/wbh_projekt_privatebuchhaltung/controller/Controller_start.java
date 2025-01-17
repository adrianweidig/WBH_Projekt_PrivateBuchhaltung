package wbh.wbh_projekt_privatebuchhaltung.controller;

import com.jpro.webapi.WebAPI;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller_start {

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
        // TODO: Implement the logic for creating a profile.
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
        // TODO: Implement the logic for loading a profile.
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
