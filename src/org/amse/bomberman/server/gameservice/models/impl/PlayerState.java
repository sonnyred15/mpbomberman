package org.amse.bomberman.server.gameservice.models.impl;

import java.util.concurrent.TimeUnit;
import org.amse.bomberman.util.Constants;

enum PlayerState {

    NORMAL {

        @Override
        void bombed(ModelPlayer player) {
            int lives = player.changeLives(-1);
            decreaseBonuses(player);
            if (lives > 0) {
                makeImmortal(player);
            }
        }

        private void makeImmortal(final ModelPlayer player) {
            player.setState(PlayerState.IMMORTAL);
            player.getTimer().schedule(new Runnable() {

                @Override
                public void run() {
                    player.setState(PlayerState.NORMAL);
                }
            }, Constants.PLAYER_IMMORTALITY_TIME, TimeUnit.MILLISECONDS);
        }
    },
    IMMORTAL {

        @Override
        void bombed(ModelPlayer player) {
            ; //do nothing.
        }
    };

    abstract void bombed(ModelPlayer player);

    void decreaseBonuses(ModelPlayer player) {
        synchronized (player) {
            if (player.getRadius() > Constants.PLAYER_DEFAULT_BOMB_RADIUS) {
                player.changeRadius(-1);
            }

            if (player.getMaxBombs() > Constants.PLAYER_DEFAULT_MAX_BOMBS) {
                player.changeMaxBombs(-1);
            }
        }
    }
}
