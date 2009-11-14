/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import org.amse.bomberman.server.net.Net;

/**
 *
 * @author Kirilchuck V.E.
 */
public class serverFrame extends javax.swing.JFrame {
    private int port = 10500;
    private Net net;
    private AbstractButton startButton = new JButton("Raise");
    private AbstractButton stopButton = new JButton("Down");
    /** Creates new form serverFrame */
    public serverFrame() throws IOException {
        this.setTitle("server control");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(190, 70));
        this.setResizable(false);
        this.setLocation(300, 300);        

        this.net = new Net(port);
        
        this.setLayout(new FlowLayout());
        this.add(startButton);
        this.add(stopButton);
        //pack();
        
        startButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    net.startAcceptingClients();
                } catch (IOException ex) {
                    //Logger.getLogger(serverFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        stopButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    net.stopAcceptingClients();
                } catch (IOException ex) {
                    //Logger.getLogger(serverFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });                        
        
        this.setVisible(true);
    }
}
