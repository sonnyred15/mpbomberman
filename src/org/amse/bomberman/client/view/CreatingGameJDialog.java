package org.amse.bomberman.client.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.net.impl.Connector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.view.mywizard.MyWizard;
import org.amse.bomberman.client.view.mywizard.Panel2;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Michail Korovkin
 */
public class CreatingGameJDialog extends JDialog {
    private MyWizard parent;
    private int width = 280;
    private int heigth = 180;
    private JComboBox mapBox;
    private JTextField gameNameTF = new JTextField();
    private JSpinner playersSpinner;
    private JButton createJButton = new JButton();
    private JButton cancelJButton = new JButton();
    private final int LINE_H = 20;
    private final int LABEL_SIZE = width/3;
    
    public CreatingGameJDialog(MyWizard wizard) throws NetException{
        super(wizard, "Create new Game", true);
        parent = wizard;
        setSize(width, heigth);
        setLocation(parent.getX()-width, parent.getY());

        String[] maps =  Connector.getInstance().getMaps();
        if (!maps[0].equals("No maps on server was founded.")) {
            mapBox = new JComboBox(maps);
        }

        // creating top line for gameName Field
        Box topBox = Box.createVerticalBox();
        Box topBoxContent = Box.createHorizontalBox();
        JLabel nameLabel = new JLabel("GameName");
        nameLabel.setPreferredSize(new Dimension(LABEL_SIZE, LINE_H));
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gameNameTF.setPreferredSize(new Dimension(width/3, LINE_H));
        gameNameTF.setText("MyGame");
        topBoxContent.add(nameLabel);
        topBoxContent.add(gameNameTF);
        topBoxContent.add(Box.createHorizontalGlue());
        topBoxContent.setPreferredSize(new Dimension(width-30, LINE_H));
        topBox.add(Box.createVerticalStrut(15));
        topBox.add(topBoxContent);

        // creating central line for MaxPlayers Field
        Box centralBox = Box.createHorizontalBox();
        JLabel maxPlLabel = new JLabel("MaxPlayers");
        maxPlLabel.setPreferredSize(new Dimension(LABEL_SIZE, LINE_H));
        maxPlLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        centralBox.add(maxPlLabel);
        SpinnerModel spModel = new SpinnerNumberModel(Constants.MAX_PLAYERS
                ,1,Constants.MAX_PLAYERS,1);
        playersSpinner = new JSpinner(spModel);
        centralBox.add(playersSpinner);
        // how do this??? standartization sizes???
        for (int i = 0; i < 8; i++) {
            centralBox.add(Box.createHorizontalGlue());
        }
        centralBox.setPreferredSize(new Dimension(width-30, LINE_H));

        // creating bottom line for Map-Select Field
        Box bottomBox = Box.createVerticalBox();
        Box bottomBoxContent = Box.createHorizontalBox();
        JLabel mapLabel = new JLabel("Map");
        mapLabel.setPreferredSize(new Dimension(LABEL_SIZE, LINE_H));
        mapLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomBoxContent.add(mapLabel);
        bottomBoxContent.add(mapBox);
        for (int i = 0; i < 4; i++) {
            bottomBoxContent.add(Box.createHorizontalGlue());
        }
        bottomBoxContent.setPreferredSize(new Dimension(width-30, LINE_H));
        bottomBox.add(bottomBoxContent);
        bottomBox.add(Box.createVerticalStrut(15));

        Container c = this.getContentPane();
        c.setLayout(new FlowLayout());
        c.add(topBox);
        c.add(centralBox);
        c.add(bottomBox);
        c.add(createJButton);
        c.add(cancelJButton);
        createJButton.setAction(new CreateGameAction(this));
        cancelJButton.setAction(new CancelAction(this));

        setResizable(false);
        setVisible(true);
    }
    private String getGameName() {
        return gameNameTF.getText();
    }
    private int getMaxPlayers() {
        return (int)((Integer)playersSpinner.getValue());
    }
    private String getMap() {
        return (String)mapBox.getSelectedItem();
    }
    private class CreateGameAction extends AbstractAction {
        CreatingGameJDialog myParent;
        public CreateGameAction(CreatingGameJDialog jDialog) {
            myParent = jDialog;
            putValue(NAME, "Create");
            putValue(SHORT_DESCRIPTION, "Create game with selected arguments.");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            IConnector con = Connector.getInstance();
            try {
                String mapName = myParent.getMap();
                mapName = mapName.substring(0, mapName.indexOf('.'));
                con.createGame(myParent.getGameName(), mapName, myParent.getMaxPlayers());
                // !!!!! is it safe method to know gameNumber???    !!!!!!!!!!!!
                List<String> games = Connector.getInstance().takeGamesList();
                String[] buf = games.get(games.size()-1).split(" ");
                int gameNumber = Integer.parseInt(buf[0]);
                int players = Integer.parseInt(buf[buf.length-1]);
                myParent.dispose();
                parent.goNext();
            }/*catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(myParent,"Can not create new game.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }*/ catch (NetException ex2) {
                JOptionPane.showMessageDialog(myParent,"Connection was lost.\n"
                    + ex2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                myParent.dispose();
                StartJFrame jframe = new StartJFrame();
            }
        }
    }
    public static class CancelAction extends AbstractAction{
        CreatingGameJDialog parent;
        public CancelAction(CreatingGameJDialog jframe){
            parent = jframe;
            putValue(NAME, "Cancel");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            parent.dispose();
        }
    }
}
