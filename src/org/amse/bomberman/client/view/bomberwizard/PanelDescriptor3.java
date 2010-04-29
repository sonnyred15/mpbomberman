package org.amse.bomberman.client.view.bomberwizard;

import org.amse.bomberman.client.view.wizard.Wizard;
import org.amse.bomberman.client.view.wizard.PanelDescriptor;
import javax.swing.JOptionPane;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.net.NetException;

/**
 *
 * @author Michael Korovkin
 */
public class PanelDescriptor3 extends PanelDescriptor {
    public PanelDescriptor3(Wizard wizard, String identifier) {
        super(wizard, identifier, new Panel3());
    }

    @Override
    public void doBeforeDisplay() {
        try {
            Panel3 panel3 = (Panel3) this.getPanel();
            panel3.clean();
            Controller.getInstance().requestGameInfo();
        } catch (NetException ex) {
            JOptionPane.showMessageDialog(this.getWizard(),
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.getWizard().setCurrentJPanel(BombWizard.IDENTIFIER1);
        }
    }

    @Override
    public boolean goBack() {
        try {
            Controller.getInstance().requestLeaveGame();
            return true;
        } catch (NetException ex) {
             JOptionPane.showMessageDialog(this.getWizard(),
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             this.getWizard().setCurrentJPanel(BombWizard.IDENTIFIER1);
             return false;
        }
    }
}
