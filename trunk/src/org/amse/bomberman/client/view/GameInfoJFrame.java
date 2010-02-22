package org.amse.bomberman.client.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.net.impl.Connector;
import org.amse.bomberman.client.net.impl.Connector.NetException;
import org.amse.bomberman.client.view.mapjframe.MapJFrame;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Michail Korovkin
 */
public class GameInfoJFrame extends JFrame{
    private int serverNumber;
    private int playersNum;
    private JLabel[] players = new JLabel[Constants.MAX_PLAYERS];
    private JButton startJButton = new JButton();
    private JButton botJButton = new JButton();
    private JButton cancelJButton = new JButton();
    private Timer timer;
    private final int width = 400;
    private final int height = 300;

    public GameInfoJFrame(int myNumber, int number) {
        super("GameInfo");
        serverNumber = myNumber;
        playersNum = number;
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(400, 150);
        setMinimumSize(new Dimension(width / 2, height / 2));
        for (int i = 0; i < playersNum; i++) {
            players[i] = new JLabel();
            players[i].setBorder(new LineBorder(Color.DARK_GRAY));
            players[i].setHorizontalAlignment(SwingConstants.CENTER);
        }
        startJButton.setAction(new StartAction(this));
        botJButton.setAction(new AddBotAction(this));
        cancelJButton.setAction(new CancelAction(this));
        try {
            List<String> gameInfo = Connector.getInstance().getMyGameInfo();
            System.out.println(gameInfo.get(0));
            if (gameInfo.get(0).equals("false")) {
                startJButton.setEnabled(false);
            }
            for (int i = 0; i < Integer.parseInt(gameInfo.get(1)); i++) {
                players[i].setText(gameInfo.get(i+2));
            }

            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new GridLayout(playersNum, 1, 10, 10));
            leftPanel.setPreferredSize(new Dimension(100, 150));
            for (int i = 0; i < playersNum; i++) {
                leftPanel.add(players[i]);
            }
            JPanel rightPanel = new JPanel();
            // how calculate sizes???
            rightPanel.setPreferredSize(new Dimension(100, 80));
            rightPanel.setLayout(new GridLayout(3, 1, 10, 10));
            rightPanel.add(startJButton);
            rightPanel.add(botJButton);
            rightPanel.add(cancelJButton);

            Container c = this.getContentPane();
            c.setLayout(new FlowLayout());
            c.add(leftPanel);
            c.add(rightPanel);
            

            setResizable(false);
            setVisible(true);
        } catch (NetException ex) {
            JOptionPane.showMessageDialog(this, "Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            StartJFrame jframe = new StartJFrame();
        }
    }

    public int getGameNumber() {
        return serverNumber;
    }
    public void stopWaitStart() {
        timer.cancel();
    }

    public static class StartAction extends AbstractAction {
        GameInfoJFrame parent;

        public StartAction(GameInfoJFrame jframe) {
            parent = jframe;
            putValue(NAME, "Start");
            putValue(SHORT_DESCRIPTION, "Start this game.");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            IConnector connect = Connector.getInstance();
            try {
                connect.startGame();
                parent.dispose();
                BombMap map = connect.getMap();
                IModel model = Model.getInstance();
                model.setMap(map);
                MapJFrame jframe = new MapJFrame();
                Model.getInstance().addListener(jframe);
                Connector.getInstance().beginUpdating();
            } catch (NetException ex) {
                JOptionPane.showMessageDialog(parent, "Connection was lost.\n"
                        + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                parent.dispose();
                StartJFrame jFrame = new StartJFrame();
            }
        }
    }

    public static class AddBotAction extends AbstractAction {
        GameInfoJFrame parent;

        public AddBotAction(GameInfoJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Add Bot");
            putValue(SHORT_DESCRIPTION, "Add one bot to this game");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Connector.getInstance().joinBotIntoGame(parent.getGameNumber());
                // refresh info of players
                //parent.refreshTable();
            } catch (NetException ex) {
                JOptionPane.showMessageDialog(parent, "Connection was lost.\n"
                        + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                parent.dispose();
                StartJFrame jFrame = new StartJFrame();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Can not join bot to the game: \n"
                        + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public static class CancelAction extends AbstractAction{
        GameInfoJFrame parent;
        public CancelAction(GameInfoJFrame jframe){
            parent = jframe;
            putValue(NAME, "Cancel");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            parent.dispose();
            ServerInfoJFrame jframe = new ServerInfoJFrame();
        }
    }
}
