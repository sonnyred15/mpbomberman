package org.amse.bomberman.client.view.bomberwizard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Creator;
/**
 *
 * @author Michael Korovkin
 */
public class Panel1 extends JPanel{
    private static final String BACKGROUND_PATH = "/org/amse/bomberman/client" +
            "/view/resources/cover.png";
    private static final URL BACKGROUND_URL = Panel1.class.getResource(BACKGROUND_PATH);

    private Image image;
    private Color textColor = Color.ORANGE;

    private final int height = 480;
    private final int width = 640;

    private JTextField ipTF = new JTextField();
    private JTextField portTF = new JTextField();
    private JTextField playerNameTF = new JTextField();
    private final int textWidth = 80;

    public Panel1() {
        setSize(width, height);
        initComponents();
        this.setVisible(true);
        this.initBackgroundImage();
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

    public String getPlayerName() {
        return playerNameTF.getText();
    }

    private void initComponents() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT,70,150));
        JComponent textFields = createMainPanel();
        this.add(textFields);
    }

    private JComponent createMainPanel(){
        Box bottomBox = Box.createHorizontalBox();
        JLabel ipLabel = new JLabel("IP");
        //ipLabel.setPreferredSize(new Dimension(width / 8, 20));
        ipLabel.setForeground(textColor);
        ipLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        ipTF.setPreferredSize(new Dimension(textWidth, 20));
        try {
            ipTF.setText(InetAddress.getByName("localhost").getHostAddress());
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        bottomBox.add(ipLabel);
        bottomBox.add(Box.createHorizontalStrut(5));
        bottomBox.add(ipTF);

        Box centralBox = Box.createHorizontalBox();
        JLabel portLabel = new JLabel("Port");
        portLabel.setForeground(textColor);
        //portLabel.setPreferredSize(new Dimension(width / 8, 20));
        portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        portTF.setPreferredSize(new Dimension(textWidth, 20));
        portTF.setText("" + Constants.DEFAULT_PORT);
        centralBox.add(portLabel);
        centralBox.add(Box.createHorizontalStrut(5));
        centralBox.add(portTF);

        Box downBox = Box.createHorizontalBox();
        JLabel nameLabel = new JLabel("Player");
        nameLabel.setForeground(textColor);
        //nameLabel.setPreferredSize(new Dimension(width / 8, 20));
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        playerNameTF.setPreferredSize(new Dimension(textWidth, 20));
        playerNameTF.setText("unnamed");
        downBox.add(nameLabel);
        downBox.add(Box.createHorizontalStrut(5));
        downBox.add(playerNameTF);

        Box MainBox = Box.createVerticalBox();
        MainBox.add(bottomBox);
        MainBox.add(Box.createVerticalStrut(20));
        MainBox.add(centralBox);
        MainBox.add(Box.createVerticalStrut(20));
        MainBox.add(downBox);

        return MainBox;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(this.image!=null){//actually image is BufferedImage so drawImage will return true.
            g.drawImage(this.image, 0, 0, null);
        }
    }

    private void initBackgroundImage() {
        try{
            this.image = ImageIO.read(BACKGROUND_URL);//returns BufferedImage
            this.image =
                    this.image.getScaledInstance(this.getWidth(),
                                                 this.getHeight(),
                                                 Image.SCALE_SMOOTH);
        }catch (IOException ex){
            Creator.createErrorDialog(this, "Can`t load background!", ex.getMessage());
            this.image = null;
        }
    }
}
