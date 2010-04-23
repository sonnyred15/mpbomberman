package org.amse.bomberman.client.view.mywizard;

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
import javax.swing.border.LineBorder;
import org.amse.bomberman.util.Constants;
/**
 *
 * @author Michael Korovkin
 */
public class WCreatingGameJPanel extends JPanel{
    private int width = 200;
    private int heigth = 100;
    private Color foreground = Color.ORANGE;
    private JComboBox mapBox = new JComboBox();;
    private JTextField gameNameTF = new JTextField();
    private JSpinner playersSpinner;
    private final int LINE_H = 20;
    private final int LABEL_SIZE = width/3+10;

    public WCreatingGameJPanel(){
        this.setPreferredSize(new Dimension(width, heigth));
        this.setBorder(new LineBorder(Color.ORANGE, 1));

        // creating top line for gameName Field
        Box topBox = Box.createHorizontalBox();
        JLabel nameLabel = new JLabel("GameName");
        nameLabel.setPreferredSize(new Dimension(LABEL_SIZE, LINE_H));
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        nameLabel.setForeground(foreground);
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
        maxPlLabel.setForeground(foreground);
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
        mapLabel.setForeground(foreground);
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
}
