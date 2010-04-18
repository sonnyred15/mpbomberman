
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.view;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.net.IServer;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Class that represents ServerInfo JFrame. ServerInfo JFrame shows
 * log of server and some of it`s parameters
 * like number of started/unstarted games, number of clients, etc.
 * @author Kirilchuk V.E.
 */
public class ServerInfo extends JFrame implements ServerChangeListener {
    private static final long serialVersionUID = 1L;

    //
    private final String CLIENTS_LABEL_TEXT = "Clients: ";
    private final String PORT_LABEL_TEXT = "Port: ";
    private final String SHUTDOWNED_LABEL_TEXT = "Shutdowned: ";
    private final String STARTED_GAMES_LABEL_TEXT = "Started games: ";
    private final String UNSTARTED_GAMES_LABEL_TEXT = "Unstarted games: ";

    //
    private final JTextArea log = new JTextArea();
    private final JLabel    labelUnstartedGames =
                                         new JLabel(UNSTARTED_GAMES_LABEL_TEXT);
    private final JLabel    labelStartedGames =
                                         new JLabel(STARTED_GAMES_LABEL_TEXT);

    //
    private final JLabel labelShutdowned = new JLabel(SHUTDOWNED_LABEL_TEXT);
    private final JLabel labelPort       = new JLabel(PORT_LABEL_TEXT);
    private final JLabel labelClients    = new JLabel(CLIENTS_LABEL_TEXT);

    /**
     * Constructor of ServerInfo JFrame. Creates it.
     * To show it you must use standart <code>setVisible(true)</code> method.
     */
    public ServerInfo() {

        /* initial form properties */
        super("server status");
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setMinimumSize(new Dimension(400, 300));
        this.setResizable(true);

        // this.setLocationRelativeTo(null); //null for center position

        /* setting main layout of server control JFrame */
        this.setLayout(new GridLayout(2, 1));

        /* adding elements to up panel */
        JPanel infoPanel = new JPanel(new GridLayout(3, 2));

        infoPanel.add(labelShutdowned);
        infoPanel.add(labelPort);
        infoPanel.add(labelClients);
        infoPanel.add(labelUnstartedGames);
        infoPanel.add(labelStartedGames);

        // infoPanel.add(labelTime);
        this.add(infoPanel);

        /* settings of log text area */
        log.setEditable(false);

        /* adding log to down panel */
        JPanel logPanel = new JPanel(new GridLayout());

        logPanel.add(new JScrollPane(log));
        this.add(logPanel);
    }

    /**
     * ServerChangeListener interface method.
     * @see ServerChangeListener
     * @param server Reference to server from which we must take changes.
     */
    public void changed(IServer server) {
        if (server != null) {
            this.labelPort.setText(PORT_LABEL_TEXT + server.getPort());
            this.labelClients.setText(CLIENTS_LABEL_TEXT +
                                      server.getSessions().size());
            this.labelShutdowned.setText(SHUTDOWNED_LABEL_TEXT +
                                         server.isShutdowned());

            if (!server.isShutdowned()) {
                List<Game> games = server.getGamesList();
                int        startedGamesCount = 0;
                int        unstartedGamesCount = 0;

                for (Game game : games) {
                    if (game.isStarted()) {
                        startedGamesCount++;
                    } else if (!game.isStarted()) {
                        unstartedGamesCount++;
                    }
                }

                this.labelStartedGames.setText(STARTED_GAMES_LABEL_TEXT +
                                               startedGamesCount);
                this.labelUnstartedGames.setText(UNSTARTED_GAMES_LABEL_TEXT +
                                                 unstartedGamesCount);

            } else {
                this.labelStartedGames.setText(this.labelStartedGames.getText() +
                                               "(was)");
                this.labelUnstartedGames.setText(this.labelUnstartedGames.getText() +
                                                 "(was)");
            }
        }
    }

    /**
     * ServerChangeListener interface method.
     * @see ServerChangeListener
     * @param line message that was added to log.
     */
    public void addedToLog(String line) {
        log.append(line + "\n");
        log.setCaretPosition(log.getText().length() - 1 - line.length());
    }

    /**
     * Clears text of log.
     */
    public void clearLog() {
        log.setText("");
    }
}
