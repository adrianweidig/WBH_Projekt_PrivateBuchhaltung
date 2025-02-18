package wbh.wbh_projekt_privatebuchhaltung.helpers;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import wbh.wbh_projekt_privatebuchhaltung.enums.EnumGenerals;

/**
 * A helper class for managing dialog buttons and UI components.
 * This class extends VBox and integrates with a given root pane.
 */
public class DialogButtonHelper extends VBox {
    private final StackPane rootPane;

    /**
     * Constructs a DialogButtonHelper instance with a given root pane.
     *
     * @param rootPane the root pane where dialogs will be displayed
     */
    public DialogButtonHelper(StackPane rootPane) {
        super(10);
        this.rootPane = rootPane;
        this.getStyleClass().add(EnumGenerals.CSS_DIALOG_BOX);
        setAlignment(Pos.CENTER);
    }

    /**
     * Displays the dialog by adding it to the root pane.
     *
     * @param dialog the VBox dialog to show
     */
    public void showDialog(VBox dialog) {
        rootPane.getChildren().add(dialog);
    }

    /**
     * Removes the dialog from the root pane.
     *
     * @param dialog the VBox dialog to remove
     */
    public void removeDialog(VBox dialog) {
        rootPane.getChildren().remove(dialog);
    }

    /**
     * Creates a labeled control container.
     *
     * @param labelText the label text
     * @param control   the control component
     * @return a VBox containing the label and control
     */
    public VBox createLabeledControl(String labelText, Control control) {
        Label label = new Label(labelText);
        return new VBox(5, label, control);
    }

    /**
     * Creates a styled action button.
     *
     * @param text the button text
     * @return a Button with applied styles
     */
    public Button createActionButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().clear();
        button.getStyleClass().add(EnumGenerals.CSS_DIALOG_ACTION_BUTTON);
        return button;
    }
}
