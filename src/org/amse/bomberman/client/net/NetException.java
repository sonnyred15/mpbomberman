package org.amse.bomberman.client.net;

/**
 * Exception to say that connection with server was lost.
 * @author Michael Korovkin
 * @author Kirilchuk V.E.
 */
public class NetException extends Exception {

    private final static long serialVersionUID = 1L;

    public NetException() {
        super("Connection was lost!\nServer is inaccessible now.\nPlease reconnect!");
    }
}
