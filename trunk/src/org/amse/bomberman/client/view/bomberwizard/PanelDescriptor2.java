package org.amse.bomberman.client.view.bomberwizard;

import java.util.List;
import org.amse.bomberman.client.view.wizard.Wizard;
import org.amse.bomberman.client.view.wizard.PanelDescriptor;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.client.control.impl.ControllerImpl;
import org.amse.bomberman.client.net.NetException;
import javax.swing.JOptionPane;


/**
 *
 * @author Mikhail Korovkin
 */
public class PanelDescriptor2 extends PanelDescriptor{
    public PanelDescriptor2(Wizard wizard, String identifier) {
        super(wizard, identifier, new Panel2());
    }

    @Override
    public void doBeforeDisplay() {
        try {
            ControllerImpl.getInstance().requestGamesList();
            ControllerImpl.getInstance().requestMapsList();
        } catch (NetException ex) {
            ControllerImpl.getInstance().lostConnection(ex.getMessage());
            /*JOptionPane.showMessageDialog(this.getWizard(),
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.getWizard().setCurrentJPanel(BombWizard.IDENTIFIER1);*/
        }
    }

    @Override
    public boolean goNext() {
        Panel2 panel2 = (Panel2) this.getPanel();
        String state = panel2.getState();
        if (state.equals(Panel2.CREATE_NAME)) {
            Controller con = ControllerImpl.getInstance();
            try {
                String mapName = panel2.getMap();
                String gameName = panel2.getGameName();
                int maxPlayers = panel2.getMaxPlayers();
                con.requestCreateGame(gameName, mapName, maxPlayers);
                return true;
            } catch (NetException ex) {
                ControllerImpl.getInstance().lostConnection(ex.getMessage());
            }
        } else {
            List<String> selectedGame = panel2.getSelectedGame();
            int gameNumber = Integer.parseInt(selectedGame.get(0));
            if (gameNumber == -1) {
                JOptionPane.showMessageDialog(this.getWizard(), "You did't select the game! "
                        + " Do this and then click join.", "Not selected game", JOptionPane.WARNING_MESSAGE);
                return false;
            } else  if (selectedGame.get(selectedGame.size()-1).equals
                    (selectedGame.get(selectedGame.size()-2))) {
                JOptionPane.showMessageDialog(this.getWizard(), "Selected game is full!\n"
                        + "Please choose another one or create new.", "Game is full", JOptionPane.WARNING_MESSAGE);
                return false;
            } else {
                try {
                    ControllerImpl.getInstance().requestJoinGame(gameNumber);
                    return true;
                } catch (NetException ex) {
                    ControllerImpl.getInstance().lostConnection(ex.getMessage());
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean goBack() {
        ControllerImpl.getInstance().disconnect();
        return true;
    }
}
