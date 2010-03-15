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
        this.addNextJPanel(new Panel1(), "CONNECT_PANEL");
        this.addNextJPanel(new Panel2(this), "CREATE_PANEL");
        this.addNextJPanel(new Panel3(), "GAME_PANEL");
        this.setCurrentJPanel(0);
        this.setNextAction(new NextAction(this));
        this.setBackAction(new BackAction(this));
        this.setVisible(true);
    }


    public static class NextAction extends AbstractAction {
        MyWizard parent;

        public NextAction(MyWizard jframe) {
            parent = jframe;
            putValue(NAME, "Next");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            JPanel current = parent.getCurrentJPanel();
            
            if (current instanceof Panel1) {
                Panel1 panel = (Panel1)current;
                IConnector con = Connector.getInstance();
                try {
                    con.сonnect(panel.getIPAddress(), panel.getPort());
                } catch (UnknownHostException ex) {
                    //ex.printStackTrace();
                    JOptionPane.showMessageDialog(parent, "Can not connect to the server.\n"
                            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    //ex.printStackTrace();
                    JOptionPane.showMessageDialog(parent, "Can not connect to the server.\n"
                            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            parent.goNext();
        }
    }
    public static class BackAction extends AbstractAction {
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
                } catch (NetException ex) {
                    //ex.printStackTrace();
                    JOptionPane.showMessageDialog(parent, "Connection was lost.\n"
                            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            parent.goBack();
        }
    }

}
