/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server;

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
import org.amse.bomberman.server.net.Net;

/**
 *
 * @author Kirilchuck V.E.
 */
public class serverFrame extends JFrame {

    private int port;
    private Net net;
    private JLabel portLabel = new JLabel("Server Port");
    private JTextField portField = new JTextField("" + Net.DEFAULT_PORT);
    private AbstractButton startButton = new JButton("Raise");
    private AbstractButton stopButton = new JButton("Down");

    /** Creates new form serverFrame */
    public serverFrame() throws IOException {

        /*initial form properties*/
        this.setTitle("server control");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(190, 90));
        this.setResizable(false);
        this.setLocation(300, 300);


        this.setLayout(new GridLayout(2, 1));

        /*port label and port textField*/
        JPanel portPanel = new JPanel(new FlowLayout());
        portPanel.add(portLabel);
        portPanel.add(portField);
        this.add(portPanel);

        /*server raisel and server shutdown buttons*/
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        this.add(buttonPanel);


        startButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    int port = Integer.parseInt(portField.getText());

                    if (port < 0 || port > 65535) {
                        throw new NumberFormatException();
                    }
                    net = new Net(port);
                    net.startAcceptingClients();
                } catch (NumberFormatException ex) {
                    portField.setText("Must be int from 1 to 65535(incl)");
                } catch (IOException ex) {
                    portField.setText("Port is already in use");
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    net.stopAcceptingClients();
                } catch (IOException ex) {
                    portField.setText("Error while closing socket");
                }
            }
        });

        this.setVisible(true);
    }
}
