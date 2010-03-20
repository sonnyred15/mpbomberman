package org.amse.bomberman.client.view.mywizard;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.net.impl.Connector;

/**
 *
 * @author Michael Korovkin
 */
public class MainWizard extends MyWizard{
    public MainWizard() {
        super(new Dimension(700, 520), "Let's BOMBERMANNING!!!");
        this.addNextJPanel(new Panel1(this), "CONNECT_PANEL");
        this.addNextJPanel(new Panel2(this), "CREATE_PANEL");
        this.addNextJPanel(new Panel3(this), "GAME_PANEL");
        this.setNextAction(new NextAction(this));
        this.setBackAction(new BackAction(this));
        this.setCurrentJPanel(0);
        this.setVisible(true);
    }
    public void slideNext() {
        this.goNext();
        Updating nextPanel = (Updating) this.getCurrentJPanel();
        nextPanel.getServerInfo();
    }
    public void slideBack() {
        this.goBack();
        updateCurrentPanel();
    }
    public void updateCurrentPanel() {
        Updating nextPanel = (Updating) this.getCurrentJPanel();
        nextPanel.getServerInfo();
    }

   private class NextAction extends AbstractAction {
       MyWizard parent;
       public NextAction(MyWizard jframe) {
           putValue(NAME, "Next");
           putValue(SMALL_ICON, null);
           parent = jframe;
       }
       public void actionPerformed(ActionEvent e) {
           IConnector con = Connector.getInstance();
           try {
               JPanel current = parent.getCurrentJPanel();
               if (current instanceof Panel1) {
                   Panel1 panel1 = (Panel1) current;
                   con.сonnect(panel1.getIPAddress(), panel1.getPort());
                   slideNext();
                   parent.setBackButtonEnable(false);
               }
               if (current instanceof Panel2) {
                   Panel2 panel2 = (Panel2) current;
                   int gameNumber = panel2.getSelectedGame();
                   if (gameNumber != -1) {
                       con.joinGame(gameNumber);
                       int maxPl = panel2.getSelectedMaxPl();
                       slideNext();
                       Panel3 panel3 = (Panel3) parent.getCurrentJPanel();
                       panel3.setPlayersNum(maxPl);
                   } else {
                       JOptionPane.showMessageDialog(parent, "You did't select the game! "
                        + " Do this and then click join.", "Error", JOptionPane.ERROR_MESSAGE);
                   }
               }
               if (current instanceof Panel3) {
                   Panel3 panel3 = (Panel3) current;
                   panel3.startGame();
               }
           } catch (UnknownHostException ex) {
               JOptionPane.showMessageDialog(parent, "Can not connect to the server.\n"
                       + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
           } catch (IOException ex) {
               JOptionPane.showMessageDialog(parent, "Can not connect to the server.\n"
                       + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
           } catch (NetException ex) {
               JOptionPane.showMessageDialog(parent,"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
           }
       }
    }
    private class BackAction extends AbstractAction {
        MyWizard parent;

        public BackAction(MyWizard jframe) {
            parent = jframe;
            putValue(NAME, "Back");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            JPanel current = parent.getCurrentJPanel();

            if (current instanceof Panel3) {
                Panel3 panel = (Panel3)current;
                IConnector con = Connector.getInstance();
                try {
                    con.leaveGame();
                    panel.stopTimers();
                    slideBack();
                    parent.setBackButtonEnable(false);
                } catch (NetException ex) {
                    JOptionPane.showMessageDialog(parent, "Connection was lost.\n"
                            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (current instanceof Panel2) {
            }
        }
    }
}
