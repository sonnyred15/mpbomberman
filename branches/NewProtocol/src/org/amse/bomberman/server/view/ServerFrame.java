
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.view;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.net.Server;
import org.amse.bomberman.server.net.tcpimpl.servers.TcpServer;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Creator;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.text.ParseException;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;

/**
 * Server startup window class.
 * @author Kirilchuck V.E.
 */
public class ServerFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    //
    private final String         BTN_TEXT_DOWN  = "Down";
    private final String         BTN_TEXT_RAISE = "Raise";
    private final ServerInfo     infoFrame      = new ServerInfo();
    private final JLabel         portLabel      = new JLabel("Server Port");    
    private final AbstractButton btnStatus      = new JButton("Status");
    private final AbstractButton btnControl     = new JButton(BTN_TEXT_RAISE);
    private Server              server;

    //
    private final JFormattedTextField portField
                                                = new JFormattedTextField();
    MaskFormatter f;
    {
        portField.setColumns(5);        
        try{
            f = new MaskFormatter("#####");
            f.setOverwriteMode(true);
            f.setPlaceholder("" + Constants.DEFAULT_PORT);
            f.setPlaceholderCharacter('0');
            f.install(portField);            
        } catch (ParseException ex){
            throw new RuntimeException("Wrong configuration " +
                    "of formatter for input." + ex.getMessage());
        }
    }

    /**
     * Constructor of ServerFrame. Create and show it.
     */
    public ServerFrame() {

        /* initial form properties */
        super("server control");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(240, 100));
        this.setResizable(false);
        this.setLocationRelativeTo(null);    // null for center position

        /* setting main layout of server control JFrame */
        this.setLayout(new GridLayout(2, 1));

        /* port label and port textField */
        JPanel portPanel = new JPanel(new FlowLayout());

        portPanel.add(portLabel);
        portPanel.add(portField);
        this.add(portPanel);

        /* server control and status buttons */
        JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(btnControl);
        btnStatus.setEnabled(false);
        buttonPanel.add(btnStatus);
        this.add(buttonPanel);

        /* server control button actionListener */
        btnControl.addActionListener(new ServerControlButtonListener());

        /* server status button actionListener */
        btnStatus.addActionListener(new StatusButtonListener());
        this.setVisible(true);
    }

    private void raiseServer() {
        try {
            int port = Integer.parseInt(portField.getText());    // throws NumberFormatException

            server = new TcpServer(port);//TODO mb make fabric?
            server.start();
            infoFrame.setServer(server);
            btnStatus.setEnabled(true);
            btnControl.setText(BTN_TEXT_DOWN);
        } catch (NumberFormatException ex) {                     // parse errors
            showErrorMessage(ex.getMessage());
        } catch (IllegalArgumentException ex) {                  // wrong port(not in 0..65535)
            showErrorMessage(ex.getMessage());
        } catch (IOException ex) {                               // sockets errors. Server logging them.
            showErrorMessage(ex.getMessage());
        } catch (IllegalStateException ex) {                     // must never happen
            showErrorMessage(ex.getMessage());
        }
    }

    private void downServer() {
        try {
            server.stop();
            btnStatus.setEnabled(false);
            btnControl.setText(BTN_TEXT_RAISE);
        } catch (IOException ex) {              // Server loggs IOErrors and throw them again.
            showErrorMessage(ex.getMessage());
        } catch (IllegalStateException ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    private void showErrorMessage(String message) {
        Creator.createErrorDialog(this, "Error.", message);
    }

    private class ServerControlButtonListener implements ActionListener { //TODO bad practice code...
        @Override
        public void actionPerformed(ActionEvent e) {
            if (btnControl.getText().equals(BTN_TEXT_RAISE)) {
                raiseServer();
            } else {
                infoFrame.setVisible(false); // Note that timer in it still works!!
                downServer();
            }
        }
    }

    private class StatusButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            infoFrame.setVisible(true);
        }
    }
}
