package org.amse.bomberman.server.view;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.net.Server;

/**
 * Class that represents ServerInfo JFrame. ServerInfo JFrame shows
 * log of server and some of it`s parameters
 * like number of started/unstarted games, number of clients, etc.
 * @author Kirilchuk V.E.
 */
public class ServerInfo extends JFrame {
    private static final long serialVersionUID = 1L;

    //
    private final String CLIENTS_LABEL_TEXT         = "Clients: ";
    private final String PORT_LABEL_TEXT            = "Port: ";
    private final String SHUTDOWNED_LABEL_TEXT      = "Shutdowned: ";
    private final String STARTED_GAMES_LABEL_TEXT   = "Started games: ";
    private final String UNSTARTED_GAMES_LABEL_TEXT = "Unstarted games: ";

    //
    private Server          server;
    private final JTextArea log                 = new JTextArea();
    private final JLabel    labelUnstartedGames = new JLabel(UNSTARTED_GAMES_LABEL_TEXT);
    private final JLabel    labelStartedGames   = new JLabel(STARTED_GAMES_LABEL_TEXT);

    //
    private final JLabel labelShutdowned = new JLabel(SHUTDOWNED_LABEL_TEXT);
    private final JLabel labelPort       = new JLabel(PORT_LABEL_TEXT);
    private final JLabel labelClients    = new JLabel(CLIENTS_LABEL_TEXT);

    //
    private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

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

        // null for center position nothing for upper left corner
        // this.setLocationRelativeTo(null);

        /* adding elements to up panel */
        JPanel infoPanel = new JPanel(new GridLayout(3, 2));

        infoPanel.add(labelShutdowned);
        infoPanel.add(labelPort);
        infoPanel.add(labelClients);
        infoPanel.add(labelUnstartedGames);
        infoPanel.add(labelStartedGames);

        this.add(infoPanel);

        //
        timer.scheduleAtFixedRate(new Runnable() {

            public void run() {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        update();
                    }
                });
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void setServer(Server server) {
        this.server = server;
    }

    private void update() {
        if (server != null) {
            this.labelShutdowned.setText(this.SHUTDOWNED_LABEL_TEXT + this.server.isStopped());
            this.labelPort.setText(this.PORT_LABEL_TEXT + this.server.getPort());
            this.labelClients.setText(this.CLIENTS_LABEL_TEXT + this.server.getSessions().size());

            if(!server.isStopped()) {
                List<Game> games = server.getGameStorage().getGamesList();
                int started = 0;
                int unstarted = 0;
                for (Game game : games) {
                    if(game.isStarted()) {
                        started++;
                    } else {
                        unstarted++;
                    }
                }

                this.labelStartedGames.setText(this.STARTED_GAMES_LABEL_TEXT + started);
                this.labelUnstartedGames.setText(this.UNSTARTED_GAMES_LABEL_TEXT + unstarted);
            }
        }
    }


}
