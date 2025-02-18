package wbh.wbh_projekt_privatebuchhaltung.models;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wbh.wbh_projekt_privatebuchhaltung.helpers.DialogButtonHelper;

import java.util.function.Consumer;

/**
 * A reusable delete confirmation dialog that integrates with DialogButtonHelper.
 */
public class DeleteDialogButton extends DialogButtonHelper {
    private static final Logger logger = LoggerFactory.getLogger(DeleteDialogButton.class);
    private static final int HBOX_SPACING = 10;

    /**
     * Constructs a DeleteDialogButton instance with a given root pane.
     *
     * @param rootPane the root pane where dialogs will be displayed
     */
    public DeleteDialogButton(StackPane rootPane) {
        super(rootPane);
    }

    /**
     * Displays a delete confirmation dialog for any given entity.
     *
     * @param entityName The name of the entity type (e.g., "bank account", "goal", "transaction").
     * @param onDelete   A callback function executed when the user confirms deletion.
     */
    public void show(String entityName, Consumer<Void> onDelete) {
        VBox dialogContent = new VBox(10);
        dialogContent.getStyleClass().add("dialog-box"); // Ensuring the right CSS class
        dialogContent.setAlignment(javafx.geometry.Pos.CENTER);

        String deleteText = "Bist du dir sicher, dass du " + entityName + " löschen möchtest?";
        Label confirmationLabel = new Label(deleteText);
        Button confirmButton = createActionButton("Löschen");
        Button cancelButton = createActionButton("Abbrechen");

        confirmButton.setOnAction(e -> {
            onDelete.accept(null);
            removeDialog(dialogContent);
            logger.debug("{} deleted.", entityName);
        });

        cancelButton.setOnAction(e -> removeDialog(dialogContent));

        dialogContent.getChildren().addAll(confirmationLabel, new HBox(HBOX_SPACING, confirmButton, cancelButton));
        showDialog(dialogContent);
    }
}
