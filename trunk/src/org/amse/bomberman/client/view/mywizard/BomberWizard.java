package org.amse.bomberman.client.view.mywizard;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.IController;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.util.ProtocolConstants;

/**
 *
 * @author Michael Korovkin
 */
public class BomberWizard extends MyWizard implements RequestResultListener {
    //private final int height = 530;
    //private final int width = 680;

    public BomberWizard() {
        super(new Dimension(660, 530), "Let's BOMBERMANNING!!!");
        this.addNextJPanel(new Panel1(this), "CONNECT_PANEL");
        this.addNextJPanel(new Panel2(this), "CREATE_PANEL");
        this.addNextJPanel(new Panel3(this), "GAME_PANEL");
        this.setNextAction(new NextAction(this));
        this.setBackAction(new BackAction(this));
        this.setCancelAction(new CancelAction(this));
        this.setCurrentJPanel(0);

        Controller.getInstance().setReceiveInfoListener(this);
        this.setVisible(true);
    }
    public void slideNext() {
        this.goNext();
        updateCurrentPanel();
    }
    public void slideBack() {
        this.goBack();
        updateCurrentPanel();
    }
    public void updateCurrentPanel() {
        Updating current = (Updating) this.getCurrentJPanel();
        current.doBeforeShow();
    }
    public void received(List<String> list) {
        if (list.size() == 0) {
            return;
        }
        String command = list.get(0);
        list.remove(0);
        JPanel current = this.getCurrentJPanel();
        if (command.equals(ProtocolConstants.CAPTION_GAME_MAPS_LIST)) {
            if (current instanceof Panel2) {
                Panel2 panel2 = (Panel2) current;
                panel2.setMaps(list);
            }
            return;
        } else if (command.equals(ProtocolConstants.CAPTION_GAMES_LIST)) {
            if (current instanceof Panel2) {
                Panel2 panel2 = (Panel2) current;
                panel2.setGames(list);
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_GAME_INFO)) {
            if (current instanceof Panel3) {
                if (!list.get(0).equals("Not joined to any game.")) {
                    Panel3 panel3 = (Panel3) current;
                    panel3.setGameInfo(list);
                } else {
                    this.slideBack();
                    JOptionPane.showMessageDialog(this, "Created game was closed.\n"
                       , "Game Closed.", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_CREATE_GAME)) {
            if (!list.get(0).equals("Game created.")) {
                JOptionPane.showMessageDialog(this, "Can not create game.\n"
                       + list.get(0), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_JOIN_GAME)) {
            if (!list.get(0).equals("Joined.")) {
                JOptionPane.showMessageDialog(this, "Can not join to the game.\n"
                       + list.get(0), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_START_GAME_INFO)) {
            if (list.get(0).equals("Game started.")) {
                if (current instanceof Panel3) {
                    Panel3 panel3 = (Panel3) current;
                    try {
                        if (!Model.getInstance().isStarted()) {
                            Model.getInstance().setStart(true);
                            panel3.startGame();
                        }
                    } catch (NetException ex) {
                        JOptionPane.showMessageDialog(this, "Connection was lost.\n"
                                + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        this.setCurrentJPanel(0);
                    }
                }
            } else {
                if (list.get(0).equals("Game is already started.")) {
                } else {
                    JOptionPane.showMessageDialog(this, "Can not start game.\n"
                            + list.get(0), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        } else if (command.equals(ProtocolConstants.MESSAGE_GAME_START)) {
            if (current instanceof Panel3) {
                Panel3 panel3 = (Panel3) current;
                try {
                    if (!Model.getInstance().isStarted()) {
                        Model.getInstance().setStart(true);
                        panel3.startGame();
                    }
                } catch (NetException ex) {
                    JOptionPane.showMessageDialog(this, "Connection was lost.\n"
                            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    this.setCurrentJPanel(0);
                }
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_GAME_STATUS_INFO)) {
            if (list.get(0).equals("started.")) {
                if (current instanceof Panel3) {
                    Panel3 panel3 = (Panel3) current;
                    try {
                        if (!Model.getInstance().isStarted()) {
                            Model.getInstance().setStart(true);
                            panel3.startGame();
                        }
                    } catch (NetException ex) {
                        JOptionPane.showMessageDialog(this, "Connection was lost.\n"
                                + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        this.setCurrentJPanel(0);
                    }
                }
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_LEAVE_GAME_INFO)) {
            if (!list.get(0).equals("Disconnected.")) {
                JOptionPane.showMessageDialog(this, "Can not leave game.\n"
                       + list.get(0), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_JOIN_BOT_INFO)) {
            if (current instanceof Panel3) {
                if (!list.get(0).equals("Bot added.")) {
                    JOptionPane.showMessageDialog(this, "Can not join bot.\n"
                            + list.get(0), "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_GET_CHAT_MSGS)) {
            if (current instanceof Panel3) {
                Panel3 panel3 = (Panel3) current;
                panel3.setNewMessages(list);
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_SEND_CHAT_MSG_INFO)) {
            if (current instanceof Panel3) {
                Panel3 panel3 = (Panel3) current;
                panel3.setNewMessages(list);
            }
            return;
        }
    }

   private class NextAction extends AbstractAction {
       MyWizard parent;
       public NextAction(MyWizard jframe) {
           putValue(NAME, "Next");
           putValue(SMALL_ICON, null);
           parent = jframe;
       }
       public void actionPerformed(ActionEvent e) {
           IController con = Controller.getInstance();
           try {
               JPanel current = parent.getCurrentJPanel();
               if (current instanceof Panel1) {
                   Panel1 panel1 = (Panel1) current;
                   con.connect(panel1.getIPAddress(), panel1.getPort());
                   Model.getInstance().setPlayerName(panel1.getPlayerName());
                   slideNext();
               }
               if (current instanceof Panel2) {
                   Panel2 panel2 = (Panel2) current;
                   int gameNumber = panel2.getSelectedGame();
                   if (gameNumber != -1) {
                       con.requestJoinGame(gameNumber);
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
                   Controller.getInstance().requestStartGame();
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
            IController con = Controller.getInstance();

            if (current instanceof Panel3) {
                Panel3 panel = (Panel3)current;
                try {
                    con.requestLeaveGame();
                    slideBack();
                } catch (NetException ex) {
                    JOptionPane.showMessageDialog(parent, "Connection was lost.\n"
                            + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (current instanceof Panel2) {
                con.disconnect();
                slideBack();
            }
        }
    }
    public static class CancelAction extends AbstractAction {
        MyWizard parent;

        public CancelAction(MyWizard jframe) {
            parent = jframe;
            putValue(NAME, "Cancel");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            parent.dispose();
            Controller.getInstance().disconnect();
        }
    }
}
