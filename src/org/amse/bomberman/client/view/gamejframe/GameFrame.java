package org.amse.bomberman.client.view.gamejframe;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.amse.bomberman.client.Main;
import org.amse.bomberman.client.models.gamemodel.impl.GameModel;
import org.amse.bomberman.client.view.ResultsTable;
import org.amse.bomberman.util.ImageUtilities;

/**
 *
 * @author Mikhail Korovkin
 */
@SuppressWarnings("serial")
public class GameFrame extends JFrame {

    private GamePanel    gamePanel;
    private BonusLabel   livesLabel;
    private BonusLabel   bombsLabel;
    private BonusLabel   radiusLabel;
    private ResultsTable resultsTable;

    private JTextArea infoTA;
    
    private int width  = 800;
    private int height = 600;

    private final int infoTextWidth = 250;

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

        setDefaultCloseOperation(EXIT_ON_CLOSE);//TODO CLIENT Bad reaction =)
        setLocationRelativeTo(null);
        setResizable(true);
        initComponents();
    }

    public void stopGame() {
        livesLabel.setEnabled(false);
        bombsLabel.setEnabled(false);
        radiusLabel.setEnabled(false);
    }

    private void initComponents() {
        setSize(width, height);

        gamePanel    = new GamePanel();
        livesLabel   = new BonusLabel(ICON_BONUS_LIFE, 0);
        bombsLabel   = new BonusLabel(ICON_BONUS_B_COUNT, 0);
        radiusLabel  = new BonusLabel(ICON_BONUS_B_RADIUS, 0);
        resultsTable = new ResultsTable();

        Box bonusBox = Box.createHorizontalBox();
        bonusBox.add(livesLabel);
        bonusBox.add(Box.createHorizontalStrut(15));
        bonusBox.add(bombsLabel);
        bonusBox.add(Box.createHorizontalStrut(15));
        bonusBox.add(radiusLabel);

        Box rightBox = Box.createVerticalBox();
        rightBox.add(Box.createVerticalStrut(42)); // !!!

        JScrollPane jsp = new JScrollPane(resultsTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setPreferredSize(new Dimension(infoTextWidth, height / 3));
        rightBox.add(jsp);

        rightBox.add(Box.createVerticalGlue());

        JScrollPane jsp2 = new JScrollPane(createInfoTA(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp2.setPreferredSize(new Dimension(infoTextWidth, height / 3));
        rightBox.add(jsp2);

        Box leftBox = Box.createVerticalBox();
        leftBox.add(bonusBox);
        leftBox.add(Box.createVerticalStrut(10));
        leftBox.add(gamePanel);

        Box mainBox = Box.createHorizontalBox();
        mainBox.add(leftBox);
        mainBox.add(Box.createHorizontalStrut(10));
        mainBox.add(rightBox);

        Container c = getContentPane();
        c.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        c.add(mainBox);
    }

    public void setResults(List<String> results) {
        resultsTable.update(results);
    }

    public void setBonuses(int lives, int bombs, int radius) {
        if (livesLabel.getCount() != lives) {
            livesLabel.update(lives);
        }
        if (bombsLabel.getCount() != bombs) {
            bombsLabel.update(bombs);
        }
        if (radiusLabel.getCount() != radius) {
            radiusLabel.update(radius);
        }
    }

    public void setGameMap(GameModel model) {
        gamePanel.update(model);
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

    private JTextArea createInfoTA() {
        infoTA = new JTextArea();
        infoTA.setFocusable(false);
        infoTA.setEditable(false);
        infoTA.setLineWrap(true);
        infoTA.setForeground(Color.RED);
        infoTA.setFont(new Font(Font.MONOSPACED, Font.BOLD + Font.ITALIC, 12));
        return infoTA;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "GameJFrame",
                JOptionPane.ERROR_MESSAGE);
    }

    @SuppressWarnings("serial")
    private class BonusLabel extends JLabel {

        private ImageIcon image;
        private int count;
        private final int size = 32;

        private BonusLabel(ImageIcon icon, int firstCount) {
            image = new ImageIcon(ImageUtilities
                    .rescaleImage(icon.getImage(), size, size));

            this.setIcon(image);
            this.setText("x" + firstCount);
            this.setVisible(true);
        }

        private void update(int newCount) {
            if (newCount != count) {
                count = newCount;
                this.setText("x" + newCount);
            }
        }

        private int getCount() {
            return count;
        }
    }
}
