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
    public static final int BOMB = -16;
    public static final int EXPLODED_BOMB = -17;
    public static final int BOMB_BEAM = -18;
    public static final int BOMB_PROOF_WALL = -8;
    public static final int EMPTY = 0;
    public static final int MAX_PLAYERS = 15;
    private int size;
    private int[][] cells;
    private List<Player> players;
    //perhaps waste
    public BombMap (int size) {
        this.size = size;
        cells = new int[size][size];
    }

    public BombMap(String fileName) throws FileNotFoundException, UnsupportedOperationException, IOException {
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
    }
    // perhaps Player or PlayerName in stead of playerNumber
    public boolean movePlayer(int playerNumber, Direction direction) {
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
    public static enum Direction {
        DOWN,
        LEFT,
        UP,
        RIGHT;
        public int getInt() {
            switch(this.valueOf(this.toString())){
                case DOWN: return 0;
                case LEFT: return 1;
                case UP: return 2;
                case RIGHT: return 3;
            }
            return -1;
        }
        public static Direction getDirection(int x) throws UnsupportedOperationException{
            switch(x) {
                case 0: return DOWN;
                case 1: return LEFT;
                case 2: return UP;
                case 3: return RIGHT;
            }
            throw new UnsupportedOperationException("Wrong Integer value" +
                    " for Direction");
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
        if (cells[cell.myX][cell.myY] < BombMap.BOMB_PROOF_WALL) {
            // different bonuses!!!
            players.get(playerNumber).incBomb();
        }
        cells[cell.myX][cell.myY] = playerNumber;
    }
    private Cell nextCell(Cell cell, Direction direction)
            throws UnsupportedOperationException {
        switch (direction) {
            case LEFT: {
                return new Cell(cell.myX, cell.myY-1);
            }
            case RIGHT: {
                return new Cell(cell.myX, cell.myY+1);
            }
            case UP: {
                return new Cell(cell.myX-1, cell.myY);
            }
            case DOWN: {
                return new Cell(cell.myX+1, cell.myY);
            }
            default: throw new UnsupportedOperationException("Out of Map range");
        }
    }
    private boolean canGoToCell(Cell cell) {
        return (cells[cell.myX][cell.myY] == BombMap.EMPTY
                || cells[cell.myX][cell.myY] < BombMap.BOMB_PROOF_WALL);
    }
}
