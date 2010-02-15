/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.amse.bomberman.server.net.IServer;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ServerInfo extends JFrame implements ServerChangeListener{

    private static final long serialVersionUID = 1L;

    private IServer server = null;

    private final JTextArea log = new JTextArea();

    public ServerInfo() {
        
        /*initial form properties*/
        this.setTitle("server status");
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setMinimumSize(new Dimension(400, 300));
        this.setResizable(false);
        this.setLocationRelativeTo(null); //null for center position
        
        /*setting main layout of server control JFrame*/
        this.setLayout(new GridLayout(2, 1));

        JPanel infoPanel = new JPanel(new FlowLayout());
        this.add(infoPanel);

        /*settings of log text area*/
        log.setEditable(false);

        /*adding log to down panel*/
        JPanel logPanel = new JPanel(new GridLayout());
        logPanel.add(new JScrollPane(log));
        this.add(logPanel);

        //pack();
    }

    public void setServer(IServer server){
        this.server = server;
        //initLogArea();
    }

    public void stateChanged() {
         //log.setText("");
         //initLogArea();
    }

    public synchronized void addedToLog(String line) {
        //log.append(line + "\n");
    }
    
    public void initLogArea(){
        //List<String> lines = server.getLog();
        //for (String string : lines) {
        //    addedToLog(string);
        //}
    }

}
