package org.amse.bomberman.client.view.mywizard;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Michael Korovkin
 */
public class Panel1 extends JPanel implements Updating{
    private final int height = 480;
    private final int width = 640;
    private MyWizard parent;
    private JTextField ipTF = new JTextField();
    private JTextField portTF = new JTextField();

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
        this.setLayout(new FlowLayout());
        Box bottomBox = Box.createHorizontalBox();
        JLabel ipLabel = new JLabel("IP");
        ipLabel.setPreferredSize(new Dimension(width/8, 20));
        ipLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomBox.add(ipLabel);
        ipTF.setPreferredSize(new Dimension(width/4, 20));
        try {
            ipTF.setText(InetAddress.getByName("localhost").getHostAddress());
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        bottomBox.add(ipTF);

        Box centralBox = Box.createHorizontalBox();
        JLabel portLabel = new JLabel("Port");
        portLabel.setPreferredSize(new Dimension(width/8, 20));
        portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        centralBox.add(portLabel);
        portTF.setPreferredSize(new Dimension(width/4, 20));
        portTF.setText("" + Constants.DEFAULT_PORT);
        centralBox.add(portTF);

        Box MainBox = Box.createVerticalBox();
        MainBox.add(Box.createVerticalGlue());
        MainBox.add(bottomBox);
        MainBox.add(Box.createVerticalStrut(20));
        MainBox.add(centralBox);
        MainBox.add(Box.createVerticalGlue());
        this.add(MainBox);
    }
}
