package org.amse.bomberman.client.view.bomberwizard;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.client.control.impl.ControllerImpl;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Creator;

//~--- JDK imports ------------------------------------------------------------

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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.amse.bomberman.client.view.wizard.WizardController;
import org.amse.bomberman.client.view.wizard.WizardEvent;
import org.amse.bomberman.util.ImageUtilities;

/**
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class Panel3 extends JPanel {
    private final static long serialVersionUID = 1L;

    //
    private final int width = 640;
    private final int height = 480;

    //
    private Image image;
    private static final String BACKGROUND_RESOURCE_NAME = "/org/amse/bomberman/client" +
            "/view/resources/cover3.png";

    //
    private int              maxPlayers = Constants.MAX_PLAYERS;
    private final JTextField messageTF = new JTextField();
    private final JComponent playersPanel;

    //
    private final JTextArea  chatTA = new JTextArea();
    private final JButton    chatJButton = new JButton("Send");
    private final JComponent chatPanel;

    //
    private JButton          botAddJButton = new JButton("Add bot");
    private final JButton    botRemoveJButton = new JButton("Remove");
    private final JComponent botsPanel;

    //
    private JList playersList;

    public Panel3() {
        this.setSize(width, height);
        this.playersPanel = this.createPlayersComponent();
        this.botsPanel = this.createBotsComponent();
        this.chatPanel = this.createChatComponent();
        initComponents();
        initBackgroundImage();
        setVisible(true);
    }

    public void setMaxPlayers(int number) {
        this.maxPlayers = number;
    }

    public void setGameInfo(List<String> info) {
        if (info.get(0).equals("false")) {
            this.botsPanel.setVisible(false);

        } else {
            this.botsPanel.setVisible(true);
        }

        maxPlayers = Integer.parseInt(info.get(1));
        this.setMaxPlayers(maxPlayers);

        DefaultListModel model = (DefaultListModel) this.playersList.getModel();

        model.clear();

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

    public void cleanChatArea() {
        this.chatTA.setText("");
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
            Creator.createErrorDialog(this, "Can`t load background!", ex.getMessage());
            this.image = null;
        }
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 0));

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
        this.add(left, BorderLayout.WEST);

        chatPanel.setOpaque(false);
        /* adding chat panel in the center */
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
                                ControllerImpl.getInstance().requestSendChatMessage(message);
                            }
                        } catch (NetException ex) {

                            System.out.println(ex);
                            WizardController.throwWizardAction(new WizardEvent
                                (BomberWizard.EVENT_DISCONNECT, ex.getMessage()));
                        }
                    }
                }
            }
        });
    }

    private JComponent createPlayersComponent() {
        DefaultListModel defaultListModel = new DefaultListModel();

        this.playersList = new JList(defaultListModel);
        this.playersList.setOpaque(false);        
        this.playersList.setCellRenderer(new IconListRenderer());

        JPanel      playersJPanel = new JPanel(new GridLayout());
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
        panel.add(botAddJButton);
        this.botAddJButton.setAction(new AddBotAction());
        panel.add(botRemoveJButton);
        this.botRemoveJButton.setAction(new RemoveBotAction());

        return panel;
    }

    private JComponent createChatComponent() {
        JPanel chatJPanel = new JPanel(new GridBagLayout());

        //this.chatTA.setBorder(new TitledBorder("Chat"));
        this.chatTA.setEditable(false);
        this.chatTA.setLineWrap(true);
        this.chatTA.setForeground(Color.ORANGE);
        this.chatTA.setFont(new Font(Font.MONOSPACED, Font.BOLD + Font.ITALIC, 15));
        this.chatTA.setOpaque(false);

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
        //        scrollTA.getViewport().setBackground(new Color(1f,
//                1f, 1f, 0.2f));
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
        this.chatJButton.setAction(new ChatAction());
        chatJPanel.add(this.chatJButton, cons);
        
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

        public void actionPerformed(ActionEvent e) {
            try {
                ControllerImpl.getInstance().requestJoinBotIntoGame();
            } catch (NetException ex) {

                WizardController.throwWizardAction(new WizardEvent
                                (BomberWizard.EVENT_DISCONNECT, ex.getMessage()));
            }
        }
    }
    private class RemoveBotAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public RemoveBotAction() {
            putValue(NAME, "Remove");
            putValue(SHORT_DESCRIPTION, "Remove one bot from this game");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                ControllerImpl.getInstance().requestRemoveBotFromGame();
            } catch (NetException ex) {

                WizardController.throwWizardAction(new WizardEvent
                                (BomberWizard.EVENT_DISCONNECT, ex.getMessage()));
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
                    ControllerImpl.getInstance().requestSendChatMessage(message);
                }
            } catch (NetException ex) {

                WizardController.throwWizardAction(new WizardEvent
                                (BomberWizard.EVENT_DISCONNECT, ex.getMessage()));
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
//            label.setOpaque(false);
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
                Creator.createErrorDialog(this, "Can`t load image for icon!",
                                          ex.getMessage());
            }
        }
    }
}
