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
 * This class extends {@link VBox} and integrates with a given root pane.
 */
public class DialogButtonHelper extends VBox {

    /* -------------------------------- */
    /* ------ Instance Variables ------ */
    /* -------------------------------- */

    private final StackPane rootPane;

    /* -------------------------------- */
    /* ------ Constructor        ------ */
    /* -------------------------------- */

    /**
     * Constructs a {@code DialogButtonHelper} instance with a given root pane.
     *
     * @param rootPane the root pane where dialogs will be displayed
     */
    public DialogButtonHelper(final StackPane rootPane) {
        super(10);
        this.rootPane = rootPane;
        this.getStyleClass().add(EnumGenerals.CSS_DIALOG_BOX);
        this.setAlignment(Pos.CENTER);
    }

    /* -------------------------------- */
    /* ------ Public Methods     ------ */
    /* -------------------------------- */

    /**
     * Displays the given dialog by adding it to the root pane.
     *
     * @param dialog the {@link VBox} dialog to be displayed
     */
    public void showDialog(final VBox dialog) {
        this.rootPane.getChildren().add(dialog);
    }

    /**
     * Removes the given dialog from the root pane.
     *
     * @param dialog the {@link VBox} dialog to be removed
     */
    public void removeDialog(final VBox dialog) {
        this.rootPane.getChildren().remove(dialog);
    }

    /**
     * Creates a labeled control container consisting of a label and a UI control.
     *
     * @param labelText the text for the label
     * @param control   the {@link Control} component to be associated with the label
     * @return a {@link VBox} containing the label and control
     */
    public VBox createLabeledControl(final String labelText, final Control control) {
        Label label = new Label(labelText);
        return new VBox(5, label, control);
    }

    /**
     * Creates a styled action button with a predefined style class.
     *
     * @param text the button text
     * @return a styled {@link Button}
     */
    public Button createActionButton(final String text) {
        Button button = new Button(text);

        // Ensure that previous styles are cleared before applying a new style
        button.getStyleClass().clear();
        button.getStyleClass().add(EnumGenerals.CSS_DIALOG_ACTION_BUTTON);

        return button;
    }
}
