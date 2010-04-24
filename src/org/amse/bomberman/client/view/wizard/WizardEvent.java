package org.amse.bomberman.client.view.wizard;

/**
 *
 * @author Michael Korovkin
 */
public class WizardEvent {
    String message;
    String value;

    public WizardEvent(String s) {
        message = s;
    }
    public WizardEvent(String message, String value) {
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
