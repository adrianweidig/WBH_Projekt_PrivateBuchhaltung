module wbh.wbh_projekt_privatebuchhaltung {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires jpro.webapi;
    requires org.slf4j;

    opens wbh.wbh_projekt_privatebuchhaltung to javafx.fxml;
    exports wbh.wbh_projekt_privatebuchhaltung;
}