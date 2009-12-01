package org.amse.bomberman.client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import org.amse.bomberman.client.model.BombMap.Direction;
import org.amse.bomberman.client.net.Connector;

/**
 * Class for adding bots to the games on server
 * @author michail korovkin
 */
public class Bot implements Runnable{
    private Socket socket;
    private Cell target = null;
    // how receive myCoord???
    private Cell myCoord = new Cell(0,0);
    // how receive myNumber???
    private int myNumber = 1;
    private BombMap temp;
    private int cons = 1000;

    /**
     * Constructor, that create new socket and join bot to the game by its gameNumber
     * @param gameNumber number of the game on server, where this bot must be added
     * @param address InetAddress of socket to this bot
     * @param port port to this bot
     * @throws java.io.IOException if occures any troubles with connection to server
     */
    public Bot(int gameNumber, InetAddress address, int port) throws IOException {
        this.socket = new Socket(address, port);
        joinGame(gameNumber);
        //Connector.getInstance().сonnect(address, port);
        //Connector.getInstance().joinGame(gameNumber);
    }
    public void joinGame(int n) {
        System.out.println(queryAnswer("2 " + n).get(0));
        System.out.println();
    }
    public boolean doMove(Direction dir) {
        String res = queryAnswer("3" + dir.getInt()).get(0);
        return (res.charAt(0) == 't');
    }
    private synchronized ArrayList<String> queryAnswer(String query){
        PrintWriter out = null;
        BufferedReader in = null;
        ArrayList<String> answer=null;
        try {
            out = new PrintWriter(this.socket.getOutputStream());
            System.out.println("Client: Sending query: '"+query+"'.");
            out.println(query);
            out.flush();

            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            String oneLine;
            answer = new ArrayList<String>();
            while ((oneLine = in.readLine()) != null) {
                if (oneLine.length() == 0) {
                    break;
                }
                answer.add(oneLine);
            }
            System.out.println("Client: Answer received.");
        } catch (Exception e) {
        }
        return answer;
    }
    public void run() {
        int x = 0;
        int y = 0;
        Random random = new Random();
        while (true) {
            BombMap map = Model.getInstance().getMap();
            while (map.getValue(new Cell(x, y)) != BombMap.EMPTY) {
                x = random.nextInt(map.getSize() - 1);
                y = random.nextInt(map.getSize() - 1);
            }
            target = new Cell(x, y);
            while ((myCoord.getX() != target.getX()) || (myCoord.getY() != target.getY())) {
                map = Model.getInstance().getMap();
                for (int i = 0; i < map.getSize(); i++) {
                    for (int j = 0; j < map.getSize(); j++) {
                        if (map.getValue(new Cell(i, j)) == myNumber) {
                            myCoord = new Cell(i, j);
                            break;
                        }
                    }
                }
                if ((myCoord.getX() == target.getX()) && (myCoord.getY() == target.getY())) {
                    break;
                }
                temp = map;
                try {
                    Direction direct = findWay(myCoord, target);
                    //Connector.getInstance().doMove(direct);
                    doMove(direct);
                } catch (UnsupportedOperationException ex) {
                    //ex.printStackTrace();
                }
            }
        }
        //System.out.println("End of bot.");
    }
    private Direction findWay(Cell begin, Cell end) {
        rec(begin, cons);
        //System.out.println(temp.toString());
        int steps = temp.getValue(end) - cons;
        Cell currentCell = end;
        for (int i = steps; i > 1; i --) {
            int x = currentCell.getX();
            int y = currentCell.getY();
            if ((x > 0) && temp.getValue(new Cell (x-1,y))-cons == i-1) {
                currentCell = new Cell(x-1,y);
            } else {
                if ((y > 0) && temp.getValue(new Cell (x,y-1))-cons == i-1) {
                    currentCell = new Cell(x,y-1);
                } else {
                    if ((x < temp.getSize()-1) && temp.getValue(new Cell (x+1,y))
                            -cons == i-1) {
                        currentCell = new Cell(x+1,y);
                    } else {
                        if ((y < temp.getSize()-1) && temp
                                .getValue(new Cell (x,y+1))-cons == i-1) {
                            currentCell = new Cell(x,y+1);
                        }
                    }
                }
            }
        }
        if (currentCell.getX() == begin.getX() + 1) {
            return Direction.DOWN;
        } else {
            if (currentCell.getX() == begin.getX() - 1) {
                return Direction.UP;
            } else {
                if (currentCell.getY() == begin.getY() - 1) {
                    return Direction.LEFT;
                } else {
                    if (currentCell.getY() == begin.getY() + 1) {
                        return Direction.RIGHT;
                    } else {
                        // ???? what is it??? what to do???
                        throw new UnsupportedOperationException();
                        //return Direction.RIGHT;
                    }
                }
            }
        }

    }
    private void rec(Cell current, int steps) {
        int x = current.getX();
        int y = current.getY();
        temp.setCell(current, steps);
        if (y < temp.getSize() - 1) {
            Cell next = new Cell(x, y+1);
            if ((temp.getValue(next) == BombMap.EMPTY)
                    || ((temp.getValue(next) > 1000) && (temp.getValue(next) > steps + 1))) {
                rec(next, steps+1);
            }
        }
        if (y > 0) {
            Cell next = new Cell(x, y-1);
            if ((temp.getValue(next) == BombMap.EMPTY)
                    || ((temp.getValue(next) > 1000) && (temp.getValue(next) > steps + 1))) {
                rec(next, steps+1);
            }
        }
        if (x > 0) {
            Cell next = new Cell(x-1, y);
            if ((temp.getValue(next) == BombMap.EMPTY)
                    || ((temp.getValue(next) > 1000) && (temp.getValue(next) > steps + 1))) {
                rec(next, steps+1);
            }
        }
        if (x < temp.getSize() - 1) {
            Cell next = new Cell(x+1, y);
            if ((temp.getValue(next) == BombMap.EMPTY)
                    || ((temp.getValue(next) > 1000) && (temp.getValue(next) > steps + 1))) {
                rec(next, steps+1);
            }
        }
    }
}
