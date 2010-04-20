package org.amse.bomberman.client.view.mywizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Creator;
/**
 *
 * @author Michael Korovkin
 */
public class WPanel3 extends JPanel{
    private final static long serialVersionUID = 1L;
    private final int         height = 480;

    //
    private int              maxPlayers = Constants.MAX_PLAYERS;
    private final int        width = 640;
    private final JTextField messageTF = new JTextField();

    //
    private final JTextArea chatTA = new JTextArea();
    private final JButton   chatJButton = new JButton("Send");
    private final JButton   botRemoveJButton = new JButton("Remove");

    //
    private JButton botAddJButton = new JButton("Add bot");

    //
    private JList    playersList;

    public WPanel3() {
        super();
        this.setSize(width, height);
        initComponents();
        setVisible(true);
    }

    public void setPlayersNum(int number) {    // TODO must be actually setMaxPlayers!
        this.maxPlayers = number;
    }

    public void setGameInfo(List<String> info) {
        if (info.get(0).equals("false")) {
            botAddJButton.setEnabled(false);
            botAddJButton.setVisible(false);
            botRemoveJButton.setEnabled(false);
            botRemoveJButton.setVisible(false);
            // TODO
            //wizard.setNextButtonEnable(false);
        } else {
            botAddJButton.setAction(new AddBotAction());
        }

        maxPlayers = Integer.parseInt(info.get(1));
        this.setPlayersNum(maxPlayers);

        DefaultListModel model = (DefaultListModel) this.playersList.getModel();

        model.clear();    // TODO Bad decision to always clear model

        for (int i = 0; i < Integer.parseInt(info.get(2)); i++) {    // setting names for existing players
            model.addElement(info.get(i + 3));
        }

        for (int i = Integer.parseInt(info.get(2)); i < maxPlayers; ++i) {
            model.addElement("");
        }
    }

    public void setNewMessages(List<String> messages) {
        if (!messages.get(0).equals("No new messages.")) {
            for (String message : messages) {
                chatTA.append(message + "\n");
            }
        }
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 0));

        /* creating players and botsControl panels */
        JPanel playersPanel = this.createPlayersPanel();
        JPanel botsPanel = this.createBotsPanel();
        Box    box = Box.createVerticalBox();

        box.add(playersPanel);
        box.add(botsPanel);
        box.add(Box.createHorizontalStrut(190));    // TODO bad decisont to use magic 190
        this.add(box, BorderLayout.WEST);

        /* creating and adding chat panel in the center */
        JPanel chatPanel = this.createChatPanel();

        this.add(chatPanel, BorderLayout.CENTER);
        this.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        messageTF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER : {
                        try {
                            String message = getMessage();

                            if (message.length() > 0) {
                                Controller.getInstance().requestSendChatMessage(message);
                            }
                        } catch (NetException ex) {

                            // is it good?
                            ex.printStackTrace();//TO DO
                            //wizard.setCurrentJPanel(0);
                        }
                    }
                }
            }
        });
        chatJButton.setAction(new ChatAction());
    }

    private JPanel createPlayersPanel() {
        DefaultListModel defaultListModel = new DefaultListModel();

        this.playersList = new JList(defaultListModel);
        this.playersList.setCellRenderer(new IconListRenderer());

        JPanel      playersPanel = new JPanel(new GridLayout());
        JScrollPane scroll = new JScrollPane(playersPanel);

        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setWheelScrollingEnabled(true);
        playersPanel.add(new JScrollPane(this.playersList));
        playersPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        return playersPanel;
    }

    private JPanel createChatPanel() {
        JPanel             chatPanel = new JPanel(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();

        this.chatTA.setEditable(false);
        this.chatTA.setLineWrap(true);

        // TEXT AREA
        cons.weightx = 1;
        cons.weighty = 1;
        cons.anchor = GridBagConstraints.FIRST_LINE_START;
        cons.gridwidth = GridBagConstraints.REMAINDER;    // icon before element that ends row
        cons.gridheight = 1;
        cons.fill = GridBagConstraints.BOTH;
        cons.insets = new Insets(0, 0, 5, 10);
        chatPanel.add(new JScrollPane(chatTA), cons);

        // TEXT FIELD
        cons = new GridBagConstraints();
        cons.ipady = 5;
        cons.weightx = 1;
        cons.weighty = 0;
        cons.gridwidth = GridBagConstraints.RELATIVE;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.insets = new Insets(0, 0, 5, 10);
        chatPanel.add(this.messageTF, cons);

        // SEND BUTTON
        cons = new GridBagConstraints();
        cons.weightx = 0;
        cons.weighty = 0;
        cons.gridwidth = GridBagConstraints.REMAINDER;
        cons.fill = GridBagConstraints.NONE;
        cons.insets = new Insets(0, 0, 5, 10);
        chatPanel.add(this.chatJButton, cons);

        return chatPanel;
    }

    private String getMessage() {
        String message = messageTF.getText();

        messageTF.setText("");

        return message;
    }

    private JPanel createBotsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        panel.add(botAddJButton);
        panel.add(botRemoveJButton);
        panel.setBorder(new EmptyBorder(0, 10, 0, 0));

        return panel;
    }

    private class AddBotAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public AddBotAction() {
            putValue(NAME, "Add Bot");
            putValue(SHORT_DESCRIPTION, "Add one bot to this game");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Controller.getInstance().requestJoinBotIntoGame();
            } catch (NetException ex) {
                // TO DO
                /*JOptionPane.showMessageDialog(wizard,
                        "Connection was lost.\n" + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                wizard.setCurrentJPanel(0);*/
            }
        }
    }


    private class ChatAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public ChatAction() {
            putValue(NAME, "Send");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                String message = getMessage();

                if (message.length() > 0) {
                    Controller.getInstance().requestSendChatMessage(message);
                }
            } catch (NetException ex) {
                // TO DO
                /*
                JOptionPane.showMessageDialog(wizard,
                        "Connection was lost.\n" + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                wizard.setCurrentJPanel(0); */
            }
        }
    }


    private class IconListRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;
        private final String      JOINED_ICON =
            "/org/amse/bomberman/client/icons/bomb-48.png";
        private final URL JOINED_ICON_URL =
            Panel1.class.getResource(JOINED_ICON);
        private Image joinedIcon;

        public IconListRenderer() {
            this.initJoinedIcon();
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {

            // Get the renderer component from parent class
            JLabel label =
                (JLabel) super.getListCellRendererComponent(list, value, index,
                                                            isSelected,
                                                            cellHasFocus);

            // Set icon
            label.setIcon(new ImageIcon(joinedIcon));

            // enabling or disabling label
            if (label.getText().equals("")) {
                label.setEnabled(false);
            } else {
                label.setEnabled(true);
            }

            return label;
        }

        private void initJoinedIcon() {    // TODO DO SUPER METHOD IN CREATOR CLASS FOR ALL SUCH METHODS!
            try {
                this.joinedIcon = ImageIO.read(JOINED_ICON_URL);    // returns BufferedImage

                // this.joinedIcon =
                // this.joinedIcon.getScaledInstance(this.iconDim, this.iconDim,
                // Image.SCALE_SMOOTH);
            } catch (IOException ex) {
                Creator.createErrorDialog(this, "Can`t load image for icon!",
                                          ex.getMessage());
                this.joinedIcon = null;
            }
        }
    }
}
