
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.imodel;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.util.Constants;

/**
 *
 * @author Kirilchuk V.E
 */
public enum Bonus {
    LIFE {
        @Override
        public void applyBy(Player player) {
            player.lives += 1;
        }

        @Override
        public int getID() {
            return Constants.MAP_BONUS_LIFE;
        }
    },
    BOMB {
        @Override
        public void applyBy(Player player) {
            player.maxBombs += 1;
        }

        @Override
        public int getID() {
            return Constants.MAP_BONUS_BOMB_COUNT;
        }
    },
    RADIUS {
        @Override
        public void applyBy(Player player) {
            player.explRadius += 1;
        }

        @Override
        public int getID() {
            return Constants.MAP_BONUS_BOMB_RADIUS;
        }
    };

    public abstract void applyBy(Player player);
    public abstract int getID();

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
                throw new AssertionError("Illegal bonus id: " + id);
            }
        }
    };
}
