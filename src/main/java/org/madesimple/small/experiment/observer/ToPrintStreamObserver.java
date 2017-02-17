package org.madesimple.small.experiment.observer;

import java.io.PrintStream;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class ToPrintStreamObserver implements Observer {

    protected PrintStream printStream;

    public ToPrintStreamObserver(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (printStream != null) {
            printStream.println(arg);
        }
    }
}
