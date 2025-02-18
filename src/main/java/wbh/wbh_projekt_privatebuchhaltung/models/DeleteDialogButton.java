package wbh.wbh_projekt_privatebuchhaltung.models;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.helpers.DialogButtonHelper;

import java.util.function.Consumer;

/**
 * A reusable delete confirmation dialog that integrates with {@link DialogButtonHelper}.
 * Displays a confirmation message and executes a callback if the user confirms deletion.
 */
public class DeleteDialogButton extends DialogButtonHelper {

    /* -------------------------------- */
    /* ------ Class Constants    ------ */
    /* -------------------------------- */

    private static final Logger logger = LoggerFactory.getLogger(DeleteDialogButton.class);
    private static final int HBOX_SPACING = 10;

    /* -------------------------------- */
    /* ------ Constructor         ------ */
    /* -------------------------------- */

    /**
     * Constructs a DeleteDialogButton instance with a given root pane.
     *
     * @param rootPane the root pane where dialogs will be displayed
     */
    public DeleteDialogButton(StackPane rootPane) {
        super(rootPane);
    }

    /* -------------------------------- */
    /* ------ Dialog Handling    ------ */
    /* -------------------------------- */

    /**
     * Displays a delete confirmation dialog for a given entity.
     * The user can either confirm or cancel the deletion.
     *
     * @param entityName The name of the entity type (e.g., "bank account", "goal", "transaction").
     * @param onDelete   A callback function executed when the user confirms deletion.
     */
    public void show(String entityName, Consumer<Void> onDelete) {
        // Create dialog container
        VBox dialogContent = new VBox(10);
        dialogContent.getStyleClass().add("dialog-box"); // Ensuring the correct CSS class
        dialogContent.setAlignment(Pos.CENTER);

        // Create confirmation message
        String deleteText = "Bist du dir sicher, dass du " + entityName + " löschen möchtest?";
        Label confirmationLabel = new Label(deleteText);

        // Create action buttons
        Button confirmButton = this.createActionButton("Löschen");
        Button cancelButton = this.createActionButton("Abbrechen");

        // Define button actions
        confirmButton.setOnAction(e -> {
            onDelete.accept(null);
            this.removeDialog(dialogContent);
            logger.debug("{} deleted.", entityName);
        });

        cancelButton.setOnAction(e -> this.removeDialog(dialogContent));

        // Assemble dialog
        HBox buttonContainer = new HBox(HBOX_SPACING, confirmButton, cancelButton);
        dialogContent.getChildren().addAll(confirmationLabel, buttonContainer);

        // Show the dialog
        this.showDialog(dialogContent);
    }
}
