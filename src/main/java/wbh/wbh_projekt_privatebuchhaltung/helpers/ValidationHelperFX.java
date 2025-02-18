package wbh.wbh_projekt_privatebuchhaltung.helpers;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import static wbh.wbh_projekt_privatebuchhaltung.enums.EnumGenerals.CSS_ERROR;

/**
 * Utility class for validating user input fields in JavaFX.
 * Ensures required fields are filled and values are valid.
 */
public class ValidationHelperFX {

    /**
     * Validates that the given controls are not empty.
     * Highlights invalid fields with an error style class.
     *
     * @param controls an array of JavaFX controls to validate
     * @return true if all controls are valid, false otherwise
     */
    public boolean validateMandatoryFieldsFX(Control... controls) {
        boolean isValid = true;

        for (Control control : controls) {
            if (control instanceof TextField) {
                isValid &= validateTextField((TextField) control);
            } else if (control instanceof DatePicker) {
                isValid &= validateDatePicker((DatePicker) control);
            } else if (control instanceof ComboBox<?>) {
                isValid &= validateComboBox((ComboBox<?>) control);
            }
        }
        return isValid;
    }

    /**
     * Checks if a TextField is not empty and applies error styling if necessary.
     */
    private boolean validateTextField(TextField textField) {
        boolean isValid = textField.getText() != null && !textField.getText().trim().isEmpty();
        updateControlStyle(textField, isValid);
        return isValid;
    }

    /**
     * Checks if a DatePicker has a selected date and applies error styling if necessary.
     */
    private boolean validateDatePicker(DatePicker datePicker) {
        boolean isValid = datePicker.getValue() != null;
        updateControlStyle(datePicker, isValid);
        return isValid;
    }

    /**
     * Checks if a ComboBox has a selected value and applies error styling if necessary.
     */
    private boolean validateComboBox(ComboBox<?> comboBox) {
        boolean isValid = comboBox.getValue() != null;
        updateControlStyle(comboBox, isValid);
        return isValid;
    }

    /**
     * Updates the style class of a control based on its validation state.
     */
    private void updateControlStyle(Control control, boolean isValid) {
        if (isValid) {
            control.getStyleClass().removeAll(CSS_ERROR);
        } else if (!control.getStyleClass().contains(CSS_ERROR)) {
            control.getStyleClass().add(CSS_ERROR);
        }

        javafx.application.Platform.runLater(() -> {
            control.applyCss();
            control.layout();
        });
    }

    /**
     * Validates if the given input is a valid monetary amount (numeric and non-zero).
     *
     * @param textField the string input to validate
     * @return true if the input is a valid amount, false otherwise
     */
    public boolean isValidAmount(TextField textField) {
        boolean isValid;

        try {
            isValid = Double.parseDouble(textField.getText()) != 0;
        } catch (NumberFormatException e) {
            isValid = false;
        }
        updateControlStyle(textField, isValid);
        return isValid;

    }
}
