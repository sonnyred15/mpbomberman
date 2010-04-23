package org.amse.bomberman.client.view.mywizard;

/**
 *
 * @author Michael Korovkin
 */
public class WizardAction {
    String message;
    String value;

    public WizardAction(String s) {
        message = s;
    }
    public WizardAction(String message, String value) {
        this.message = message;
        this.value = value;
    }
    public String getMessage() {
        return message;
    }
    public String getValue() {
        return value;
    }
}
