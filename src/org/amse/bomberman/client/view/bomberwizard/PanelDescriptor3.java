package org.amse.bomberman.client.view.bomberwizard;

import org.amse.bomberman.client.view.wizard.Wizard;
import org.amse.bomberman.client.view.wizard.PanelDescriptor;
import org.amse.bomberman.client.control.impl.ControllerImpl;
import org.amse.bomberman.client.net.NetException;

/**
 *
 * @author Mikhail Korovkin
 */
public class PanelDescriptor3 extends PanelDescriptor {
    public PanelDescriptor3(Wizard wizard, String identifier) {
        super(wizard, identifier, new Panel3());
    }

    @Override
    public void doBeforeDisplay() {
        try {
            Panel3 panel3 = (Panel3) this.getPanel();
            panel3.cleanChatArea();
            ControllerImpl.getInstance().requestGameInfo();
        } catch (NetException ex) {
            ControllerImpl.getInstance().lostConnection(ex.getMessage());
        }
    }

    @Override
    public boolean goBack() {
        try {
            ControllerImpl.getInstance().requestLeaveGame();
            return true;
        } catch (NetException ex) {
            ControllerImpl.getInstance().lostConnection(ex.getMessage());
            return false;
        }
    }
}
