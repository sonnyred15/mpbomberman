package org.amse.bomberman.server.gameservice.gamemap.objects.impl;

import java.util.Random;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayer;
import org.amse.bomberman.server.gameservice.models.impl.ModelPlayerVisitor;
import org.amse.bomberman.util.Constants;

/**
 *
 * @author Kirilchuk V.E
 */
public enum Bonus implements ModelPlayerVisitor {

    LIFE {

        @Override
        public void visit(ModelPlayer player) {
            player.changeLives(+1);
        }

        @Override
        public int getID() {
            return Constants.MAP_BONUS_LIFE;
        }
    },
    BOMB {

        @Override
        public void visit(ModelPlayer player) {
            player.changeMaxBombs(+1);
        }

        @Override
        public int getID() {
            return Constants.MAP_BONUS_BOMB_COUNT;
        }
    },
    RADIUS {

        @Override
        public void visit(ModelPlayer player) {
            player.changeRadius(+1);
        }

        @Override
        public int getID() {
            return Constants.MAP_BONUS_BOMB_RADIUS;
        }
    };

    public abstract int getID();

    public static boolean isBonus(int id) {
        if (id == Constants.MAP_BONUS_LIFE
                || id == Constants.MAP_BONUS_BOMB_COUNT
                || id == Constants.MAP_BONUS_BOMB_RADIUS) {
            return true;
        }

        return false;
    }
    private static Random generator = new Random();

    /**
     * Returns random bonus from availiable or null(as no bonus).
     * The probability for all bonuses is hardcoded in this method.
     * @return random Bonus or null(as no bonus).
     */
    public static Bonus randomBonus() { //TODO unhardcode!
        int random = generator.nextInt(100); //from 0 to 99

        if (random < 5) {
            return LIFE;
        }
        if (random < 10) {
            return BOMB;
        }
        if (random < 15) {
            return RADIUS;
        }

        return null;
    }

    public static Bonus valueOf(int id) {
        switch (id) {
            case Constants.MAP_BONUS_LIFE: {
                return LIFE;
            }

            case Constants.MAP_BONUS_BOMB_COUNT: {
                return BOMB;
            }

            case Constants.MAP_BONUS_BOMB_RADIUS: {
                return RADIUS;
            }

            default: {
                throw new IllegalArgumentException("Illegal bonus id: " + id);
            }
        }
    }

    ;
}
