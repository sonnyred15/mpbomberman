package org.amse.bomberman.client.view.asynchronous;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import org.amse.bomberman.client.net.impl.Connector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Michail Korovkin
 */
public class GameInfoJFrame extends JFrame implements IView {

    private JLabel[] players = new JLabel[Constants.MAX_PLAYERS];
    private JButton startJButton = new JButton();
    private JButton botJButton = new JButton();
    private JButton cancelJButton = new JButton();
    private JButton chatJButton = new JButton();
    private JTextArea chatTA = new JTextArea();
    private JTextField messageTF = new JTextField();
    private final int width = 400;
    private final int height = 600;
    private final Dimension defaultButton = new Dimension(100, 20);
    private final String emptyName = "EMPTY";
    private final String closedName = "Closed";
    private final JPanel leftPanel;
    private final JPanel rightPanel;
    private final JPanel chatPanel;
    private final Box bottomBox;

    public GameInfoJFrame(Controller controller) {
        super("GameInfo");

        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(400, 150);
        setMinimumSize(new Dimension(width / 2, height / 2));
        // add closed places or not???
        for (int i = 0; i < Constants.MAX_PLAYERS; i++) {
            players[i] = new JLabel();
            players[i].setHorizontalAlignment(SwingConstants.CENTER);
            this.setPlayer(i, emptyName);
        }

        startJButton.setAction(new StartAction(this));
        startJButton.setPreferredSize(defaultButton);
        botJButton.setAction(new AddBotAction(this));
        botJButton.setPreferredSize(defaultButton);
        cancelJButton.setAction(new CancelAction(this));
        cancelJButton.setPreferredSize(defaultButton);
        messageTF.addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER: {
                        try {
                            String message = getMessage();
                            if (message.length() > 0) {
                                List<String> list = Connector.getInstance().sendChatMessage(message);
                                setNewMessages(list);
                            }
                        } catch (NetException ex) {
                            // is it good?
                            ex.printStackTrace();

                            dispose();
                        //ServerInfoJFrame jframe = new ServerInfoJFrame(); //TODO
                        }
                    }
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(Constants.MAX_PLAYERS, 1, 10, 10));
        leftPanel.setPreferredSize(new Dimension(100, height - 100));

        rightPanel = new JPanel(new FlowLayout());
        rightPanel.setPreferredSize(new Dimension(250, height - 100));

        chatPanel = new JPanel(new GridLayout());

        bottomBox = Box.createHorizontalBox();

        Container c = this.getContentPane();
        c.setLayout(new FlowLayout());
        c.add(leftPanel);
        c.add(rightPanel);
        c.add(bottomBox);

        setResizable(false);
        setVisible(false);
    }

    public void updateInfo(List<String> gameInfo) {

        if (gameInfo.get(0).equals("false")) {
            startJButton.setEnabled(false);
            botJButton.setEnabled(false);
        }

        for (int i = 0; i < Integer.parseInt(gameInfo.get(1)); i++) {
            this.setPlayer(i, gameInfo.get(i + 2));
        }

//            for (int i = Integer.parseInt(gameInfo.get(1)); i < playersNum; i++) {
//                this.setPlayer(i, emptyName);
//            }//TODO

        for (int i = 0; i < Constants.MAX_PLAYERS; i++) {
            leftPanel.removeAll();//PENDING
            leftPanel.add(players[i]);
        }

        chatPanel.removeAll();//PENDING
        chatPanel.setPreferredSize(new Dimension(240, height - 150));
        chatPanel.add(new JScrollPane(chatTA));

        chatTA.setText("");//PENDING
        chatTA.setEditable(false);
        chatTA.setLineWrap(true);

        messageTF.setPreferredSize(new Dimension(150, 25));

        chatJButton.setPreferredSize(new Dimension(85, 25));
        chatJButton.setAction(new ChatAction(this));

        rightPanel.removeAll();//PENDING
        rightPanel.add(chatPanel);
        rightPanel.add(messageTF);
        rightPanel.add(chatJButton);

        // how calculate sizes???
        bottomBox.removeAll();//PENDING
        bottomBox.setPreferredSize(new Dimension(width, 25));
        bottomBox.add(Box.createHorizontalGlue());
        bottomBox.add(botJButton);
        bottomBox.add(Box.createHorizontalGlue());
        bottomBox.add(startJButton);
        bottomBox.add(Box.createHorizontalGlue());
        bottomBox.add(cancelJButton);
        bottomBox.add(Box.createHorizontalGlue());
    }

    public void update(){
        //PENDING
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

    //TODO
//    private int getGameNumber() {
//        return serverNumber;
//    }
    private String getMessage() {
        String message = messageTF.getText();
        messageTF.setText("");
        return message;
    }

    private void setNewMessages(List<String> messages) {
        for (String message : messages) {
            chatTA.append(message + "\n");
        }
    }

    private void startGame() throws NetException {//TODO
//        this.dispose();
//        IModel model = Model.getInstance();
//        model.setMap(Connector.getInstance().getMap());
//        MapJFrame jframe = new MapJFrame();
//        Model.getInstance().addListener(jframe);
//        Connector.getInstance().beginUpdating();
    }

    public static class StartAction extends AbstractAction {

        GameInfoJFrame parent;

        public StartAction(GameInfoJFrame jframe) {
            parent = jframe;
            putValue(NAME, "Start");
            putValue(SHORT_DESCRIPTION, "Start this game.");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {//TODO
//            IConnector connect = Connector.getInstance();
//            try {
//                connect.startGame();
//                parent.startGame();
//            } catch (NetException ex) {
//                JOptionPane.showMessageDialog(parent, "Connection was lost.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                parent.dispose();
//                StartJFrame jFrame = new StartJFrame();
//            }
        }
    }

    public static class AddBotAction extends AbstractAction {

        GameInfoJFrame parent;

        public AddBotAction(GameInfoJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Add Bot");
            putValue(SHORT_DESCRIPTION, "Add one bot to this game");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {//TODO
//            try {
//                Connector.getInstance().joinBotIntoGame(parent.getGameNumber());
//            } catch (NetException ex) {
//                JOptionPane.showMessageDialog(parent, "Connection was lost.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                parent.dispose();
//                StartJFrame jFrame = new StartJFrame();
            } /*catch (IOException ex) {
        JOptionPane.showMessageDialog(parent, "Can not join bot to the game: \n"
        + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }*/

    }

    public static class CancelAction extends AbstractAction {

        GameInfoJFrame parent;

        public CancelAction(GameInfoJFrame jframe) {
            parent = jframe;
            putValue(NAME, "Cancel");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {//TODO
//            try {
//                Connector.getInstance().leaveGame();
//            } catch (NetException ex) {
//                JOptionPane.showMessageDialog(parent, "Connection was lost.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
//            parent.dispose();
//        //ServerInfoJFrame jframe = new ServerInfoJFrame(); //TODO
//        }
        }
    }

    public static class ChatAction extends AbstractAction {

        GameInfoJFrame parent;

        public ChatAction(GameInfoJFrame jframe) {
            parent = jframe;
            putValue(NAME, "Send");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {//TODO
//            try {
//                String message = parent.getMessage();
//                if (message.length() > 0) {
//                    List<String> list = Connector.getInstance().sendChatMessage(message);
//                    parent.setNewMessages(list);
//                }
//            } catch (NetException ex) {
//                JOptionPane.showMessageDialog(parent, "Connection was lost.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                parent.dispose();
//            //ServerInfoJFrame jframe = new ServerInfoJFrame(); //TODO
//            }
        }
    }
}
