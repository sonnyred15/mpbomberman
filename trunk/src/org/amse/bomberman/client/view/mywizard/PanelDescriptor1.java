package org.amse.bomberman.client.view.mywizard;

import java.io.IOException;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.NetException;

/**
 *
 * @author Michael Korovkin
 */
public class PanelDescriptor1 extends WizardDescriptor{
    public PanelDescriptor1(Wizard wizard, String identifier) {
        super(wizard, identifier, new WPanel1());
    }

    @Override
    public void goNext() {
        WPanel1 panel = (WPanel1)this.getPanel();
        try {
            Controller.getInstance().connect(panel.getIPAddress(), panel.getPort());
            Model.getInstance().setPlayerName(panel.getPlayerName());
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(this.getWizard(), "Can not connect to the server.\n"
                       + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this.getWizard(), "Can not connect to the server.\n"
                       + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }catch (NetException ex) {
            JOptionPane.showMessageDialog(this.getWizard(),"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
