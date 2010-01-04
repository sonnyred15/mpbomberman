package org.amse.bomberman.client.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author michail korovkin
 */
public class BombMap {
    private int[][] cells;
    private List<Cell> explosions;
    
    public BombMap (int size) {
        cells = new int[size][size];
        explosions = new ArrayList<Cell>();
    }

    public BombMap(String fileName) throws FileNotFoundException, UnsupportedOperationException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            int buf = Integer.parseInt(br.readLine());
            int size;
            // max Size?
            if (buf < 500 && buf > 0) {
                size = buf;
            } else {
                throw new UnsupportedOperationException("Incorrect size of Map." +
                    " Please Check this");
            }
            cells = new int[size][size];
            explosions = new ArrayList<Cell>();
            for (int i = 0; i < size; i++) {
                String[] numbers = br.readLine().split(" ");
                if (numbers.length < size) {
                    throw new UnsupportedOperationException("Incorrect map. Please check this.");
                }
                for (int j = 0; j < size; j++) {
                    cells[i][j] = Integer.parseInt(numbers[j]);
                }
            }
            br.close();
        } catch (IOException ex) {
            try {
                br.close();
            } catch (IOException e) {
                System.out.println("Cann't close file. Please check this.");
            }
            throw ex;
        }
    }
    public int getSize() {
        return cells.length;
    }
    public int getValue (Cell cell) {
        if (checkCell(cell)) {
            return cells[cell.getX()][cell.getY()];
        } else throw new UnsupportedOperationException("Asking cell is absent.");
    }
    public List<Cell> getExplosions() {
        return explosions;
    }
    public void setCell(Cell cell, int value) {
        if (checkCell(cell)) {
            cells[cell.getX()][cell.getY()] = value;
        } else throw new UnsupportedOperationException("You want set value of absent cell.");
    }
    public void setExplosions(List<Cell> expl) {
        for (Cell cell:expl) {
            if (!checkCell(cell)) {
                throw new UnsupportedOperationException("False cell in list of explosions.");
            }
        }
        explosions = expl;
    }
    /*public void addPlayer(Player player, int x, int y) throws UnsupportedOperationException {
        if (cells[x][y] == BombMap.EMPTY ) {
            if (players.size() < BombMap.MAX_PLAYERS) {
                players.add(player);
                cells[x][y] = players.size();
            } else {
                throw new UnsupportedOperationException("Too many players.");
            }
        } else {
            throw new UnsupportedOperationException("Cell is filled already.");
        }
    }*/
    // perhaps Player or PlayerName in stead of playerNumber
    /*public boolean movePlayer(int playerNumber, Direction direction) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j] == playerNumber) {
                    try {
                        Cell nextCell = nextCell(new Cell(i,j),direction);
                        if (canGoToCell(nextCell)) {
                            cells[i][j] = BombMap.EMPTY;
                            goToCell(nextCell, playerNumber);
                            return true;
                        }
                    } catch (UnsupportedOperationException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        }
        return false;
    }*/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells.length; j++) {
                sb.append(cells[i][j]+ " ");
            }
            sb.append("\n");
        }
        return sb.toString();

    }
    public void writeToFile(String fileName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        try {
            bw.write(cells.length + "");
            bw.newLine();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells.length; j++) {
                    sb.append(cells[i][j] + " ");
                }
                bw.write(sb.toString());
                bw.newLine();
                sb.delete(0, sb.length());
            }
            bw.close();
        } catch (IOException ex) {
            try {
                bw.close();
            } catch (IOException e) {
                System.out.println("Cann't close file. Please check this.");
            }
            throw ex;
        }
    }
    @Override
    @SuppressWarnings("unchecked")
    public BombMap clone() {
        BombMap res = new BombMap(this.getSize());
        for (int i = 0; i < this.getSize(); i++) {
            for (int j = 0; j < this.getSize(); j++) {
                res.setCell(new Cell(i,j), this.getValue(new Cell(i,j)));
            }
        }
        List<Cell> expl = new ArrayList<Cell>();
        expl = this.getExplosions();
        res.setExplosions((ArrayList)expl);
        return res;
    }
    /*private void goToCell(Cell cell, int playerNumber) {
        if (cells[cell.myX][cell.myY] < BombMap.BOMB_PROOF_WALL) {
            // different bonuses!!!
            players.get(playerNumber).incBomb();
        }
        cells[cell.myX][cell.myY] = playerNumber;
    }
    
    private boolean canGoToCell(Cell cell) {
        return (cells[cell.getX()][cell.getY()] == BombMap.EMPTY
                || cells[cell.getX()][cell.getY()] < BombMap.BOMB_PROOF_WALL);
    }*/
    private boolean checkCell(Cell cell) {
        return (cell.getX() >= 0 && cell.getX() < cells.length
                && cell.getY() >= 0 && cell.getY() < cells.length);
    }
}
