package org.amse.bomberman.client.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Timer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.Cell;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.view.MapJFrameUtil.CheckTimerTask;
import org.amse.bomberman.client.view.MapJFrameUtil.MyJPanel;
import org.amse.bomberman.util.*;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Michail Korovkin
 */
public class MapJFrame extends JFrame implements IView{
    private MyJPanel[][] cells;
    private JLabel livesJLabel;
    private Timer timer;
    // is really nead???
    private boolean dead = false;
    private MapJFrameListener listener = new MapJFrameListener(this);
    private final int height = 600;
    private final int width = 500;
    // amount of cells at the one line on the Screen
    private final int range = 10;
    // amount of cells at one line in the Full map
    private final int size;
    // Cell that is the most Left and Up at the screen
    private Cell LUCell;
    // Cell that is the most Right and Down at the screen
    private Cell RDCell;
    private Cell myCoord;
    
    public MapJFrame(BombMap map) {
        super("BomberMan");
        setSize(width, height);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocation(400, 100);
        setMinimumSize(new Dimension(width / 2, height / 2));
        size = map.getSize();
        //myCoord = map.
        
        Container c = getContentPane();
        c.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        livesJLabel = new JLabel("Lives: 0");
        // ???
        livesJLabel.setPreferredSize(new Dimension(width, 30));
        livesJLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.add(livesJLabel);
        this.findEyeShot();
        int num = map.getSize();
        JPanel field = new JPanel();
        field.setLayout(new GridLayout(num,num,0,0));
        cells = new MyJPanel[num][num];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                cells[i][j] = new MyJPanel(48);
                cells[i][j].setContent(map.getValue(new Cell(i,j)));
                field.add(cells[i][j]);
            }
        }
        c.add(field);
        List<Cell> changes = Model.getInstance().getChanges();
        //this.refresh(changes, map);
        this.addKeyListener(listener);
        this.setJMenuBar(new MapJMenuBar(this));

        checkStart();
        setResizable(true);
        setVisible(true);
    }

    public void stopWaitStart() {
        timer.cancel();
    }
    public void update() {
        IModel model = Model.getInstance();
        List<Cell> changes = model.getChanges();
        BombMap newMap = model.getMap();
        int lives = model.getPlayerLives();
        this.refresh(changes, newMap);
        this.refreshLives(lives);
        if (lives <= 0) {
            if (!dead) {
                JOptionPane.showMessageDialog(this, "You are dead!!!", "Death"
                        , JOptionPane.INFORMATION_MESSAGE);
                this.removeKeyListener(listener);
                dead = true;
            }
        }
    }
    private void refreshLives(int lives) {
        String buf = livesJLabel.getText();
        String beginS = buf.substring(0, buf.length() - 1);
        String result = beginS.concat("" + lives);
        livesJLabel.setText(result);
    }
    private void refresh(List<Cell> changes, BombMap map) {
        if (changes != null) {
            List<Cell> expl = map.getExplosions();
            for (Cell cell : changes) {
                if (expl.contains(cell)) {
                    cells[cell.getX()][cell.getY()].checkExplosion(map.getValue(cell));
                } else {
                    cells[cell.getX()][cell.getY()].setContent(map.getValue(cell));
                }
                // why it isn't need???
                //cells[cell.getX()][cell.getY()].repaint();
            }
        }
    }
    public void tryScroll(Direction direct) {
    }
    private void checkStart() {
        timer = new Timer();
        timer.schedule(new CheckTimerTask(this), (long)0,(long) Constants.GAME_STEP_TIME);
    }
    private void findEyeShot() {
        if (range >= size) {
            LUCell = new Cell(0,0);
            RDCell = new Cell(size-1, size-1);
        } else {
            //if ()
        }
    }
}
