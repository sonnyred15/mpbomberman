package org.amse.bomberman.client.view.bomberwizard;

import org.amse.bomberman.util.Constants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

/**
 *
 * @author Mikhail Korovkin
 */
@SuppressWarnings("serial")
public class CreateGameJPanel extends JPanel{
    private final int width = 400;
    private final int heigth = 30;
    private Color foreground = Color.ORANGE;

    private JTextField gameNameTF = new JTextField();
    private JSpinner playersSpinner;
    private JComboBox mapBox = new JComboBox();

    private JLabel nameLabel;
    private JLabel maxPlLabel;
    private JLabel mapLabel;

    public CreateGameJPanel(){
        this.setPreferredSize(new Dimension(width, heigth));
        //this.setBorder(new LineBorder(Color.ORANGE, 1));

        // creating leftPanel for gameName Field
        Box leftBox = Box.createHorizontalBox();
        nameLabel = new JLabel("GameName");
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        nameLabel.setForeground(foreground);
        gameNameTF.setPreferredSize(new Dimension(width/4, this.heigth - 10));
        gameNameTF.setText("MyGame");
        leftBox.add(nameLabel);
        leftBox.add(Box.createHorizontalStrut(10));
        leftBox.add(gameNameTF);

        // creating centralPanel for MaxPlayers Field
        Box centralBox = Box.createHorizontalBox();
        maxPlLabel = new JLabel("MaxPlayers");
        maxPlLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        maxPlLabel.setForeground(foreground);
        SpinnerModel spModel = new SpinnerNumberModel(Constants.MAX_PLAYERS
                ,1,Constants.MAX_PLAYERS,1);
        playersSpinner = new JSpinner(spModel);
        centralBox.add(maxPlLabel);
        centralBox.add(Box.createHorizontalStrut(10));
        centralBox.add(playersSpinner);

        // creating rightPanel for Map-Select Field
        Box rightBox = Box.createHorizontalBox();
        mapLabel = new JLabel("Map");
        mapLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        mapLabel.setForeground(foreground);
        rightBox.add(mapLabel);
        rightBox.add(Box.createHorizontalStrut(10));
        rightBox.add(mapBox);

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        this.add(leftBox);
        this.add(centralBox);
        this.add(rightBox);
        this.setOpaque(false);
        setVisible(true);
    }
    public void setMaps(List<String> maps) {
        this.mapBox.removeAllItems();
        for (int i = 0; i < maps.size(); i++) {
            this.mapBox.addItem(maps.get(i));
        }
    }
    public String getGameName() {
        return gameNameTF.getText();
    }
    public int getMaxPlayers() {
        return (int)((Integer)playersSpinner.getValue());
    }
    public String getMap() {
        return (String)mapBox.getSelectedItem();
    }
    @Override
    public void setEnabled(boolean b) {
        this.gameNameTF.setEnabled(b);
        this.nameLabel.setEnabled(b);
        this.playersSpinner.setEnabled(b);
        this.maxPlLabel.setEnabled(b);
        this.mapBox.setEnabled(b);
        this.mapLabel.setEnabled(b);
    }
}
