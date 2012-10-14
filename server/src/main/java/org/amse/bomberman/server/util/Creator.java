package org.amse.bomberman.server.util;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class.
 * Main appointment to create something.
 * @author Kirilchuk V.E.
 */
public class Creator {

    private static final Logger LOG = LoggerFactory.getLogger(Creator.class);
    
    private Creator() {}

    static String[] botNames =
    {"Suicider", "Terminator",
     "Nike", "Budda",
     "Adibas", "Killer",
     "Gosu", "Nietzsche",
     "Idiot", "Archimed",
     "Gosling", "Switch",
     "Gauss", "KISS",
     "Lenin", "Factory",
     "Stalin", "Singletone",
     "Steveee", "Compositor",
     "Patriot", "Adapter",
     "Slayer", "Debian",
     "J2SEBot", "Lenny",
     "iBot", "Squeeze",
     "I_KILL_YOU", "SuperMan",
     "Ololo", "Linus T.",
     "Bug", "Feature"
    };
    static Random rnd = new Random();

    public static String randomBotName() {
        int id = rnd.nextInt(botNames.length);
        return botNames[id];
    }
}
