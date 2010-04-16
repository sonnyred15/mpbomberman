package org.amse.bomberman.client.view.mywizard;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.IController;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Michail Korovkin
 */
public class CreatingGameJPanel extends JPanel {
    private MyWizard parent;
    private int width = 200;
    private int heigth = 200;
    private JComboBox mapBox = new JComboBox();;
    private JTextField gameNameTF = new JTextField();
    private JSpinner playersSpinner;
    private JButton createJButton = new JButton();
    private final int LINE_H = 20;
    private final int LABEL_SIZE = width/3+10;

    public CreatingGameJPanel(MyWizard wizard){
        parent = wizard;
        this.setPreferredSize(new Dimension(width, heigth));

        // creating top line for gameName Field
        Box topBox = Box.createHorizontalBox();
        JLabel nameLabel = new JLabel("GameName");
        nameLabel.setPreferredSize(new Dimension(LABEL_SIZE, LINE_H));
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gameNameTF.setPreferredSize(new Dimension(width/3, LINE_H));
        gameNameTF.setText("MyGame");
        topBox.add(nameLabel);
        topBox.add(Box.createHorizontalStrut(10));
        topBox.add(gameNameTF);
        topBox.setPreferredSize(new Dimension(width-30, LINE_H));

        // creating central line for MaxPlayers Field
        Box centralBox = Box.createHorizontalBox();
        JLabel maxPlLabel = new JLabel("MaxPlayers");
        maxPlLabel.setPreferredSize(new Dimension(LABEL_SIZE, LINE_H));
        maxPlLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        centralBox.add(maxPlLabel);
        SpinnerModel spModel = new SpinnerNumberModel(Constants.MAX_PLAYERS
                ,1,Constants.MAX_PLAYERS,1);
        playersSpinner = new JSpinner(spModel);
        centralBox.add(Box.createHorizontalStrut(10));
        centralBox.add(playersSpinner);
        centralBox.setPreferredSize(new Dimension(width-30, LINE_H));

        // creating bottom line for Map-Select Field
        Box bottomBox = Box.createHorizontalBox();
        JLabel mapLabel = new JLabel("Map");
        mapLabel.setPreferredSize(new Dimension(LABEL_SIZE, LINE_H));
        mapLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomBox.add(mapLabel);
        bottomBox.add(Box.createHorizontalStrut(10));
        bottomBox.add(mapBox);
        for (int i = 0; i < 4; i++) {
            bottomBox.add(Box.createHorizontalGlue());
        }
        bottomBox.setPreferredSize(new Dimension(width-30, LINE_H));

        this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        this.add(topBox);
        this.add(centralBox);
        this.add(bottomBox);
        this.add(createJButton);
        createJButton.setAction(new CreateGameAction());
        setVisible(true);
    }
    public void setMaps(List<String> maps) {
        this.mapBox.removeAllItems();
        for (int i = 0; i < maps.size(); i++) {
            this.mapBox.addItem(maps.get(i));
        }
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
        public CreateGameAction() {
            putValue(NAME, "Create");
            putValue(SHORT_DESCRIPTION, "Create game with selected arguments.");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            IController con = Controller.getInstance();
            try {
                String mapName = getMap();
                con.requestCreateGame(getGameName(), mapName, getMaxPlayers());
                parent.goNext();
                Panel33 nextPanel = (Panel33) parent.getCurrentJPanel();
                nextPanel.doBeforeShow();
            } catch (NetException ex) {
                JOptionPane.showMessageDialog(parent,"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                parent.setCurrentJPanel(0);
            }
        }
    }
}

