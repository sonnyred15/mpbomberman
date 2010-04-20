package org.amse.bomberman.client.view.mywizard;

import java.awt.Dimension;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.view.gamejframe.GameJFrame;
import org.amse.bomberman.util.ProtocolConstants;

/**
 *
 * @author Michael Korovkin
 */
public class BombWizard extends Wizard implements RequestResultListener {
    public static final String IDENTIFIER1 = "Server_Panel";
    public static final String IDENTIFIER2 = "Create/Join_Panel";
    public static final String IDENTIFIER3 = "GameInfo_Panel";
    public BombWizard() {
        super(new Dimension(660, 530),"Let's BOMBERMANNING!!!");
        this.addWizardDescriptor(new PanelDescriptor1(this, IDENTIFIER1));
        this.addWizardDescriptor(new PanelDescriptor2(this, IDENTIFIER2));
        this.addWizardDescriptor(new PanelDescriptor3(this, IDENTIFIER3));
        this.setCurrentJPanel(IDENTIFIER1);

        Controller.getInstance().setReceiveInfoListener(this);
        this.setVisible(true);
    }

    public void received(List<String> list) {
       if (list.size() == 0) {
            return;
        }
        String command = list.get(0);
        list.remove(0);
        JPanel current = this.getCurrentJPanel();
        if (command.equals(ProtocolConstants.CAPTION_GAME_MAPS_LIST)) {
            if (current instanceof WPanel2) {
                WPanel2 panel2 = (WPanel2) current;
                panel2.setMaps(list);
            }
            return;
        } else if (command.equals(ProtocolConstants.CAPTION_GAMES_LIST)) {
            if (current instanceof WPanel2) {
                WPanel2 panel2 = (WPanel2) current;
                panel2.setGames(list);
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_GAME_INFO)) {
            if (current instanceof WPanel3) {
                if (!list.get(0).equals("Not joined to any game.")) {
                    WPanel3 panel3 = (WPanel3) current;
                    panel3.setGameInfo(list);
                } else {
                    this.goBack();
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
                if (current instanceof WPanel3) {
                    if (!Model.getInstance().isStarted()) {
                        Model.getInstance().setStart(true);
                        this.finish();
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
            if (current instanceof WPanel3) {
                if (!Model.getInstance().isStarted()) {
                    Model.getInstance().setStart(true);
                    this.finish();
                }
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_GAME_STATUS_INFO)) {
            if (list.get(0).equals("started.")) {
                if (current instanceof WPanel3) {
                    if (!Model.getInstance().isStarted()) {
                        Model.getInstance().setStart(true);
                        this.finish();
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
            if (current instanceof WPanel3) {
                if (!list.get(0).equals("Bot added.")) {
                    JOptionPane.showMessageDialog(this, "Can not join bot.\n"
                            + list.get(0), "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_GET_CHAT_MSGS)) {
            if (current instanceof WPanel3) {
                WPanel3 panel3 = (WPanel3) current;
                panel3.setNewMessages(list);
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_SEND_CHAT_MSG_INFO)) {
            if (current instanceof WPanel3) {
                WPanel3 panel3 = (WPanel3) current;
                panel3.setNewMessages(list);
            }
            return;
        }
    }
    @Override
    public void finish() {
        this.dispose();
        Controller.getInstance().setReceiveInfoListener((RequestResultListener)
                Model.getInstance());
        GameJFrame jframe = new GameJFrame();
        Model.getInstance().addListener(jframe);
        try {
            Controller.getInstance().requestGameMap();
        } catch (NetException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(this, "Connection was lost.\n"
                + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.setCurrentJPanel(IDENTIFIER1);
            Controller.getInstance().setReceiveInfoListener(this);
        }
    }
}
