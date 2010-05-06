
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.view;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.tcpimpl.Server;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Creator;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Server startup window class.
 * @author Kirilchuck V.E.
 */
public class ServerFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    //
    private final String         BTN_TEXT_DOWN  = "Down";
    private final String         BTN_TEXT_RAISE = "Raise";
    private final ServerInfo     info           = new ServerInfo();
    private final JLabel         portLabel      = new JLabel("Server Port");
    private final JTextField     portField      = new JTextField("" + Constants.DEFAULT_PORT);
    private final AbstractButton btnStatus      = new JButton("Status");
    private final AbstractButton btnControl     = new JButton(BTN_TEXT_RAISE);
    private IServer              server;

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

            server = new Server(port);    // Now always Asynchronous server with asynchronous session
            server.setChangeListener(info);
            info.clearLog();
            server.start();
        } catch (NumberFormatException ex) {                     // parse errors
            showErrorMessage(ex.getMessage());

            return;
        } catch (IllegalArgumentException ex) {                  // wrong port(not in 0..65535)
            showErrorMessage(ex.getMessage());

            return;
        } catch (IOException ex) {                               // sockets errors. Server logging them.
            showErrorMessage(ex.getMessage());

            return;
        } catch (IllegalStateException ex) {                     // must never happen
            showErrorMessage(ex.getMessage());

            return;
        }

        btnStatus.setEnabled(true);
        btnControl.setText(BTN_TEXT_DOWN);
    }

    private void downServer() {
        try {
            if (server != null) {
                server.shutdown();
            } else {
                String message = "No server. Can`t shutdown";

                showErrorMessage(message);
            }
        } catch (IOException ex) {              // Server loggs IOErrors and throw them again.
            showErrorMessage(ex.getMessage());

            return;
        } catch (IllegalStateException ex) {    // must never happen
            showErrorMessage(ex.getMessage());

            return;
        }

        btnStatus.setEnabled(false);
        btnControl.setText(BTN_TEXT_RAISE);
    }

    private void showErrorMessage(String message) {
        Creator.createErrorDialog(this, message, "Error");
    }

    private class ServerControlButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (btnControl.getText().equals(BTN_TEXT_RAISE)) {
                raiseServer();
            } else {
                downServer();
            }
        }
    }


    private class StatusButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            info.setVisible(true);
        }
    }
}
