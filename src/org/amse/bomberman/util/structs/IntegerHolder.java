/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.util.structs;

/**
 *
 * @author Kirilchuk V.E.
 */
public class IntegerHolder {

    private volatile int value;

    public IntegerHolder(int value) {
        this.value = value;
    }

    public int changeValue(int delta) {
        value += delta;
        return value;
    }

    public int getValue() {
        return value;
    }

    public int increment() {
        ++value;
        return value;
    }

    public int decrement() {
        --value;
        return value;
    }
}
