/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.net.IServer;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ServerInfo extends JFrame implements LogChangeListener {

    private static final long serialVersionUID = 1L;
    private final String SHUTDOWNED_LABEL_TEXT = "Shutdowned: ";
    private final String STARTED_GAMES_LABEL_TEXT = "Started games: ";
    private final String UNSTARTED_GAMES_LABEL_TEXT = "Unstarted games: ";
    private final String CLIENTS_LABEL_TEXT = "Clients: ";
    private final String TIME_LABEL_TEXT = "Working time(sec.): ";
    private final String PORT_LABEL_TEXT = "Port: ";
    //
    private IServer server = null;
    private final JTextArea log = new JTextArea();
    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    //
    private final JLabel labelShutdowned = new JLabel(SHUTDOWNED_LABEL_TEXT);
    private final JLabel labelPort = new JLabel(PORT_LABEL_TEXT);
    private final JLabel labelStartedGames = new JLabel(STARTED_GAMES_LABEL_TEXT);
    private final JLabel labelUnstartedGames = new JLabel(UNSTARTED_GAMES_LABEL_TEXT);
    private final JLabel labelClients = new JLabel(CLIENTS_LABEL_TEXT);
    private final JLabel labelTime = new JLabel(TIME_LABEL_TEXT);

    public ServerInfo() {

        /*initial form properties*/
        this.setTitle("server status");
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setMinimumSize(new Dimension(400, 300));
        this.setResizable(true);
        //this.setLocationRelativeTo(null); //null for center position

        /*setting main layout of server control JFrame*/
        this.setLayout(new GridLayout(2, 1));

        /*adding elements to up panel*/
        JPanel infoPanel = new JPanel(new GridLayout(3, 2));
        infoPanel.add(labelShutdowned);
        infoPanel.add(labelPort);
        infoPanel.add(labelClients);
        infoPanel.add(labelUnstartedGames);
        infoPanel.add(labelStartedGames);
        infoPanel.add(labelTime);
        this.add(infoPanel);

        /*settings of log text area*/
        log.setEditable(false);

        /*adding log to down panel*/
        JPanel logPanel = new JPanel(new GridLayout());
        logPanel.add(new JScrollPane(log));
        this.add(logPanel);

        //pack();
    }

    public void setServer(IServer server) {
        this.server = server;
        timer.scheduleAtFixedRate(new Runnable() {

            public void run() {
                stateChanged();
            }

        }, 1000, 1000, TimeUnit.MILLISECONDS);
        //initLogArea();
    }

    public void stateChanged() {
        if (this.server != null) {
            this.labelPort.setText(PORT_LABEL_TEXT + this.server.getPort());
            this.labelClients.setText(CLIENTS_LABEL_TEXT + this.server.getClientsNum());
            this.labelShutdowned.setText(SHUTDOWNED_LABEL_TEXT + this.server.isShutdowned());

            List<Game> games = this.server.getGamesList();
            int startedGamesCount = 0;
            int unstartedGamesCount = 0;
            for (Game game : games) {
                if (game.isStarted()) {
                    startedGamesCount++;
                } else if (!game.isStarted()) {
                    unstartedGamesCount++;
                }
            }

            this.labelStartedGames.setText(STARTED_GAMES_LABEL_TEXT + startedGamesCount);
            this.labelUnstartedGames.setText(UNSTARTED_GAMES_LABEL_TEXT + unstartedGamesCount);
            this.labelTime.setText(TIME_LABEL_TEXT + this.server.getWorkTime());
        }
    }

    public synchronized void addedToLog(String line) {
        log.append(line + "\n");
        log.setCaretPosition(log.getText().length() - 1 - line.length());
    }

    public synchronized void clearLog() {
        log.setText("");
    }

    public void initLogArea() {
        //List<String> lines = server.getLog();
        //for (String string : lines) {
        //    addedToLog(string);
        //}
    }

}
