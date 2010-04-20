package org.amse.bomberman.client.view.mywizard;

import javax.swing.JOptionPane;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.net.NetException;

/**
 *
 * @author Michael Korovkin
 */
public class PanelDescriptor3 extends PanelDescriptor {
    public PanelDescriptor3(Wizard wizard, String identifier) {
        super(wizard, identifier, new WPanel3());
    }

    @Override
    public void doBeforeDisplay() {
        try {
            // TO DO
            //chatTA.setText("");
            Controller.getInstance().requestGameInfo();
        } catch (NetException ex) {
            JOptionPane.showMessageDialog(this.getWizard(), "Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            //go first panel
        }
    }

    @Override
    public void goBack() {
        try {
            Controller.getInstance().requestLeaveGame();
        } catch (NetException ex) {
             JOptionPane.showMessageDialog(this.getWizard(), "Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             // go first panel
        }
    }
}
