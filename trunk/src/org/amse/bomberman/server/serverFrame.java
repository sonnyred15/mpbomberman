/*
 * serverFrame.java
 *
 * Created on 4 Ноябрь 2009 г., 20:19
 */

package org.amse.bomberman.server;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import org.amse.bomberman.server.net.Net;

/**
 *
 * @author  chibis
 */
public class serverFrame extends javax.swing.JFrame {
    private int port = 10500;
    private Net net;
    private AbstractButton startButton = new JButton("START LISTEN");
    private AbstractButton stopButton = new JButton("STOP LISTEN");
    /** Creates new form serverFrame */
    public serverFrame() throws IOException {
        initComponents();
        this.setLocation(300, 300);
        this.setSize(300, 200);
        this.setResizable(false);
        
        this.net = new Net(port);
        
        this.setLayout(new GridLayout(2, 1));
        this.add(startButton);
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
        this.add(stopButton);
        this.setVisible(true);
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(100, 100));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new serverFrame().setVisible(true);
                } catch (IOException ex) {
                   // Logger.getLogger(serverFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
