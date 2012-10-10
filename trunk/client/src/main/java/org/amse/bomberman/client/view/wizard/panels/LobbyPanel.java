package org.amse.bomberman.client.view.wizard.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.ImageUtilities;
import org.amse.bomberman.util.UIUtil;

/**
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class LobbyPanel extends JPanel {
    private final static long serialVersionUID = 1L;

    //
    private final int width  = 640;
    private final int height = 480;

    //
    private Image image;
    private static final String BACKGROUND_RESOURCE_NAME
            = "/org/amse/bomberman/client/view/resources/cover3.png";

    //
    private int              maxPlayers = Constants.MAX_PLAYERS;
    private final JTextField messageTF = new JTextField();
    private final JComponent playersPanel;

    //
    private final JTextArea  chatTA = new JTextArea();
    private final JButton    chatButton = new JButton("Send");
    private final JComponent chatPanel;

    //
    private JButton          botAddButton    = new JButton("Add bot");
    private final JButton    kickButton = new JButton("Kick");
    private final JComponent botsPanel;

    //
    private JList playersList;

    //
    private final Controller controller;

    public LobbyPanel(Controller controller) {        
        this.controller = controller;

        playersPanel = createPlayersComponent();
        botsPanel = createBotsComponent();
        chatPanel = createChatComponent();

        setSize(width, height);

        initComponents();
        initBackgroundImage();
    }

    private void setMaxPlayers(int number) {
        this.maxPlayers = number;
    }

    public void setGameInfo(final List<String> info) {
        if(!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    setGameInfo(info);
                }
            });
            return;//or will be done twice
        }

        if (info.get(0).equals("false")) {
            this.botsPanel.setVisible(false);
        } else {
            this.botsPanel.setVisible(true);
        }

        maxPlayers = Integer.parseInt(info.get(1));
        setMaxPlayers(maxPlayers);

        DefaultListModel model = (DefaultListModel) playersList.getModel();

        model.clear();

        for (int i = 0; i < Integer.parseInt(info.get(2)); i++) {    // setting names for existing players
            model.addElement(info.get(i + 3));
        }

        for (int i = Integer.parseInt(info.get(2)); i < maxPlayers; ++i) {
            model.addElement("");
        }
    }

    public void setNewMessages(final List<String> messages) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (!messages.get(0).equals("No new messages.")) {//TODO hardcoded String
                    for (String message : messages) {
                        chatTA.append(message + "\n");
                    }
                }
            }
        });
    }

    /**
     * Thread safe.
     */
    public void clearChatArea() {
        this.chatTA.setText("");
    }

    public void clearGameInfo() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                DefaultListModel model = (DefaultListModel) playersList.getModel();
                model.clear();//fires change
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(this.image != null){//actually image is BufferedImage so drawImage will return true.
            g.drawImage(this.image, 0, 0, null);
        }
    }

    private void initBackgroundImage() {
        try{
            this.image = ImageUtilities.initImage(BACKGROUND_RESOURCE_NAME,
                                                  this.getWidth(),
                                                  this.getHeight());
        }catch (IOException ex){
            UIUtil.createErrorDialog(this, "Can`t load background!", ex.getMessage());
            this.image = null;
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 0));

        /* creating players and botsControl panels */
        JPanel left = new JPanel(new GridBagLayout());
        left.setPreferredSize(new Dimension(200, 480));
        left.setBorder(new EmptyBorder(0, 5, 5, 0));
        left.setOpaque(false);

        //
        GridBagConstraints cons = new GridBagConstraints();

        cons.anchor = GridBagConstraints.NORTHWEST;
        cons.fill = GridBagConstraints.BOTH;
        cons.gridx = 0;
        cons.gridy = 0;
        cons.weightx = 1;
        cons.weighty = 1;
        cons.gridwidth = GridBagConstraints.REMAINDER;
        left.add(playersPanel, cons);

        //
        cons = new GridBagConstraints();
        cons.anchor = GridBagConstraints.PAGE_END;
        cons.gridx = 0;
        cons.gridy = 1;
        cons.weightx = 1;
        cons.gridwidth = 1;
        left.add(botsPanel, cons);

        //
        add(left, BorderLayout.WEST);

        chatPanel.setOpaque(false);
        /* adding chat panel in the center */
        add(chatPanel, BorderLayout.CENTER);
        add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        messageTF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER: {
                        String message = getMessage();

                        if (message.length() > 0) {
                            controller.requestSendChatMessage(message);
                        }
                    }
                }
            }
        });
    }

    private JComponent createPlayersComponent() {
        DefaultListModel defaultListModel = new DefaultListModel();

        playersList = new JList(defaultListModel);
        playersList.setOpaque(false);        
        playersList.setCellRenderer(new IconListRenderer());

        JPanel playersJPanel = new JPanel(new GridLayout());
        playersJPanel.setOpaque(false);
        JScrollPane scrollJList = new JScrollPane(playersList);

        scrollJList.setHorizontalScrollBarPolicy(ScrollPaneConstants
                .HORIZONTAL_SCROLLBAR_NEVER);
        scrollJList.setWheelScrollingEnabled(true);
        scrollJList.setOpaque(false);
        scrollJList.setBorder(null);
        scrollJList.getViewport().setOpaque(false);

        playersJPanel.add(scrollJList);

        return playersJPanel;
    }

    private JComponent createBotsComponent() {        
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);
        panel.add(botAddButton);
        botAddButton.setAction(new AddBotAction());
        panel.add(kickButton);
        kickButton.setAction(new KickAction());

        return panel;
    }

    private JComponent createChatComponent() {
        JPanel chatJPanel = new JPanel(new GridBagLayout());

        chatTA.setEditable(false);
        chatTA.setLineWrap(true);
        chatTA.setForeground(Color.ORANGE);
        chatTA.setFont(new Font(Font.MONOSPACED, Font.BOLD + Font.ITALIC, 15));
        chatTA.setOpaque(false);

        GridBagConstraints cons = new GridBagConstraints();

        // TEXT AREA
        cons.weightx = 1;
        cons.weighty = 1;
        cons.anchor = GridBagConstraints.FIRST_LINE_START;
        cons.gridwidth = GridBagConstraints.REMAINDER;    // icon before element that ends row
        cons.gridheight = 1;
        cons.fill = GridBagConstraints.BOTH;
        cons.insets = new Insets(0, 0, 5, 10);
        //
        JScrollPane scrollTA = new JScrollPane(chatTA);        
        scrollTA.setOpaque(false);
        scrollTA.getViewport().setOpaque(false);
        TitledBorder borderTA = new TitledBorder("Chat");
        borderTA.setTitleColor(Color.WHITE);
        borderTA.setTitleFont(new Font(null, Font.BOLD, 16));
        scrollTA.setViewportBorder(borderTA);
        scrollTA.setBorder(null);

        chatJPanel.add(scrollTA , cons);

        // TEXT FIELD
        cons = new GridBagConstraints();
        cons.ipady = 5;
        cons.weightx = 1;
        cons.weighty = 0;
        cons.gridwidth = GridBagConstraints.RELATIVE;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.insets = new Insets(0, 0, 5, 10);
        chatJPanel.add(this.messageTF, cons);

        // SEND BUTTON
        cons = new GridBagConstraints();
        cons.weightx = 0;
        cons.weighty = 0;
        cons.gridwidth = GridBagConstraints.REMAINDER;
        cons.fill = GridBagConstraints.NONE;
        cons.insets = new Insets(0, 0, 5, 10);
        chatButton.setAction(new ChatAction());
        chatJPanel.add(this.chatButton, cons);
        
        return chatJPanel;
    }

    private String getMessage() {
        String message = messageTF.getText();
        messageTF.setText("");
        return message;
    }

    private class AddBotAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public AddBotAction() {
            putValue(NAME, "Add Bot");
            putValue(SHORT_DESCRIPTION, "Add one bot to this game");
            putValue(SMALL_ICON, null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            controller.requestAddBot();
        }
    }

    private class KickAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public KickAction() {
            putValue(NAME, "Remove");
            putValue(SHORT_DESCRIPTION, "Remove one bot from this game");
            putValue(SMALL_ICON, null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO Client SERVER work on it.
//            controller.requestKickFromGame();
        }
    }

    private class ChatAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public ChatAction() {
            putValue(NAME, "Send");
            putValue(SMALL_ICON, null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String message = getMessage();

            if (message.length() > 0) {
                controller.requestSendChatMessage(message);
            }
        }
    }

    private class IconListRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        private final String      JOINED_ICON
                = "/org/amse/bomberman/client/icons/bomb-48.png";

        private final URL JOINED_ICON_URL =
            ConnectionPanel.class.getResource(JOINED_ICON);
        
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
            label.setBackground(new Color(1f, 1f,
                                          1f, 0f));
            label.setForeground(Color.ORANGE);


            // enabling or disabling label
            if (label.getText().equals("")) {
                label.setEnabled(false);
            } else {
                label.setEnabled(true);
            }

            return label;
        }

        private void initJoinedIcon() {
            try {
                this.joinedIcon = ImageIO.read(JOINED_ICON_URL);    // returns BufferedImage
            } catch (IOException ex) {
                UIUtil.createErrorDialog(this, "Can`t load image for icon!",
                                          ex.getMessage());
            }
        }
    }
}
