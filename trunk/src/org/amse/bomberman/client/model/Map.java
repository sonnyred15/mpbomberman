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
public class Map {
    public static final int DOWN = 0;
    public static final int LEFT = 1;
    public static final int UP = 2;
    public static final int RIGHT = 3;
    public static final int BOMB = -16;
    public static final int BOMB_PROOF_WALL = -8;
    public static final int EMPTY = 0;
    public static final int MAX_PLAYERS = 15;
    private int size;
    private int[][] cells;
    private List<Player> players;
    //perhaps waste
    public Map (int size) {
        this.size = size;
        cells = new int[size][size];
    }

    public Map(String fileName) throws FileNotFoundException, UnsupportedOperationException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            int buf = Integer.parseInt(br.readLine());
            // max Size?
            if (buf < 500 && buf > 0) {
                size = buf;
            } else {
                throw new UnsupportedOperationException("Incorrect size of Map." +
                    " Please Check this");
            }
            cells = new int[size][size];
            players = new ArrayList<Player>();
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
    public int getValue (int x, int y) {
        return cells[x][y];
    }
    // perhaps is waste
    public int[][] getMassive() {
        return cells;
    }
    // perhaps waste
    public void setCell(int x, int y, int value) {
        cells[x][y] = value;
    }
    public void addPlayer(Player player, int x, int y) throws UnsupportedOperationException {
        if (cells[x][y] == Map.EMPTY ) {
            if (players.size() < Map.MAX_PLAYERS) {
                players.add(player);
                cells[x][y] = players.size();
            } else {
                throw new UnsupportedOperationException("Too many players.");
            }
        } else {
            throw new UnsupportedOperationException("Cell is filled already.");
        }
    }
    // perhaps Player or PlayerName in stead of playerNumber
    public boolean movePlayer(int playerNumber, int direction) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j] == playerNumber) {
                    try {
                        Cell nextCell = nextCell(new Cell(i,j),direction);
                        if (canGoToCell(nextCell)) {
                            cells[i][j] = Map.EMPTY;
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
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(cells[i][j]+ " ");
            }
            sb.append("\n");
        }
        return sb.toString();

    }
    public void writeToFile(String fileName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        try {
            bw.write(size + "");
            bw.newLine();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
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
    private class Cell {
        int myX;
        int myY;
        public Cell (int x, int y) throws UnsupportedOperationException{
            if (x < 0 || x > size - 1 || y < 0 || y > size - 1) {
                throw new UnsupportedOperationException("Out of Map range.");
            } else {
                myX = x;
                myY = y;
            }
        }
    }
    private void goToCell(Cell cell, int playerNumber) {
        if (cells[cell.myX][cell.myY] < Map.BOMB_PROOF_WALL) {
            // different bonuses!!!
            players.get(playerNumber).incBomb();
        }
        cells[cell.myX][cell.myY] = playerNumber;
    }
    private Cell nextCell(Cell cell, int direction) throws UnsupportedOperationException {
        switch (direction) {
            case Map.LEFT: {
                return new Cell(cell.myX, cell.myY-1);
            }
            case Map.RIGHT: {
                return new Cell(cell.myX, cell.myY+1);
            }
            case Map.UP: {
                return new Cell(cell.myX-1, cell.myY);
            }
            case Map.DOWN: {
                return new Cell(cell.myX+1, cell.myY);
            }
            default: throw new UnsupportedOperationException("Out of Map range");
        }
    }
    private boolean canGoToCell(Cell cell) {
        return (cells[cell.myX][cell.myY] == Map.EMPTY
                || cells[cell.myX][cell.myY] < Map.BOMB_PROOF_WALL);
    }
}
