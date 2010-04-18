/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.client.view.mywizard;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.view.gamejframe.GameJFrame;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Creator;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Panel3 extends JPanel implements Updating {

    private final static long serialVersionUID = 1L;
    //
    private static final String JOINED_ICON = "/org/amse/bomberman/client/icons/bomb-48.png";
    private static final URL JOINED_ICON_URL = Panel1.class.getResource(
            JOINED_ICON);
    private Image joinedIcon;
    private int iconDim = 40;
    //
    private int playersNum = 1;
    private MyWizard wizard;
    //
    private JLabel[] playersNames = new JLabel[Constants.MAX_PLAYERS];
    private JLabel[] bombs = new JLabel[Constants.MAX_PLAYERS];
    //
    private JButton botAddJButton = new JButton("Add bot");
    private JButton botRemoveJButton = new JButton("Remove");
    //
    private JTextArea chatTA = new JTextArea();
    private JTextField messageTF = new JTextField();
    private JButton chatJButton = new JButton("Send");
    //
    private final int width = 640;
    private final int height = 480;

    public Panel3(MyWizard jframe) {
        super();
        this.wizard = jframe;
        this.setSize(width, height);
        this.initJoinedIcon();
        this.initPlayersAndIconsLabels();
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());

        /*creating players and botsControl panels*/
        JPanel playersPanel = this.createPlayersPanel();
        JPanel botsPanel = this.createBotsPanel();
//        JScrollPane playersScroll = new JScrollPane(playersPanel);
//        playersScroll.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
//        playersScroll.setHorizontalScrollBarPolicy(
//                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        Box box = Box.createVerticalBox();
//        box.add(playersScroll);
        box.add(playersPanel);
        box.add(botsPanel);
        this.add(box, BorderLayout.WEST);

        /*creating and adding chat panel in the center*/
        JPanel chatPanel = this.createChatPanel();
        this.add(chatPanel, BorderLayout.CENTER);

        this.add(Box.createVerticalStrut(10), BorderLayout.NORTH);

        messageTF.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER: {
                        try {
                            String message = getMessage();
                            if (message.length() > 0) {
                                Controller.getInstance().requestSendChatMessage(
                                        message);
                            }
                        } catch (NetException ex) {
                            // is it good?
                            ex.printStackTrace();
                            wizard.setCurrentJPanel(0);
                        }
                    }
                }
            }
        });

        chatJButton.setAction(new ChatAction());
    }

    private void initJoinedIcon() {//TODO DO SUPER METHOD IN CREATOR CLASS FOR ALL SUCH METHODS!
        try {
            this.joinedIcon = ImageIO.read(JOINED_ICON_URL);//returns BufferedImage
            this.joinedIcon =
            this.joinedIcon.getScaledInstance(this.iconDim, this.iconDim,
                    Image.SCALE_SMOOTH);
        } catch (IOException ex) {
            Creator.createErrorDialog(this, "Can`t load background!", ex.
                    getMessage());
            this.joinedIcon = null;
        }
    }

    private JPanel createPlayersPanel() {
        /*Initializing labels and icons for players panel*/
        this.initPlayersAndIconsLabels();

        /*Initializing GridBagLayout*/
        GridBagLayout layout = new GridBagLayout();
        JPanel playersPanel = new JPanel(layout);

        /*Layouting*/
        GridBagConstraints cons;
        int i = 0;
        for (; i < this.bombs.length; ++i) {
            /*reseting constraints*/
            cons = new GridBagConstraints();
            cons.gridy = i;
            cons.anchor = GridBagConstraints.NORTHWEST;

            /*icons*/
            cons.gridx = 0;
            cons.fill = GridBagConstraints.NONE;
            cons.ipady = 3;
            playersPanel.add(bombs[i], cons);

            /*players labels*/
            cons.gridx = 1;
            cons.fill = GridBagConstraints.NONE;
            cons.ipady = this.iconDim / 2 + 5;
            playersPanel.add(playersNames[i], cons);
        }
        cons = new GridBagConstraints();
        cons.weightx = 2;
        cons.weighty = 1;
        cons.gridx = 0;
        cons.gridy = i;
        cons.gridheight = GridBagConstraints.REMAINDER;
        cons.gridwidth = GridBagConstraints.REMAINDER;
        cons.fill = GridBagConstraints.BOTH;
        playersPanel.add(Box.createGlue(), cons);

        //playersPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        return playersPanel;
    }

    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();

        this.chatTA.setEditable(false);
        this.chatTA.setLineWrap(true);

        //TEXT AREA
        cons.weightx = 1;
        cons.weighty = 1;
        cons.anchor = GridBagConstraints.FIRST_LINE_START;
        cons.gridwidth = GridBagConstraints.REMAINDER; //icon before element that ends row
        cons.gridheight = 1;
        cons.fill = GridBagConstraints.BOTH;
        cons.insets = new Insets(0, 0, 5, 10);
        chatPanel.add(new JScrollPane(chatTA), cons);

        //TEXT FIELD
        cons = new GridBagConstraints();
        cons.ipady = 5;
        cons.weightx = 1;
        cons.weighty = 0;
        cons.gridwidth = GridBagConstraints.RELATIVE;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.insets = new Insets(0, 0, 5, 10);
        chatPanel.add(this.messageTF, cons);

        //SEND BUTTON
        cons = new GridBagConstraints();
        cons.weightx = 0;
        cons.weighty = 0;
        cons.gridwidth = GridBagConstraints.REMAINDER;
        cons.fill = GridBagConstraints.NONE;
        cons.insets = new Insets(0, 0, 5, 10);
        chatPanel.add(this.chatJButton, cons);

        return chatPanel;
    }

    private void initPlayersAndIconsLabels() {
        /*Initializing icons */
        for (int i = 0; i < this.bombs.length; ++i) {
            this.bombs[i] = new JLabel();
            this.bombs[i].setIcon(new ImageIcon(joinedIcon));
            this.bombs[i].setEnabled(false);
        }

        /*Initializing empty players labels*/
        for (int i = 0; i < this.playersNames.length; ++i) {
            this.playersNames[i] = new JLabel();
            this.playersNames[i].setHorizontalAlignment(SwingConstants.LEFT); //TODO ???
            this.playersNames[i].setText("");
        }
    }

    public void doBeforeShow() {
        try {
            chatTA.setText("");
            Controller.getInstance().requestGameInfo();
        } catch (NetException ex) {
            JOptionPane.showMessageDialog(this, "Connection was lost.\n" + ex.
                    getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.wizard.setCurrentJPanel(0);
        }
    }

    public void setPlayersNum(int number) {
        this.playersNum = number;
    }

    public void startGame() throws NetException {
        wizard.dispose();
        IModel model = Model.getInstance();
        Controller.getInstance().setReceiveInfoListener((RequestResultListener) Model.
                getInstance());
        GameJFrame jframe = new GameJFrame();
        Model.getInstance().addListener(jframe);
        Controller.getInstance().requestGameMap();
    }

    public void setGameInfo(List<String> info) {
        if (info.get(0).equals("false")) {
            botAddJButton.setEnabled(false);
            botAddJButton.setVisible(false);
            botRemoveJButton.setEnabled(false);
            botRemoveJButton.setVisible(false);
            wizard.setNextButtonEnable(false);
        } else {
            botAddJButton.setAction(new AddBotAction());
        }

        playersNum = Integer.parseInt(info.get(1));

        if (Integer.parseInt(info.get(2)) > 0) {//setting names for existing players
            for (int i = 0; i < Integer.parseInt(info.get(2)); i++) {
                this.setPlayer(i, info.get(i + 3));
                this.bombs[i].setVisible(true);
                this.bombs[i].setEnabled(true);
            }
        }

        for (int i = Integer.parseInt(info.get(2)); i < playersNum; i++) {//removing non existing
            this.setPlayer(i, "");
            this.bombs[i].setEnabled(false);
            this.playersNames[i].setVisible(true);
            this.bombs[i].setVisible(true);
        }

        for (int i = playersNum; i < playersNames.length; i++) {//removing non existing
            this.setPlayer(i, "");
            this.playersNames[i].setVisible(false);
            this.bombs[i].setVisible(false);
        }
    }

    public void setNewMessages(List<String> messages) {
        if (!messages.get(0).equals("No new messages.")) {
            for (String message : messages) {
                chatTA.append(message + "\n");
            }
        }
    }

    private void setPlayer(int id, String name) {
        playersNames[id].setText(name);
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
        return panel;
    }

    private class AddBotAction extends AbstractAction {

        public AddBotAction() {
            putValue(NAME, "Add Bot");
            putValue(SHORT_DESCRIPTION, "Add one bot to this game");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Controller.getInstance().requestJoinBotIntoGame();
            } catch (NetException ex) {
                JOptionPane.showMessageDialog(wizard,
                        "Connection was lost.\n" + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                wizard.setCurrentJPanel(0);
            }
        }
    }

    private class ChatAction extends AbstractAction {

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
                JOptionPane.showMessageDialog(wizard,
                        "Connection was lost.\n" + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                wizard.setCurrentJPanel(0);
            }
        }
    }
}
