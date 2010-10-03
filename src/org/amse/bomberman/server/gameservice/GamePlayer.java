/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
