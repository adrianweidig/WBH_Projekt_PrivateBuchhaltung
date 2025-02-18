package wbh.wbh_projekt_privatebuchhaltung.helpers;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import static wbh.wbh_projekt_privatebuchhaltung.enums.EnumGenerals.CSS_ERROR;

/**
 * Utility class for validating user input fields in JavaFX.
 * Ensures that required fields are filled and values meet specified criteria.
 */
public class ValidationHelperFX {

    /* -------------------------------- */
    /* ------ Public Methods     ------ */
    /* -------------------------------- */

    /**
     * Validates that the given controls are not empty.
     * Highlights invalid fields with an error style class.
     *
     * @param controls an array of JavaFX controls to validate
     * @return {@code true} if all controls are valid, {@code false} otherwise
     */
    public boolean validateMandatoryFieldsFX(final Control... controls) {
        boolean isValid = true;

        // Iterate through each control and validate based on its type
        for (Control control : controls) {
            if (control instanceof TextField) {
                isValid &= this.validateTextField((TextField) control);
            } else if (control instanceof DatePicker) {
                isValid &= this.validateDatePicker((DatePicker) control);
            } else if (control instanceof ComboBox<?>) {
                isValid &= this.validateComboBox((ComboBox<?>) control);
            }
        }
        return isValid;
    }

    /**
     * Validates if the given input is a valid monetary amount (numeric and non-zero).
     * Applies error styling if the input is invalid.
     *
     * @param textField the {@link TextField} input to validate
     * @return {@code true} if the input is a valid amount, {@code false} otherwise
     */
    public boolean isValidAmount(final TextField textField) {
        boolean isValid;

        try {
            isValid = Double.parseDouble(textField.getText()) != 0;
        } catch (NumberFormatException e) {
            isValid = false;
        }

        this.updateControlStyle(textField, isValid);
        return isValid;
    }

    /* -------------------------------- */
    /* ------ Private Methods    ------ */
    /* -------------------------------- */

    /**
     * Checks if a {@link TextField} is not empty and applies error styling if necessary.
     *
     * @param textField the {@link TextField} to validate
     * @return {@code true} if the field is not empty, {@code false} otherwise
     */
    private boolean validateTextField(final TextField textField) {
        boolean isValid = textField.getText() != null && !textField.getText().trim().isEmpty();
        this.updateControlStyle(textField, isValid);
        return isValid;
    }

    /**
     * Checks if a {@link DatePicker} has a selected date and applies error styling if necessary.
     *
     * @param datePicker the {@link DatePicker} to validate
     * @return {@code true} if a date is selected, {@code false} otherwise
     */
    private boolean validateDatePicker(final DatePicker datePicker) {
        boolean isValid = datePicker.getValue() != null;
        this.updateControlStyle(datePicker, isValid);
        return isValid;
    }

    /**
     * Checks if a {@link ComboBox} has a selected value and applies error styling if necessary.
     *
     * @param comboBox the {@link ComboBox} to validate
     * @return {@code true} if a value is selected, {@code false} otherwise
     */
    private boolean validateComboBox(final ComboBox<?> comboBox) {
        boolean isValid = comboBox.getValue() != null;
        this.updateControlStyle(comboBox, isValid);
        return isValid;
    }

    /**
     * Updates the style class of a control based on its validation state.
     * Ensures that valid controls do not have error styling while invalid ones are marked.
     *
     * @param control the {@link Control} to update
     * @param isValid {@code true} if the control is valid, {@code false} if invalid
     */
    private void updateControlStyle(final Control control, final boolean isValid) {
        if (isValid) {
            control.getStyleClass().removeAll(CSS_ERROR);
        } else if (!control.getStyleClass().contains(CSS_ERROR)) {
            control.getStyleClass().add(CSS_ERROR);
        }

        // Ensure the style update is applied correctly in the JavaFX application thread
        Platform.runLater(() -> {
            control.applyCss();
            control.layout();
        });
    }
}
