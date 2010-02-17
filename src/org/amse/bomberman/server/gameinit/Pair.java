/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

/**
 *
 * @author Kirilchuck V.E.
 */
public class Pair {

    private int x;
    private int y;

    public Pair() {
        this.x = 0;
        this.y = 0;
    }

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair pair = (Pair) obj;
        return ((this.x == pair.x) && (this.y == pair.y));
    }

    @Override
    public String toString() {
        return "x=" + x + " y=" + y;
    }
}
