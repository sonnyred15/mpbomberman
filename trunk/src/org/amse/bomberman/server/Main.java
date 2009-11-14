/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server;

import java.io.IOException;

import org.amse.bomberman.server.gameInit.Map;
import org.amse.bomberman.server.net.ClientEmulator;
import org.amse.bomberman.server.net.Net;

/**
 *
 * @author chibis
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        new serverFrame();
//        int port = 10500;
//        Net net = new Net(port);
//        net.startAcceptingClients();

//        ClientEmulator cl1 = new ClientEmulator();
//        cl1.connect(port);
//        cl1.takeGamesList();
//        cl1.createGame();
//        cl1.createGame();
//        cl1.takeGamesList();
//        cl1.joinGame(0);
//        cl1.getMap();
//        cl1.startGame();
//        cl1.getMap();
//        
//        net.stopAcceptingClients();
    }
}
