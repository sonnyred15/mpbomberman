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
    public static final String ACTION_DISCONNECT = "Connection lost";
    public static final String ACTION_JOIN = "Join selected game";
    public static final String ACTION_NEXT_TEXT = "Back text";
    public static final String ACTION_BACK_TEXT = "Next text";
    public BombWizard() {
        super(new Dimension(640, 480),"Let's BOMBERMANNING!!!");
        this.addPanelDescriptor(new PanelDescriptor1(this, IDENTIFIER1));
        this.addPanelDescriptor(new PanelDescriptor2(this, IDENTIFIER2));
        this.addPanelDescriptor(new PanelDescriptor3(this, IDENTIFIER3));
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
                if (current instanceof Panel3) {
                    if (!Model.getInstance().isStarted()) {
                        Model.getInstance().setStart(true);
                        this.startGame();
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
                if (!Model.getInstance().isStarted()) {
                    Model.getInstance().setStart(true);
                    this.startGame();
                }
            }
            return;
        }else if (command.equals(ProtocolConstants.CAPTION_GAME_STATUS_INFO)) {
            if (list.get(0).equals("started.")) {
                if (current instanceof Panel3) {
                    if (!Model.getInstance().isStarted()) {
                        Model.getInstance().setStart(true);
                        this.startGame();
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

    @Override
    public void finish() {
        try {
            Controller.getInstance().requestStartGame();
        } catch (NetException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(this, "Connection was lost.\n"
                + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.setCurrentJPanel(IDENTIFIER1);
        }
    }

    @Override
    public void wizardActionPerformed(WizardAction a) {
        String action = a.getMessage();
        if (action.equals(ACTION_BACK_TEXT)) {
            this.setBackText(a.getValue());
        } else if (action.equals(ACTION_NEXT_TEXT)) {
            this.setNextText(a.getValue());
        } else if (action.equals(ACTION_JOIN)) {
            this.goNext();
        } else if (action.equals(ACTION_DISCONNECT)) {
            JOptionPane.showMessageDialog(this, "Connection was lost.\n"
                + a.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.setCurrentJPanel(IDENTIFIER1);
        } else {
            System.out.println(action);
        }
    }
    private void startGame() {
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
