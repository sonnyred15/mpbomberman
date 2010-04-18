/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameinit;

import org.amse.bomberman.server.gameinit.imodel.Player;
import org.amse.bomberman.server.net.tcpimpl.Server;
import org.amse.bomberman.util.Creator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameTest {
    public static Game game;
    public static Server server;

    public GameTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        server = new Server();
        server.start(); // or server status will be shutdowned.

        game = Creator.createGame(server, "1", "testGame", -1);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        server.shutdown();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testJoinAndDisconnect() {
        System.out.println();
        System.out.println("Testing join and disconnect");

        Thread[] threads = new Thread[10];
        int counter =0;
        for (Thread thread : threads) {

            thread = new Thread(new Runnable() {

                public void run() {

                    for (int i = 0; i < 100; i++) {
                        Player pl = game.getPlayer(game.tryJoin(""+i, null)); //TODO null must be session!
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                        if (pl!=null){
                            //game.leaveFromGame(pl);
                        }
                    }

                }
            });
            threads[counter] = thread;
            counter++;
            thread.start();
        }

        int deadThreadsCounter=0;
        while(deadThreadsCounter!= threads.length){
            deadThreadsCounter = 0;
            for (Thread thread : threads) {
                if(thread!=null && !thread.isAlive()){
                    deadThreadsCounter++;
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }           
        }

        assertEquals(0,game.getCurrentPlayersNum());
    }


}