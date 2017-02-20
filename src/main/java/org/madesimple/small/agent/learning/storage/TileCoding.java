package org.madesimple.small.agent.learning.storage;

import org.madesimple.small.environment.ContinuousEnvironment;
import org.madesimple.small.environment.ContinuousState;
import org.madesimple.small.utility.Configurable;
import org.madesimple.small.utility.Configuration;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public abstract class TileCoding implements Configurable {
    protected int nTilings;

    public TileCoding() {
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        nTilings = cfg.getInteger("TileCoding.NumTilings");
    }

    public abstract void initialise(ContinuousEnvironment environment);

    public abstract int[] tiles(ContinuousState state);
}
