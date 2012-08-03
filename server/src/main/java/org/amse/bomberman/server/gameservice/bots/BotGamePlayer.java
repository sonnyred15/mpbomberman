package org.amse.bomberman.server.gameservice.bots;

import org.amse.bomberman.server.gameservice.GamePlayer;

/**
 *
 * @author Kirilchuk V.E.
 */
public class BotGamePlayer implements GamePlayer {
    private int id = -1;
    private String nickName = "bot";

    @Override
    public void setPlayerId(int id) {
        this.id = id;
    }

    @Override
    public int getPlayerId() {
        return this.id;
    }

    @Override
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String getNickName() {
        return this.nickName;
    }

}
