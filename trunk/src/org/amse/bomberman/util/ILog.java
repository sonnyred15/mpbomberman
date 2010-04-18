
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.util;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.List;

/**
 * Interface that represents simple log.
 * @author Kirilchuk V.E.
 */
public interface ILog {

    /**
     * Printings new message in log.
     * @param message message to add.
     */
    void println(String message);

    /**
     * Closing log.
     * @throws IOException if IO errors occurs while closing log.
     */
    void close() throws IOException;

    /**
     * Returns log text.
     * @return log text.
     */
    List<String> getLog();

    /**
     * Checks if log is closed.
     * @return true if log is closed, false otherwise.
     */
    boolean isClosed();
}
