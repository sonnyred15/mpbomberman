/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.amse.bomberman.server.net.IServer;
import org.amse.bomberman.server.net.tcpimpl.Server;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.impl.ConsoleLog;

/**
 * Server startup window.
 * @author Kirilchuck V.E.
 */
public class ServerFrame extends JFrame {

    private int port;
    private IServer server;
    private JLabel portLabel = new JLabel("Server Port");
    private JTextField portField = new JTextField("" + Constants.DEFAULT_PORT);
    private AbstractButton startButton = new JButton("Raise");
    private AbstractButton stopButton = new JButton("Down");

    private ConsoleLog log = new ConsoleLog();

    /** Creates new form ServerFrame */
    public ServerFrame(){

        /*initial form properties*/
        this.setTitle("server control");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(200, 100));
        this.setResizable(false);

        /*setting relative(from cur screen resolution) appearing position*/
        int screenHeight = 0;
        int screenWidth = 0;
        try {
           Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
           screenHeight = screenDim.height;
           screenWidth = screenDim.width;
        } catch (HeadlessException ex) {
            log.println(ex.getMessage());
        }
        this.setLocation(screenHeight/2, screenWidth/2);

        /*setting main layout of server control JFrame*/
        this.setLayout(new GridLayout(2, 1));

        /*port label and port textField*/
        JPanel portPanel = new JPanel(new FlowLayout());
        portPanel.add(portLabel);
        portPanel.add(portField);
        this.add(portPanel);

        /*server raise and server shutdown buttons*/
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(startButton);
        stopButton.setEnabled(false);
        buttonPanel.add(stopButton);
        this.add(buttonPanel);

        /*raise server button actionListener*/
        startButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    port = Integer.parseInt(portField.getText()); //throws NumberFormatException

                    if (port < 0 || port > 65535) {
                        throw new NumberFormatException(); //catching
                    }
                    
                    if (server == null) { //if it is first initialization
                        server = new Server(port);
                    } else if (server.getPort() != port) { //if we want to raise server on new port
                        if (!server.isShutdowned()) {
                            server.shutdown();
                        }
                        log.println("Raising server on new port");
                        server = new Server(port);
                    }
                    server.start();
                } catch (NumberFormatException ex) { //parse errors                    
                    //portField.setText("Error");
                    log.println(ex.getMessage() + 
                            "Must be int from 0 to 65535(inclusive)");
                    return;
                } catch (IOException ex) { //sockets errors. Server logging them.
                    //portField.setText("Error");
                    //log.println(ex.getMessage());
                    return;
                } catch (IllegalStateException ex) {
                    //portField.setText("Error");
                    log.println(ex.getMessage());
                    return;
                }
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
            }
        });

        /*shutdown server button actionListener*/
        stopButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    if (server != null) {
                        server.shutdown();
                    } else {
                        throw new IllegalStateException("No server. Can`t shutdown."); //catching
                    }
                } catch (IOException ex) { //Server loggs IOErrors and throw them again.
                    //portField.setText("Error");
                    //log.println(ex.getMessage());
                    return;
                } catch (IllegalStateException ex) {
                    //portField.setText("Error");
                    log.println(ex.getMessage());
                    return;
                }
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });

        this.setVisible(true);
    }
}
