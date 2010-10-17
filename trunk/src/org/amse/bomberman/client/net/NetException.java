package org.amse.bomberman.client.net;

/**
 * Exception to say that connection with server was lost.
 * 
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class NetException extends Exception {

    private static final long serialVersionUID = 1L;
    public static final String MESSAGE =
            "Connection was lost!\n" +
            "Server is inaccessible now.\n" +
            "Please reconnect!";

    public NetException() {
        super(MESSAGE);
    }

    public NetException(String message) {
        super(message);
    }
}
