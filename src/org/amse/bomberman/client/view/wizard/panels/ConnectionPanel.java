package org.amse.bomberman.client.view.wizard.panels;

import java.text.ParseException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.awt.*;
import javax.swing.*;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import javax.swing.text.PlainDocument;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.util.ImageUtilities;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Creator;

/**
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
@SuppressWarnings("serial")
public class ConnectionPanel extends JPanel {

    private static final String BACKGROUND_RESOURCE_NAME
            = "/org/amse/bomberman/client/view/resources/cover.png";

    private Image image;

    private Color textColor = Color.ORANGE;

    private final int width = 640;
    private final int height = 480;
    private final int textWidth = 80;

    private JFormattedTextField ipTF;
    private JFormattedTextField portTF;
    private JTextField playerNameTF;

    private final String defaultIp = "127. 0 . 0 . 1 ";

    public ConnectionPanel() { 
        setSize(width, height);
        initComponents();
        this.initBackgroundImage();
        this.setVisible(true);
    }

    public InetAddress getIPAddress() throws UnknownHostException {
        return InetAddress.getByName(ipTF.getText().replaceAll(" ", ""));
    }

    public int getPort() {
        return Integer.parseInt(portTF.getText().replaceAll(" ", ""));
    }

    public String getPlayerName() {
        return playerNameTF.getText();
    }

    private void initComponents() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 70, 150));
        JComponent textFields = createMainPanel();
        this.add(textFields);
    }

    private JComponent createMainPanel() {
        Box MainBox = Box.createVerticalBox();
        MainBox.add(createTopBox());
        MainBox.add(Box.createVerticalStrut(20));
        MainBox.add(createCentralBox());
        MainBox.add(Box.createVerticalStrut(20));
        MainBox.add(createBottomBox());

        return MainBox;
    }

    private Box createTopBox() {
        Box topBox = Box.createHorizontalBox();

        JLabel ipLabel = new JLabel("IP");       
        ipLabel.setForeground(textColor);        

        try {
            MaskFormatter mf = new MaskFormatter("###.###.###.###");
            mf.setPlaceholder(defaultIp);
            ipTF = new JFormattedTextField(mf);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        ipTF.setFocusLostBehavior(JFormattedTextField.COMMIT);
        ipTF.setPreferredSize(new Dimension(textWidth, 20));
        topBox.add(ipLabel);
        topBox.add(Box.createHorizontalStrut(5));
        topBox.add(ipTF);

        return topBox;
    }

    private Box createCentralBox() {
        Box centralBox = Box.createHorizontalBox();

        JLabel portLabel = new JLabel("Port");
        portLabel.setForeground(textColor);        
        portLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumIntegerDigits(5);
        nf.setGroupingUsed(false);

        NumberFormatter portFormatter = new NumberFormatter(nf);
        portFormatter.setAllowsInvalid(false);
        portTF = new JFormattedTextField(portFormatter);
        portTF.setPreferredSize(new Dimension(textWidth, 20));
        portTF.setValue(Constants.DEFAULT_PORT);

        centralBox.add(portLabel);
        centralBox.add(Box.createHorizontalStrut(5));
        centralBox.add(portTF);

        return centralBox;
    }

    private Box createBottomBox() {
        Box bottomBox = Box.createHorizontalBox();

        JLabel nameLabel = new JLabel("Player");
        nameLabel.setForeground(textColor);
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        //TODO CLIENT push to client constants
        final int maxLength = 10;
        PlainDocument doc = new PlainDocument();
        doc.setDocumentFilter(new DocumentFilter(){

            @Override
            public void insertString(FilterBypass fb, int offset,
                    String string, AttributeSet attr) throws BadLocationException {
                replace(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length,
                    String text, AttributeSet attrs) throws BadLocationException {
                int addCount = (text==null ? 0 : text.length());
                int count = fb.getDocument().getLength();
                if(count - length +addCount > maxLength) {
                    UIManager.getLookAndFeel().provideErrorFeedback(playerNameTF);
                } else {
                    fb.replace(offset, length, text, attrs);
                }
            }
        });
        playerNameTF = new JTextField(doc, "Unnamed", 8);
        playerNameTF.setPreferredSize(new Dimension(textWidth, 20));

        bottomBox.add(nameLabel);
        bottomBox.add(Box.createHorizontalStrut(5));
        bottomBox.add(playerNameTF);

        return bottomBox;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.image != null) {//actually image is BufferedImage so drawImage will return true.
            g.drawImage(this.image, 0, 0, null);
        }
    }

    private void initBackgroundImage() {
        try {
            this.image = ImageUtilities.initImage(BACKGROUND_RESOURCE_NAME,
                                                  this.getWidth(),
                                                  this.getHeight());
        } catch (IOException ex) {
            Creator.createErrorDialog(this, "Can`t load background!", ex.getMessage());
        }
    }
}
