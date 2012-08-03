package org.amse.bomberman.util;

import java.io.Closeable;
import java.io.IOException;

/**
 *
 * @author Kirilchuk V.E.
 */
public class IOUtilities {

    public static void close(Closeable toClose) {
        try {
            if(toClose != null) {
                toClose.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private IOUtilities() {}

}
