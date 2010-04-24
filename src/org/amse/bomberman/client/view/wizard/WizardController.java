package org.amse.bomberman.client.view.wizard;

import org.amse.bomberman.client.view.wizard.WizardEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Korovkin
 */
public class WizardController {
    private static List<WizardListener> listeners;

    public WizardController() {
        listeners = new ArrayList<WizardListener>();
    }
    public void addWizardListener(WizardListener w) {
        listeners.add(w);
    }
    public void removeWizardListener(WizardListener w) {
        listeners.remove(w);
    }
    public static void throwWizardAction(WizardEvent a) {
        for (WizardListener listener: listeners) {
            listener.wizardActionPerformed(a);
        }
    }
}
