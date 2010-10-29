package org.amse.bomberman.protocol;

/**
 * Class that represents exception about not valid data
 * in protocol message.
 *
 * @author Kirilchuk V.E.
 */
public class InvalidDataException extends Exception {
    private static final long serialVersionUID = 1L;
    private final int messageId;

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public InvalidDataException(int messageId, String message) {
        super(message);
        this.messageId = messageId;
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public InvalidDataException(int messageId, String message, Throwable cause) {
        super(message, cause);
        this.messageId = messageId;
    }

    /**
     * @return message id that tells for what message invalid data exception
     * was thrown.
     */
    public int getMessageId() {
        return messageId;
    }
}
