package org.amse.bomberman.client.view.mywizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Michael Korovkin
 * @author Kirilchuk V.E.
 */
public class Panel1 extends JPanel implements Updating {

    private static final String BACKGROUND_PATH = "/org/amse/bomberman/client/view/resources/cover.jpg";
    private static final URL BACKGROUND_URL = Panel1.class.getResource(BACKGROUND_PATH);
    private static final Image BACKGROUND_IMAGE = Toolkit.getDefaultToolkit().createImage(BACKGROUND_URL);

    private final int height = 480;
    private final int width = 640;
    private MyWizard parent;
    private JTextField ipTF = new JTextField();
    private JTextField portTF = new JTextField();
    private JTextField playerNameTF = new JTextField();

    public Panel1(MyWizard jframe) {
        parent = jframe;
        setSize(width, height);
        initComponents();
        this.setVisible(true);
    }

    public InetAddress getIPAddress() throws UnknownHostException {
        return InetAddress.getByName(ipTF.getText());
    }

    public int getPort() {
        return Integer.parseInt(portTF.getText());
    }

    public void setIP(String ipValue) {
        ipTF.setText(ipValue);
    }

    public void setPort(int portValue) {
        StringBuilder sb = new StringBuilder();
        sb.append(portValue);
        portTF.setText(sb.toString());
    }

    public void getServerInfo() {
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.add(Box.createRigidArea(new Dimension(this.getWidth(), 100)),
                BorderLayout.SOUTH);
        this.add(Box.createRigidArea(new Dimension(0, this.getHeight())),
                 BorderLayout.WEST);
        this.add(Box.createRigidArea(new Dimension(this.getWidth(), 150)),
                BorderLayout.NORTH);
        this.add(Box.createRigidArea(new Dimension(300, this.getHeight())),
                 BorderLayout.EAST);

        JComponent textFields = createMainPanel();
        JPanel mainPanel = new JPanel(new FlowLayout());
        mainPanel.setOpaque(false);
        mainPanel.add(textFields);
        this.add(mainPanel, BorderLayout.CENTER);


    }

    private JComponent createMainPanel(){                
        Box bottomBox = Box.createHorizontalBox();
        JLabel ipLabel = new JLabel("IP");
        ipLabel.setPreferredSize(new Dimension(width / 8, 20));
        ipLabel.setForeground(Color.red);
        ipLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomBox.add(ipLabel);
        ipTF.setPreferredSize(new Dimension(width / 4, 20));
        try {
            ipTF.setText(InetAddress.getByName("localhost").getHostAddress());
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        bottomBox.add(ipTF);

        Box centralBox = Box.createHorizontalBox();
        JLabel portLabel = new JLabel("Port");
        portLabel.setForeground(Color.red);
        portLabel.setPreferredSize(new Dimension(width / 8, 20));
        portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        centralBox.add(portLabel);
        portTF.setPreferredSize(new Dimension(width / 4, 20));
        portTF.setText("" + Constants.DEFAULT_PORT);
        centralBox.add(portTF);

        Box downBox = Box.createHorizontalBox();
        JLabel nameLabel = new JLabel("Player");
        nameLabel.setForeground(Color.red);
        nameLabel.setPreferredSize(new Dimension(width / 8, 20));
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        downBox.add(nameLabel);
        playerNameTF.setPreferredSize(new Dimension(width / 4, 20));
        playerNameTF.setText("unnamed");
        downBox.add(playerNameTF);

        Box MainBox = Box.createVerticalBox();
        //MainBox.add(Box.createVerticalGlue());
        MainBox.add(bottomBox);
        MainBox.add(Box.createVerticalStrut(20));
        MainBox.add(centralBox);
        MainBox.add(Box.createVerticalStrut(20));
        MainBox.add(downBox);
        //MainBox.add(Box.createVerticalGlue());

        return MainBox;
    }

//    private void initComponents2() {
//        this.setLayout(new FlowLayout());
//        Box bottomBox = Box.createHorizontalBox();
//        JLabel ipLabel = new JLabel("IP");
//        ipLabel.setPreferredSize(new Dimension(width / 8, 20));
//        ipLabel.setHorizontalAlignment(SwingConstants.RIGHT);
//        bottomBox.add(ipLabel);
//        ipTF.setPreferredSize(new Dimension(width / 4, 20));
//        try {
//            ipTF.setText(InetAddress.getByName("localhost").getHostAddress());
//        } catch (UnknownHostException ex) {
//            ex.printStackTrace();
//        }
//        bottomBox.add(ipTF);
//
//        Box centralBox = Box.createHorizontalBox();
//        JLabel portLabel = new JLabel("Port");
//        portLabel.setPreferredSize(new Dimension(width / 8, 20));
//        portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
//        centralBox.add(portLabel);
//        portTF.setPreferredSize(new Dimension(width / 4, 20));
//        portTF.setText("" + Constants.DEFAULT_PORT);
//        centralBox.add(portTF);
//
//        Box MainBox = Box.createVerticalBox();
//        MainBox.add(Box.createVerticalGlue());
//        MainBox.add(bottomBox);
//        MainBox.add(Box.createVerticalStrut(20));
//        MainBox.add(centralBox);
//        MainBox.add(Box.createVerticalGlue());
//        this.add(MainBox);
//    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //BAD HACK!!! //TODO !
        if (!g.drawImage(Panel1.BACKGROUND_IMAGE, 0, 0, this.getWidth(), this.
                getHeight(), null)) {
            this.repaint();
        }
    }
}
