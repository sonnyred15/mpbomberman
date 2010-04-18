package org.amse.bomberman.client.view.mywizard;

import javax.swing.JOptionPane;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.net.NetException;

/**
 *
 * @author Michael Korovkin
 */
public class PanelDescriptor2 extends WizardDescriptor{
    private static final String IDENTIFIER = "Create/Join_Panel";
    public PanelDescriptor2() {
        super(IDENTIFIER, new WPanel2());
    }

    @Override
    public void doBeforeDisplay() {
        try {
            Controller.getInstance().requestGamesList();
            Controller.getInstance().requestMapsList();
        } catch (NetException ex) {
            JOptionPane.showMessageDialog(this.getWizard(),"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            //go first panel
        }
    }

    @Override
    public void goNext() {
        WPanel2 panel2 = (WPanel2) this.getPanel();
        int gameNumber = panel2.getSelectedGame();
        if (gameNumber != -1) {
            try {
                Controller.getInstance().requestJoinGame(gameNumber);
            } catch (NetException ex) {
                JOptionPane.showMessageDialog(this.getWizard(), "Connection was lost.\n"
                        + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                // go first panel
            }
        } else {
            JOptionPane.showMessageDialog(this.getWizard(), "You did't select the game! "
                    + " Do this and then click join.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void goBack() {
        Controller.getInstance().disconnect();
    }
}
