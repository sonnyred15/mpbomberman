package org.amse.bomberman.client.models.gamemodel.impl;

import org.amse.bomberman.client.models.gamemodel.GameMap;
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
 * @author Michail Korovkin
 */
public class SimpleGameMap implements GameMap {

    private int[][] cells;
    private List<ImmutableCell> explosions;

    public SimpleGameMap(int size) {
        cells = new int[size][size];
        explosions = new ArrayList<ImmutableCell>();
    }

//    public GameMap(String fileName) throws FileNotFoundException,
//            UnsupportedOperationException, IOException {
//        BufferedReader br = new BufferedReader(new FileReader(fileName));
//        try {
//            int buf = Integer.parseInt(br.readLine());
//            int size;
//            // max Size?
//            if (buf < 500 && buf > 0) {
//                size = buf;
//            } else {
//                throw new UnsupportedOperationException("Incorrect size of Map." +
//                    " Please Check this");
//            }
//            cells = new int[size][size];
//            explosions = new ArrayList<Cell>();
//            for (int i = 0; i < size; i++) {
//                String[] numbers = br.readLine().split(" ");
//                if (numbers.length < size) {
//                    throw new UnsupportedOperationException("Incorrect map. Please check this.");
//                }
//                for (int j = 0; j < size; j++) {
//                    cells[i][j] = Integer.parseInt(numbers[j]);
//                }
//            }
//            br.close();
//        } catch (IOException ex) {
//            try {
//                br.close();
//            } catch (IOException e) {
//                System.out.println("Cann't close file. Please check this.");
//            }
//            throw ex;
//        }
//    }
    @Override
    public int getSize() {
        return cells.length;
    }

    @Override
    public int getValue(ImmutableCell cell) {
        if (checkCell(cell)) {
            return cells[cell.getX()][cell.getY()];
        } else {
            throw new UnsupportedOperationException("Asking cell is absent.");
        }
    }

    @Override
    public List<ImmutableCell> getExplosions() {
        return explosions;
    }

    @Override
    public void setCell(ImmutableCell cell, int value) {
        if (checkCell(cell)) {
            cells[cell.getX()][cell.getY()] = value;
        } else {
            throw new UnsupportedOperationException("You want set value of absent cell.");
        }
    }

    @Override
    public void setExplosions(List<ImmutableCell> expl) {
        for (ImmutableCell cell : expl) {
            if (!checkCell(cell)) {
                throw new UnsupportedOperationException("False cell in list of explosions.");
            }
        }
        explosions = expl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells.length; j++) {
                sb.append(cells[i][j] + " ");
            }
            sb.append("\n");
        }
        return sb.toString();

    }

//    public void writeToFile(String fileName) throws IOException {
//        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
//        try {
//            bw.write(cells.length + "");
//            bw.newLine();
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < cells.length; i++) {
//                for (int j = 0; j < cells.length; j++) {
//                    sb.append(cells[i][j] + " ");
//                }
//                bw.write(sb.toString());
//                bw.newLine();
//                sb.delete(0, sb.length());
//            }
//            bw.close();
//        } catch (IOException ex) {
//            try {
//                bw.close();
//            } catch (IOException e) {
//                System.out.println("Cann't close file. Please check this.");
//            }
//            throw ex;
//        }
//    }
//
//    @Override
//    public GameMap clone() {
//        GameMap res = new GameMap(this.getSize());
//        for (int i = 0; i < this.getSize(); i++) {
//            for (int j = 0; j < this.getSize(); j++) {
//                res.setCell(new Cell(i, j), this.getValue(new Cell(i, j)));
//            }
//        }
//        List<Cell> expl = new ArrayList<Cell>();
//        expl = this.getExplosions();
//        res.setExplosions((ArrayList<Cell>) expl);
//        return res;
//    }

    private boolean checkCell(ImmutableCell cell) {
        return (cell.getX() >= 0 && cell.getX() < cells.length
                && cell.getY() >= 0 && cell.getY() < cells.length);
    }
}
