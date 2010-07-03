package org.amse.bomberman.client.view.wizard;

/**
 *
 * @author Mikhail Korovkin
 */
public class WizardEvent {
    String event;
    String message;

    public WizardEvent(String s) {
        event = s;
    }
    public WizardEvent(String event, String message) {
        this.message = message;
        this.event = event;
    }
    public String getMessage() {
        return message;
    }
    public String getValue() {
        return event;
    }
}
