
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.util;

/**
 * Class that represents pair of two integer values.
 * @author Kirilchuck V.E.
 */
public class Pair {
    private int x;
    private int y;

    /**
     * Default constructor. Creates pair with zero ints.
     */
    public Pair() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Main constructor of Pair with defined ints.
     * @param x first int.
     * @param y second int.
     */
    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the first int of Pair.
     * @param x int to set as first int of Pair.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the seconf int of Pair.
     * @param y int to set as second int of Pair.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Returns first int of Pair.
     * @return first int of Pair.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns second int of Pair.
     * @return second int of Pair.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Overrided equals method. Two Pair`s are equal either if they are the
     * same object, or they first and second ints are equal.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {    // equals by reference
            return true;
        }

        if (!(obj instanceof Pair)) {
            return false;
        }

        Pair pair = (Pair) obj;

        return ((this.x == pair.x) && (this.y == pair.y));
    }

    /**
     * Overrided hash code for Pair.
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 5;

        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;

        return hash;
    }

    /**
     * Overrided toString for Pair.
     * Returns Pair(a,b) as next string: "x=a y=b"
     * @return
     */
    @Override
    public String toString() {
        return "x=" + x + " y=" + y;
    }
}
