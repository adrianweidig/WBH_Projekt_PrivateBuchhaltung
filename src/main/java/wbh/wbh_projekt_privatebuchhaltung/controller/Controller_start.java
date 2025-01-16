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

    @FXML
    void onaction_createprofile(ActionEvent event) {

    }

    @FXML
    void onaction_loadprofile(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert btn_createprofile != null : "fx:id=\"btn_createprofile\" was not injected: check your FXML file 'view_start.fxml'.";
        assert btn_loadprofile != null : "fx:id=\"btn_loadprofile\" was not injected: check your FXML file 'view_start.fxml'.";
        assert vbox_background != null : "fx:id=\"vbox_background\" was not injected: check your FXML file 'view_start.fxml'.";

    }

    public void setWebAPI(final WebAPI webAPI) {
        if (this.webAPI == null) {
            this.webAPI = webAPI;
        }
    }

    public void setHostServices(final HostServices hostServices) {
        if (this.hostServices == null) {
            this.hostServices = hostServices;
        }
    }

}
