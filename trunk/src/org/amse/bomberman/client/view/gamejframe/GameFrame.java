package org.amse.bomberman.client.view.gamejframe;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.*;
import java.util.List;
import org.amse.bomberman.client.Main;
import org.amse.bomberman.client.models.gamemodel.impl.GameMapModel;
import org.amse.bomberman.client.models.gamemodel.impl.PlayerModel;
import org.amse.bomberman.client.view.ResultsTable;
import org.amse.bomberman.util.ImageUtilities;

/**
 *
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
@SuppressWarnings("serial")
public class GameFrame extends JFrame implements ComponentListener {

    private GamePanel    gamePanel;
    private BonusLabel   livesLabel;
    private BonusLabel   bombsLabel;
    private BonusLabel   radiusLabel;
    private ResultsTable resultsTable;
    private JTextArea    infoTA;
    
    private int width  = 800;
    private int height = 600;

    private final int infoTextWidth = 250;

    //TODO CLIENT move this to ImageFactory
    private static final String LIFE_ICON_PATH
            = "org/amse/bomberman/client/icons/heart-48.png";

    private static final String B_RADIUS_ICON_PATH
            = "org/amse/bomberman/client/icons/b_radius-48.png";

    private static final String B_COUNT_ICON_PATH
            = "org/amse/bomberman/client/icons/b_count-48.png";

    private static final ImageIcon ICON_BONUS_LIFE
            = new ImageIcon(Main.class.getClassLoader()
            .getResource(LIFE_ICON_PATH));

    private static final ImageIcon ICON_BONUS_B_RADIUS
            = new ImageIcon(Main.class.getClassLoader()
            .getResource(B_RADIUS_ICON_PATH));

    private static final ImageIcon ICON_BONUS_B_COUNT
            = new ImageIcon(Main.class.getClassLoader()
            .getResource(B_COUNT_ICON_PATH));

    public GameFrame() {
        super("BomberMan");

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        initComponents();
        gamePanel.addComponentListener(this);
        setResizable(true);
    }

    private void initComponents() {
        setSize(width, height);

        gamePanel    = new GamePanel();
        livesLabel   = new BonusLabel(ICON_BONUS_LIFE, 0);
        bombsLabel   = new BonusLabel(ICON_BONUS_B_COUNT, 0);
        radiusLabel  = new BonusLabel(ICON_BONUS_B_RADIUS, 0);
        resultsTable = new ResultsTable();

        /* BONUS BOX */
        Box bonusBox = Box.createHorizontalBox();
        bonusBox.add(Box.createHorizontalGlue());
        bonusBox.add(livesLabel);
        bonusBox.add(Box.createHorizontalStrut(15));
        bonusBox.add(bombsLabel);
        bonusBox.add(Box.createHorizontalStrut(15));
        bonusBox.add(radiusLabel);
        bonusBox.add(Box.createHorizontalGlue());

        /* INFO STUFF */
        JScrollPane jsp = new JScrollPane(resultsTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setPreferredSize(new Dimension(infoTextWidth, height / 3));

        JScrollPane jsp2 = new JScrollPane(createInfoTA(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp2.setPreferredSize(new Dimension(infoTextWidth, height / 3));
        
        /* Adding all to content pane.. */
        Container content = getContentPane();
        content.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        content.add(bonusBox, c);

        c.gridx = 1;
        c.gridy = 0;
        content.add(Box.createGlue(), c);

        c.fill = GridBagConstraints.VERTICAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 2;//gamePanel <-> results cell and chat cell
        content.add(gamePanel, c);//[0;1]

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;
        content.add(jsp, c);

        c.gridx = 1;
        c.gridy = 2;
        content.add(jsp2, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 2;
        content.add(Box.createGlue(), c);
    }

    public void setResults(final List<String> results) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                resultsTable.setResults(results);
            }
        });
    }

    public void setBonuses(final int lives,final  int bombs,final int radius) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                livesLabel.setValue(lives);
                bombsLabel.setValue(bombs);
                radiusLabel.setValue(radius);
            }
        });
    }

    public void setGameMap(GameMapModel model) {
        gamePanel.updateGameMap(model);//will automatically use EDT
    }

    public void setPlayerInfo(PlayerModel model) {
        gamePanel.updatePlayer(model);//will automatically use EDT
    }

    public void setHistory(List<String> history) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            if (history.size() - i < 4) {
                sb.append("-");
                sb.append(history.get(i));
                sb.append("\n");
            }
        }
        infoTA.setText(sb.toString());
    }

    public void reset() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                resultsTable.clearResults();
                infoTA.setText("");
                gamePanel.reset();
            }
        });
    }

    private JTextArea createInfoTA() {
        infoTA = new JTextArea();
        infoTA.setFocusable(false);
        infoTA.setEditable(false);
        infoTA.setLineWrap(true);
        infoTA.setForeground(Color.RED);
        infoTA.setFont(new Font(Font.MONOSPACED, Font.BOLD + Font.ITALIC, 12));
        return infoTA;
    }

    public void componentResized(ComponentEvent e) {
        pack();
    }

    public void componentMoved(ComponentEvent e) {
        //ignore
    }

    public void componentShown(ComponentEvent e) {
        //ignore
    }

    public void componentHidden(ComponentEvent e) {
        //ignore
    }

    @SuppressWarnings("serial")
    private class BonusLabel extends JLabel {

        private ImageIcon image;
        private int value;
        private final int size = 32;

        private BonusLabel(ImageIcon icon, int firstCount) {
            image = new ImageIcon(ImageUtilities
                    .rescaleImage(icon.getImage(), size, size));

            this.setIcon(image);
            this.setText("x" + firstCount);
            this.setVisible(true);
        }

        private void setValue(int value) {
            if (this.value != value) {
                this.value = value;
                this.setText("x" + value);
            }
        }

        private int getValue() {
            return value;
        }
    }
}
