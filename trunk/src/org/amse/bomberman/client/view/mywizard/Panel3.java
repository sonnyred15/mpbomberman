package org.amse.bomberman.client.view.mywizard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Timer;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.net.impl.Connector;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Michael Korovkin
 */
public class Panel3 extends JPanel{
    private int serverNumber;
    private int playersNum;
    private JLabel[] players = new JLabel[Constants.MAX_PLAYERS];
    private JButton botJButton = new JButton();
    private JButton chatJButton = new JButton();
    private JTextArea chatTA = new JTextArea();
    private JTextField messageTF = new JTextField();
    //private Timer timer = new Timer();
    private final int width = 640;
    private final int height = 480;
    private final Dimension defaultButton = new Dimension(100,20);
    private final long checkStartDelay = 150;
    private final String emptyName = "EMPTY";
    private final String closedName = "Closed";

    public Panel3() {
        //serverNumber = myNumber;
        //playersNum = number;
        this.setSize(width, height);
        // add closed places or not???
        for (int i = 0; i < Constants.MAX_PLAYERS; i++) {
            players[i] = new JLabel();
            players[i].setHorizontalAlignment(SwingConstants.CENTER);
            if (i >= playersNum) {
                this.setPlayer(i, closedName);
                players[i].setVisible(false);
            }
        }
        /*messageTF.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER: {
                        try {
                            String message = getMessage();
                            if (message.length() > 0) {
                                List<String> list = Connector.getInstance()
                                        .sendChatMessage(message);
                                setNewMessages(list);
                            }
                        } catch (NetException ex) {
                            // is it good?
                            ex.printStackTrace();
                            //stopTimers();
                            //dispose();
                            //ServerInfoJFrame jframe = new ServerInfoJFrame();
                        }
                    }
                }
            }
            public void keyReleased(KeyEvent e) {
            }
        });
        /*try {
            List<String> gameInfo = Connector.getInstance().getMyGameInfo();
            botJButton.setPreferredSize(defaultButton);
            if (gameInfo.get(0).equals("false")) {
                botJButton.setEnabled(false);
                botJButton.setVisible(false);
            } else {
                //botJButton.setAction(new AddBotAction(this));
            }
            for (int i = 0; i < Integer.parseInt(gameInfo.get(1)); i++) {
                this.setPlayer(i, gameInfo.get(i+2));
            }
            for (int i = Integer.parseInt(gameInfo.get(1)); i < playersNum; i++) {
                this.setPlayer(i, emptyName);
            }
*/
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new GridLayout(Constants.MAX_PLAYERS, 1, 10, 10));
            leftPanel.setPreferredSize(new Dimension(100, height - 100));
            for (int i = 0; i < Constants.MAX_PLAYERS; i++) {
                leftPanel.add(players[i]);
            }

            JPanel rightPanel = new JPanel(new FlowLayout());
            rightPanel.setPreferredSize(new Dimension(250, height - 100));
            JPanel chatPanel = new JPanel(new GridLayout());
            chatPanel.setPreferredSize(new Dimension(240, height - 150));
            chatPanel.add(new JScrollPane(chatTA));
            chatTA.setEditable(false);
            chatTA.setLineWrap(true);
            messageTF.setPreferredSize(new Dimension(150, 25));
            chatJButton.setPreferredSize(new Dimension(85,25));
            //chatJButton.setAction(new ChatAction(this));
            rightPanel.add(chatPanel);
            rightPanel.add(messageTF);
            rightPanel.add(chatJButton);

            Box bottomBox = Box.createHorizontalBox();
            // how calculate sizes???
            bottomBox.setPreferredSize(new Dimension(width, 25));
            bottomBox.add(Box.createHorizontalGlue());
            bottomBox.add(botJButton);
            bottomBox.add(Box.createHorizontalGlue());

            this.setLayout(new FlowLayout());
            this.add(leftPanel);
            this.add(rightPanel);
            this.add(bottomBox);

            //this.startUpdating();
            //setResizable(false);
            setVisible(true);
        /*} catch (NetException ex) {
            JOptionPane.showMessageDialog(this, "Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            //StartJFrame jframe = new StartJFrame();
        }*/
    }
    private void setPlayer(int id, String name) {
        players[id].setText(name);
        LineBorder border = null;
        Color color = null;
        if (name.equals(emptyName)) {
            border = new LineBorder(Color.GRAY);
            color = Color.GRAY;
        } else {
            if (name.equals(closedName)) {
                border = new LineBorder(Color.LIGHT_GRAY);
                color = Color.LIGHT_GRAY;
            } else {
                border = new LineBorder(Color.BLACK);
                color = Color.BLACK;
            }
        }
        players[id].setBorder(border);
        players[id].setForeground(color);
    }
    private int getGameNumber() {
        return serverNumber;
    }
    private String getMessage() {
        String message = messageTF.getText();
        messageTF.setText("");
        return message;
    }
    private void setNewMessages(List<String> messages) {
        for (String message: messages) {
            chatTA.append(message + "\n");
        }
    }
    /*public static class AddBotAction extends AbstractAction {
        GameInfoJFrame parent;

        public AddBotAction(GameInfoJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Add Bot");
            putValue(SHORT_DESCRIPTION, "Add one bot to this game");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Connector.getInstance().joinBotIntoGame(parent.getGameNumber());
            } catch (NetException ex) {
                JOptionPane.showMessageDialog(parent, "Connection was lost.\n"
                        + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                parent.stopTimers();
                parent.dispose();
                StartJFrame jFrame = new StartJFrame();
            } /*catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Can not join bot to the game: \n"
                        + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public static class ChatAction extends AbstractAction{
        GameInfoJFrame parent;
        public ChatAction(GameInfoJFrame jframe){
            parent = jframe;
            putValue(NAME, "Send");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            try {
                String message = parent.getMessage();
                if (message.length() > 0) {
                    List<String> list = Connector.getInstance().sendChatMessage
                            (message);
                    parent.setNewMessages(list);
                }
            } catch (NetException ex) {
                JOptionPane.showMessageDialog(parent, "Connection was lost.\n"
                        + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                parent.stopTimers();
                parent.dispose();
                ServerInfoJFrame jframe = new ServerInfoJFrame();
            }

        }
    }
     */
}
