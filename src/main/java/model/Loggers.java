package model;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Loggers {

    private static final Logger ROOT = Logger.getLogger("");
    // Avoid instantiation.
    private Loggers(){
    }
    /*
     * Set the level of the loggers, just for debugging.
     */
    public static void changeLoggersLevels(Level level){

        ROOT.setLevel(level);
        Handler[] handlers = ROOT.getHandlers();

        for (int index = 0; index < handlers.length; ++index) {
            handlers[index].setLevel(level);
        }

    }
}
