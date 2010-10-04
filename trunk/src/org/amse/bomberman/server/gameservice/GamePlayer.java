package org.amse.bomberman.server.gameservice;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface GamePlayer {

    void setPlayerId(int id);

    int getPlayerId();
    
    void setNickName(String nickName);

    String getNickName();
}
