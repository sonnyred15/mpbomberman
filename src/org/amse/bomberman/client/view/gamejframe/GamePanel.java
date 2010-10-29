package org.amse.bomberman.client.view.gamejframe;

import org.amse.bomberman.client.models.gamemodel.GameMap;
import org.amse.bomberman.client.models.gamemodel.Cell;
import org.amse.bomberman.client.models.gamemodel.impl.GameMapModel;
import org.amse.bomberman.util.Constants;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.amse.bomberman.client.models.gamemodel.impl.PlayerModel;
import org.amse.bomberman.client.view.ImageFactory;

/**
 *
 * @author Mikhail Korovkin
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel {
    private final ImageFactory images = new ImageFactory();

    private GameMap    gameMap = null;
    private List<Cell> changes = new ArrayList<Cell>();

    public static final int CELL_SIZE = 48;
    // amount of cells at the one line on the Screen
    private int range;
    public static final int DEFAULT_RANGE = 10;

    private int height;
    private int width;
    // amount of cells at one line in the Full map
    private int size;
    // Cell that is the most Left and Up at the screen
    private Cell LUCell = new Cell(0, 0);
    // Cell that is the most Right and Down at the screen
    private Cell RDCell = new Cell(DEFAULT_RANGE-1, DEFAULT_RANGE-1);
    private Cell myCoord = new Cell(0, 0);

    private final int step = 4;

    private BufferedImage buffer
            = new BufferedImage(CELL_SIZE, CELL_SIZE, BufferedImage.TYPE_INT_ARGB);

    private static final Color EMPTY_COLOR   = Color.LIGHT_GRAY;
    private static final Color PL_EXPL_COLOR = Color.RED;

    public void updateGameMap(final GameMapModel model) {
        if(!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    updateGameMap(model);
                }
            });
            return;
        }

        GameMap newGameMap = model.getGameMap();

        // if this is first invocation after receiving of gameMap
        if (gameMap == null) {
            size = newGameMap.getSize();
            if (size < DEFAULT_RANGE) {
                range = size;
                RDCell = new Cell(size-1, size-1);
            } else {
                range = DEFAULT_RANGE;
            }
            width = range * CELL_SIZE;
            height = range * CELL_SIZE;
            setPreferredSize(new Dimension(width, height));
        }
        gameMap = newGameMap;
        changes = model.getChanges();
        repaint();
    }

    public void updatePlayer(final PlayerModel model) {
        if(!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    updatePlayer(model);
                }
            });
            return;
        }

        myCoord = model.getPlayer().getCoord();
        int x = myCoord.getX();
        int y = myCoord.getY();
        // bad hack with scroll sizes
        if ((x - step < LUCell.getX() && LUCell.getX() > 0)
            || (x+1+step > RDCell.getX() && RDCell.getX() < size-1)
            || (y-step < LUCell.getY() && LUCell.getY() > 0)
            || (y+1+step > RDCell.getY() && RDCell.getY() < size-1))  {
            findEyeShot();
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) {        
        if (gameMap == null) {//if we have no gameMap
            return;
        }
        List<Cell> expl = gameMap.getExplosions();
        for (int i = LUCell.getX(); i <= RDCell.getX(); i++) {
            for (int j = LUCell.getY(); j <= RDCell.getY(); j++) {
                Cell c = parseToMyCoord(new Cell(i, j));
                graphics.drawImage(drawNotExpl(buffer, new Cell(i, j)),
                        c.getY() * CELL_SIZE, c.getX() * CELL_SIZE, this);
            }
        }
        for (Cell cell : expl) {
            Cell myCell = parseToMyCoord(cell);
            graphics.drawImage(drawExplosion(buffer, cell),
                    CELL_SIZE * myCell.getY(), CELL_SIZE * myCell.getX(), this);
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
        int y = realCell.getY() - LUCell.getY();
        result = new Cell(x,y);
        return result;
    }

    private void findEyeShot() {
        if (range >= size) {//if we can see more then gameMapSize - do nothing.
            LUCell = new Cell(0,0);
            RDCell = new Cell(size-1, size-1);
        } else {
            int x1;
            int x2;
            int y1;
            int y2;
            // if player is on the left border of map
            if (myCoord.getX() <= (range-1)/2) {
                x1 = 0;
            } else {
                // if player is on the right border of map
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

    private BufferedImage drawExplosion(BufferedImage image, Cell cell) {
        Image result = null;
        Graphics2D g = image.createGraphics();
        g.setBackground(EMPTY_COLOR);
        g.clearRect(0, 0, CELL_SIZE, CELL_SIZE);
        
        int cellId = gameMap.getValue(cell);
        Image icon = images.getImage(cellId);

        if (cellId < Constants.MAP_EMPTY && cellId
                >= Constants.MAP_PROOF_WALL) { // if it is wall
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 10 * 0.1f));
            g.drawImage(icon, 0, 0, this);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 9 * 0.1f));
            g.drawImage(images.getImage(Constants.MAP_EXPLOSION), 0, 0, this);
        } else if (cellId > Constants.MAP_EMPTY && cellId
                <= Constants.MAX_PLAYERS) { // if it is player
            result = images.getImage(cellId);
            g.setBackground(PL_EXPL_COLOR);
            g.clearRect(0, 0, CELL_SIZE, CELL_SIZE);
            g.drawImage(result, 0, 0, this);
        } else { // others
            result = images.getImage(Constants.MAP_EXPLOSION);
            g.drawImage(result, 0, 0, this);
        }

        return image;
    }

    private BufferedImage drawNotExpl(BufferedImage image, Cell cell) {
        Graphics2D g = image.createGraphics();
        g.setBackground(EMPTY_COLOR);
        g.clearRect(0, 0, CELL_SIZE, CELL_SIZE);

        int cellId = gameMap.getValue(cell);
        if (cellId >= Constants.MAP_BOMB + 100 + 1) {// if it is player on the bomb
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 10 * 0.1f));
            int playerNum = gameMap.getValue(cell) - 100 - Constants.MAP_BOMB;
            g.drawImage(images.getImage(playerNum), 0, 0, this);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 9 * 0.1f));
            g.drawImage(images.getImage(Constants.MAP_BOMB), 0, 0, this);
        } else {
            Image icon = images.getImage(cellId);
            if (icon != null) {
                g.drawImage(icon, 0, 0, this);
            }
        }

        return image;
    }

    public void reset() {
        gameMap = null;
        range = DEFAULT_RANGE;
        LUCell = new Cell(0, 0);
        RDCell = new Cell(DEFAULT_RANGE-1, DEFAULT_RANGE-1);
        myCoord = new Cell(0, 0);
        size = 0;
    }
}
