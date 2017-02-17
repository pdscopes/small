package org.madesimple.small.experiment.observer;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class NullObserver implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        // do nothing
    }
}
