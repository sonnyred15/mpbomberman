package org.amse.bomberman.client.view;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.amse.bomberman.client.model.Model;
import org.amse.bomberman.client.net.Connector;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author michail korovkin
 */
public class StartJFrame extends JFrame {
    private final int height = 150;
    private final int width = 240;
    private JButton connectJButton = new JButton();
    //private JButton ipDefault = new JButton();
    private JTextField ipTF = new JTextField();
    //private JButton portDefault = new JButton();
    private JTextField portTF = new JTextField();

    public StartJFrame() {
        super("Let's start BomberManing!!!");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(400, 250);
        setMinimumSize(new Dimension(width / 2, height / 2));

        Container c = getContentPane();
        c.setLayout(new FlowLayout());

        Box bottomBox = Box.createHorizontalBox();
        JLabel ipLabel = new JLabel("IP");
        ipLabel.setPreferredSize(new Dimension(width/8, 20));
        ipLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomBox.add(ipLabel);
       // bottomBox.add(Box.createHorizontalStrut(10));
        ipTF.setPreferredSize(new Dimension(width/2, 20));
        try {
            ipTF.setText(InetAddress.getByName("localhost").getHostAddress());
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        bottomBox.add(ipTF);
        //bottomBox.add(Box.createHorizontalStrut(10));
        //bottomBox.add(ipDefault);

        Box centralBox = Box.createHorizontalBox();
        JLabel portLabel = new JLabel("Port");
        portLabel.setPreferredSize(new Dimension(width/8, 20));
        portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        centralBox.add(portLabel);
        //centralBox.add(Box.createHorizontalStrut(10));
        portTF.setPreferredSize(new Dimension(width/2, 20));
        portTF.setText("" + Constants.DEFAULT_PORT);
        centralBox.add(portTF);
        //centralBox.add(Box.createHorizontalStrut(10));
        //centralBox.add(portDefault);
        
        c.add(bottomBox);
        c.add(centralBox);
        c.add(connectJButton);

        //ipDefault.setAction(new DefaultIPAction(this));
        //portDefault.setAction(new DefaultPortAction(this));
        connectJButton.setAction(new ConnectAction(this));

        IConnector connector = new Connector();
        Model.getInstance().setConnector(connector);
        setResizable(false);
        setVisible(true);
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
    /*public static class DefaultIPAction extends AbstractAction {
        StartJFrame parent;

        public DefaultIPAction(StartJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Default IP");
            putValue(SHORT_DESCRIPTION, "Set default IP");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            try {
                parent.setIP(InetAddress.getByName("localhost").getHostAddress());
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
        }
    }
    public static class DefaultPortAction extends AbstractAction {
        StartJFrame parent;

        public DefaultPortAction(StartJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Default Port");
            putValue(SHORT_DESCRIPTION, "Set default Port");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            parent.setPort(PORT);
        }
    }*/
    public static class ConnectAction extends AbstractAction {
        StartJFrame parent;

        public ConnectAction(StartJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "CONNECT");
            putValue(SHORT_DESCRIPTION, "Connect to server at the directed " +
                    "ip address and port");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            IConnector con = Model.getInstance().getConnector();
            try {
                con.сonnect(parent.getIPAddress(), parent.getPort());
                parent.dispose();
                ServerInfoJFrame serverJFrame = new ServerInfoJFrame();
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parent,"Can not connect to: " 
                        + ex.getMessage() + " Please check server IP."
                        , "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parent,"Can not connect to the server."
                        + "\nPerhaps it is unavailable now\nor you set incorrect " +
                        "server Address.\nPlease check server IP and port."
                        , "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
