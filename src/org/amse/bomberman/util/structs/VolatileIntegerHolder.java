package org.amse.bomberman.util.structs;

/**
 * This class is NOT thread safe. Atomicity with modifying operations is
 * not guaranteed and must be provided by explicit synchronization.
 * However, if modification was synchronized then get operation always return
 * valid value and don`t need to be synchronized due to volatility of value.
 * @author Kirilchuk V.E.
 */
public class VolatileIntegerHolder {

    private volatile int value;

    public VolatileIntegerHolder(int value) {
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
