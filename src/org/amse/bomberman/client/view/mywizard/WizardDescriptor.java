package org.amse.bomberman.client.view.mywizard;

import javax.swing.JPanel;

/**
 *
 * @author Michael Korovkin
 */
public abstract class WizardDescriptor {
    private Wizard parent;
    private JPanel targetPanel;
    private String panelIdentifier;

    public WizardDescriptor(Wizard wizard, String id, JPanel panel) {
        panelIdentifier = id;
        targetPanel = panel;
        parent = wizard;
    }

    public JPanel getPanel() {
        return targetPanel;
    }
    public void setPanel(JPanel panel) {
        targetPanel = panel;
    }

    public String getIdentifier() {
        return panelIdentifier;
    }
    public void setIdentifier(String id) {
        panelIdentifier = id;
    }

    public void setWizard(Wizard w) {
        parent = w;
    }
    public Wizard getWizard() {
        return parent;
    }

    /**
     * Override this method to provide functionality that will be performed just before
     * the panel is to be displayed.
     */
    public void doBeforeDisplay() {

    }

     /**
     * Override this method to perform functionality just before the panel is to be
     * hidden.
     */
    public void doAfterDisplay() {

    }

    public void goNext() {

    }

    public void goBack() {
        
    }
}
