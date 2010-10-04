package org.amse.bomberman.util;

public enum Direction {

    DOWN(0), LEFT(1), UP(2), RIGHT(3);
    private final int value;

    private Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static Direction fromInt(int direction) throws IllegalArgumentException {
        switch (direction) {
            case 0:
                return DOWN;
            case 1:
                return LEFT;
            case 2:
                return UP;
            case 3:
                return RIGHT;
            default:
                throw new IllegalArgumentException("Wrong argument " + "must be between 0 and 3 inclusive");
        }
    }
}
