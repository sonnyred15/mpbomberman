package org.amse.bomberman.client.net;

/**
 * Exception to say that connection with server was lost.
 * 
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class NetException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Default message for broken connection. Something like:
     * <P>
     * "Connection was lost! Please reconnect!".
     */
    public static final String MESSAGE =
            "Connection was lost!\n" +
            "Server is inaccessible now.\n" +
            "Please reconnect!";

    /**
     * Creates NetException with {@link NetException#MESSAGE} message.
     */
    public NetException() {
        super(MESSAGE);
    }

    /**
     * Creates NetException with specified message.
     *
     * @param message message to set.
     */
    public NetException(String message) {
        super(message);
    }
}
