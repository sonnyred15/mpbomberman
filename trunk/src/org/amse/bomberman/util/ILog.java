/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.util;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author chibis
 */
public interface ILog {

    void println(String message);

    void close() throws IOException;

    List<String> getLog();

    boolean isClosed();
}
