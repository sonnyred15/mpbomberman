package org.amse.bomberman.client.view.bomberwizard;

import org.amse.bomberman.client.view.wizard.WizardEvent;
import org.amse.bomberman.client.view.wizard.Wizard;
import org.amse.bomberman.client.net.RequestResultListener;
import org.amse.bomberman.client.control.impl.ControllerImpl;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.protocol.ProtocolConstants;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.amse.bomberman.protocol.ProtocolMessage;

/**
 *
 * @author Mikhail Korovkin
 */
public class BomberWizard extends Wizard implements RequestResultListener {
    private static final long serialVersionUID = 1L;
    
    public static final String IDENTIFIER1 = "Server_Panel";
    public static final String IDENTIFIER2 = "Create/Join_Panel";
    public static final String IDENTIFIER3 = "GameInfo_Panel";
    public static final String EVENT_DISCONNECT = "Connection lost";
    public static final String EVENT_JOIN = "Join selected game";
    public static final String EVENT_NEXT_TEXT = "Back text";
    public static final String EVENT_BACK_TEXT = "Next text";

    public BomberWizard() {
        super(new Dimension(640, 480),"Let's BOMBERMANNING!!!");
        this.addPanelDescriptor(new PanelDescriptor1(this, IDENTIFIER1));
        this.addPanelDescriptor(new PanelDescriptor2(this, IDENTIFIER2));
        this.addPanelDescriptor(new PanelDescriptor3(this, IDENTIFIER3));
        this.setCurrentJPanel(IDENTIFIER1);

        ControllerImpl.getInstance().setReceiveInfoListener(this);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void received(ProtocolMessage<Integer, String> response) {
        int messageId = response.getMessageId();
        List<String> data = response.getData();

        JPanel current = this.getCurrentJPanel();
        if (messageId == ProtocolConstants.GAME_MAPS_LIST_MESSAGE_ID) {
            if (current instanceof Panel2) {
                Panel2 panel2 = (Panel2) current;
                panel2.setMaps(data);
            }
            return;
        } else if (messageId == ProtocolConstants.GAMES_LIST_MESSAGE_ID) {
            if (current instanceof Panel2) {
                Panel2 panel2 = (Panel2) current;
                panel2.setGames(data);
            }
            return;
        }else if (messageId == ProtocolConstants.GAME_INFO_MESSAGE_ID) {
            if (current instanceof Panel3) {
                if (!data.get(0).equals("Not joined to any game.")) {
                    Panel3 panel3 = (Panel3) current;
                    panel3.setGameInfo(data);
                } else {
                    this.goBack();
                    JOptionPane.showMessageDialog(this, "Created game was closed.\n"
                       , "Game Closed.", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            return;
        }else if (messageId == ProtocolConstants.CREATE_GAME_MESSAGE_ID) {
            if (!data.get(0).equals("Game created.")) {
                JOptionPane.showMessageDialog(this, "Can not create game.\n"
                       + data.get(0), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }else if (messageId == ProtocolConstants.JOIN_GAME_MESSAGE_ID) {
            if (!data.get(0).equals("Joined.")) {
                JOptionPane.showMessageDialog(this, "Can not join to the game.\n"
                       + data.get(0), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }else if (messageId == ProtocolConstants.START_GAME_MESSAGE_ID) {
            if (data.get(0).equals("Game started.")) {
                if (current instanceof Panel3) {
                    if (!Model.getInstance().isStarted()) {
                        Model.getInstance().setStart(true);
                        this.startGame();
                    }
                }
            } else {
                if (data.get(0).equals("Game is already started.")) {
                } else {
                    JOptionPane.showMessageDialog(this, "Can not start game.\n"
                            + data.get(0), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        } else if (messageId == ProtocolConstants.NOTIFICATION_MESSAGE_ID) {
            if(data.contains(ProtocolConstants.MESSAGE_GAME_START)) {
            if (current instanceof Panel3) {
                if (!Model.getInstance().isStarted()) {
                    Model.getInstance().setStart(true);
                    this.startGame();
                }
            }
            }
            return;
        } else if (messageId == ProtocolConstants.GAME_STATUS_MESSAGE_ID) {
            if (data.get(0).equals("started.")) {
                if (current instanceof Panel3) {
                    if (!Model.getInstance().isStarted()) {
                        Model.getInstance().setStart(true);
                        this.startGame();
                    }
                }
            }
            return;
        } else if (messageId == ProtocolConstants.LEAVE_MESSAGE_ID) {
            if (!data.get(0).equals("Disconnected.")) {
                JOptionPane.showMessageDialog(this, "Can not leave game.\n"
                       + data.get(0), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }else if (messageId == ProtocolConstants.BOT_ADD_MESSAGE_ID) {
            if (current instanceof Panel3) {
                if (!data.get(0).equals("Bot added.")) {
                    JOptionPane.showMessageDialog(this, "Can not join bot.\n"
                            + data.get(0), "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
            return;
        }else if (messageId == ProtocolConstants.BOT_REMOVE_MESSAGE_ID) {
            if (current instanceof Panel3) {
                if (!data.get(0).equals("Bot removed.")) {
                    JOptionPane.showMessageDialog(this, "Can not remove bot.\n"
                            + data.get(0), "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
            return;
        }else if (messageId == ProtocolConstants.CHAT_GET_MESSAGE_ID) {
            if (current instanceof Panel3) {
                Panel3 panel3 = (Panel3) current;
                panel3.setNewMessages(data);
            }
            return;
        }else if (messageId == ProtocolConstants.CHAT_ADD_RESULT_MESSAGE_ID) {
            if (current instanceof Panel3) {
                Panel3 panel3 = (Panel3) current;
                panel3.setNewMessages(data);
            }
            return;
        }
    }

    @Override
    public void finish() {
        try {
            ControllerImpl.getInstance().requestStartGame();
        } catch (NetException ex) {
            ControllerImpl.getInstance().lostConnection(ex.getMessage());
            /*System.out.println(ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.setCurrentJPanel(IDENTIFIER1);*/
        }
    }

    @Override
    public void wizardActionPerformed(WizardEvent e) {
        String event = e.getValue();
        if (event.equals(EVENT_BACK_TEXT)) {
            this.setBackText(e.getValue());
        } else if (event.equals(EVENT_NEXT_TEXT)) {
            this.setNextText(e.getValue());
        } else if (event.equals(EVENT_JOIN)) {
            this.goNext();
        } else if (event.equals(EVENT_DISCONNECT)) {
            ControllerImpl.getInstance().lostConnection(e.getMessage());
            /*JOptionPane.showMessageDialog(this, a.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.setCurrentJPanel(IDENTIFIER1);*/
        } else {
            System.out.println(event);
        }
    }
    private void startGame() {
        ControllerImpl.getInstance().startGame();
    }
}
