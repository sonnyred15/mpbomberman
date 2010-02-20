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
    private final int range;
    private final int defaultRange = 10;
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
        if (size < defaultRange) {
            range = size;
        } else {
            range = defaultRange;
        }
        myCoord = Model.getInstance().getPlayerCoord();
        
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
        field.setLayout(new GridLayout(range,range,0,0));
        cells = new MyJPanel[range][range];
        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range; j++) {
                cells[i][j] = new MyJPanel(48);
                cells[i][j].setContent(map.getValue(new Cell(i+LUCell.getX(),j+LUCell.getY())));
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
                if (isInEyeShot(cell) && expl.contains(cell)) {
                    cells[cell.getX()-LUCell.getX()][cell.getY()-LUCell.getY()]
                            .checkExplosion(map.getValue(cell));
                } else {
                    if (isInEyeShot(cell)) {
                        cells[cell.getX()-LUCell.getX()][cell.getY()-LUCell.getY()]
                                .setContent(map.getValue(cell));
                    }
                }
                // why it isn't need???
                //cells[cell.getX()][cell.getY()].repaint();
            }
        }
    }
    public void tryScroll(Direction direct) {
        /*if (range >= size) {
            return;
        }
        BombMap map = Model.getInstance().getMap();
        switch(direct) {
            case RIGHT: {
                if (RDCell.getY() < size - 1) {
                    for (int j = 0; j < range - 1; j++) {
                        for (int i = 0; i < range; i++) {
                            cells[i][j] = cells[i][j + 1];
                            cells[i][j].repaint();
                        }
                    }
                    for (int i = 0; i < range; i++) {
                        cells[i][range - 1].setContent(map.getValue(new Cell
                                (i + LUCell.getX(), range + LUCell.getY())));
                        cells[i][range-1].repaint();
                    }
                    LUCell = LUCell.nextCell(direct);
                    RDCell = RDCell.nextCell(direct);
                    break;
                }
            }
            case DOWN: {

                break;
            }
            case LEFT: {

                break;
            }
            case UP: {

                break;
            }
            default: {
                throw new UnsupportedOperationException("Incorrect value of direction:"
                        + direct.toString());
            }
        }*/
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
            int x1;
            int x2;
            int y1;
            int y2;
            if (myCoord.getX() <= (range-1)/2) {
                x1 = 0;
            } else {
                x1 = myCoord.getX() - (range-1)/2;
            }
            if (myCoord.getY() <= (range-1)/2) {
                y1 = 0;
            } else {
                y1 = myCoord.getY() - (range-1)/2;
            }
            x2 = x1 + range - 1;
            y2 = y1 + range - 1;
            LUCell = new Cell(x1,y1);
            RDCell = new Cell(x2, y2);
        }
    }
    private boolean isInEyeShot(Cell cell) {
        return ((cell.getX() >= LUCell.getX()) && (cell.getX() <= RDCell.getX())
                && (cell.getY() >= LUCell.getY()) && (cell.getY() <= RDCell.getY()));
    }
}
