package org.amse.bomberman.client.view.mapjframe;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.Cell;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.impl.Connector;
import org.amse.bomberman.client.net.impl.Connector.NetException;
import org.amse.bomberman.client.view.IView;
import org.amse.bomberman.client.view.StartJFrame;
import org.amse.bomberman.client.view.mapjframe.MapJFrameUtil.MyJPanel;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Michail Korovkin
 */
public class MapJFrame extends JFrame implements IView{
    private MyJPanel[][] cells;
    private JLabel livesJLabel;
    // is really nead???
    private boolean dead = false;
    private MapJFrameListener listener = new MapJFrameListener(this);
    private final int height = 600;
    private final int width = 500;
    // amount of cells at the one line on the Screen
    private int range;
    private final int defaultRange = 10;
    // amount of cells at one line in the Full map
    private int size;
    // Cell that is the most Left and Up at the screen
    private Cell LUCell;
    // Cell that is the most Right and Down at the screen
    private Cell RDCell;
    private Cell myCoord;
    private final int step = 2;
    
    public MapJFrame() {
        super("BomberMan");
        setSize(width, height);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocation(400, 100);
        setMinimumSize(new Dimension(width / 2, height / 2));
        BombMap map;
        try {
            map = Connector.getInstance().getMap();
            size = map.getSize();
            if (size < defaultRange) {
                range = size;
            } else {
                range = defaultRange;
            }
            myCoord = Model.getInstance().getPlayerCoord();

            Container c = getContentPane();
            c.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            livesJLabel = new JLabel("Lives: 0");
            // ???
            livesJLabel.setPreferredSize(new Dimension(width, 30));
            livesJLabel.setHorizontalAlignment(SwingConstants.CENTER);
            c.add(livesJLabel);
            this.findEyeShot();
            int num = map.getSize();
            JPanel field = new JPanel();
            field.setLayout(new GridLayout(range, range, 0, 0));
            cells = new MyJPanel[range][range];
            for (int i = 0; i < range; i++) {
                for (int j = 0; j < range; j++) {
                    cells[i][j] = new MyJPanel(48);
                    cells[i][j].setContent(map.getValue(parseToRealCoord(new Cell(i,j))));
                    field.add(cells[i][j]);
                }
            }
            c.add(field);
            this.addKeyListener(listener);
            this.setJMenuBar(new MapJMenuBar(this));

            setResizable(true);
            setVisible(true);
        } catch (NetException ex) {
             JOptionPane.showMessageDialog(this,"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             StartJFrame jframe = new StartJFrame();
        }
    }

    public void update() {
        IModel model = Model.getInstance();
        myCoord = model.getPlayerCoord();
        int x = myCoord.getX();
        int y = myCoord.getY();
        BombMap newMap = model.getMap();
        if ((x - step < LUCell.getX() && LUCell.getX() > 0)
            || (x+step > RDCell.getX() && RDCell.getX() < size-1)
            || (y-step < LUCell.getY() && LUCell.getY() > 0)
            || (y+step > RDCell.getY() && RDCell.getY() < size-1))  {
            this.findEyeShot();
            for (int i = 0; i < range; i++) {
                for (int j = 0; j < range; j++) {
                    cells[i][j].setContent(newMap.getValue(parseToRealCoord
                            (new Cell(i,j))));
                }
            }
        } else {
            List<Cell> changes = model.getChanges();
            this.refresh(changes, newMap);
        }
        int lives = model.getPlayerLives();
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
    private Cell parseToRealCoord(Cell myCell) {
        Cell result;
        int x = myCell.getX() + LUCell.getX();
        int y = myCell.getY() + LUCell.getY();
        result = new Cell(x,y);
        return result;
    }
    private Cell parseToMyCoord(Cell realCell) {
        Cell result;
        int x = realCell.getX() - LUCell.getX();
        int y = realCell.getX() - LUCell.getY();
        result = new Cell(x,y);
        return result;
    }
    /*public void tryScroll(Direction direct) {
        if (range >= size) {
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
        }
    }*/
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
                if (myCoord.getX() >=(size-1)-(range-1)/2) {
                    x1 = size - range;
                } else {
                    x1 = myCoord.getX() - (range-1)/2;
                }
            }
            if (myCoord.getY() <= (range-1)/2) {
                y1 = 0;
            } else {
                if (myCoord.getY() >= (size-1)-(range-1)/2) {
                    y1 = size - range;
                } else {
                    y1 = myCoord.getY() - (range-1)/2;
                }
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
