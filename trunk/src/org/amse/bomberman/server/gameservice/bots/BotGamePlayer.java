/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameservice.bots;

import org.amse.bomberman.server.gameservice.GamePlayer;

/**
 *
 * @author Kirilchuk V.E.
 */
public class BotGamePlayer implements GamePlayer {
    private int id = -1;
    private String nickName = "bot";

    public void setPlayerId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return this.id;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return this.nickName;
    }

}
