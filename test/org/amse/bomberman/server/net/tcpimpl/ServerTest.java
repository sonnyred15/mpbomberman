/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.util.Creator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kirilchuk V.E
 */
public class ServerTest {

    public final static Server INSTANCE = new Server();
    public final static int MAX_THREADS = 50;
    public final static int MAX_GAMES_BY_THREAD = 100;

    public ServerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        ServerTest.INSTANCE.start();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        ServerTest.INSTANCE.shutdown();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAddGame() throws InterruptedException {
        System.out.println();
        System.out.println("Testing addGame()");

        Thread[] threads = new Thread[MAX_THREADS];
        for (int i = 0; i < MAX_THREADS; i++) {

            Thread t = new Thread(new Runnable() {

                public void run() {
                    for (int i = 0; i < MAX_GAMES_BY_THREAD; i++) {
                        Game game = null;
                        try {
                            game = Creator.createGame(INSTANCE, "19", "anothergame", -1);
                        } catch (FileNotFoundException ex) {
                            fail(ex.getMessage());
                        } catch (IOException ex) {
                            fail(ex.getMessage());
                        }
                        INSTANCE.addGame(game);
                    }
                }
            });
            threads[i] = t;
            t.start();
        }

        int deadThreadsCounter = 0;
        while (deadThreadsCounter != MAX_THREADS) {
            deadThreadsCounter = 0;
            for (Thread thread : threads) {
                if (!thread.isAlive()) {
                    deadThreadsCounter++;
                }
            }
            Thread.sleep(5000);
        }

        assertEquals(MAX_GAMES_BY_THREAD * MAX_THREADS, INSTANCE.getGamesList().size());
    }

    @Test
    public void RemoveGame() throws InterruptedException {//MUST RUN AFTER testAddGame()
        System.out.println();
        System.out.println("Testing removeGame()");

        Thread[] threads = new Thread[MAX_THREADS];
        final List<Game> games = INSTANCE.getGamesList();

        for (int i = 0; i < MAX_THREADS; i++) {

            Thread t = new Thread(new Runnable() {

                public void run() {
                    for (int i = 0; i < MAX_GAMES_BY_THREAD; i++) {
                        synchronized (this) {
                            INSTANCE.removeGame(games.get(games.size() - 1));
                        }
                    }
                }
            });
            threads[i] = t;
            t.start();
        }

        int deadThreadsCounter = 0;
        while (deadThreadsCounter != MAX_THREADS) {
            deadThreadsCounter = 0;
            for (Thread thread : threads) {
                if (!thread.isAlive()) {
                    deadThreadsCounter++;
                }
            }
            Thread.sleep(5000);
        }

        assertEquals(0, INSTANCE.getGamesList().size());
    }
}