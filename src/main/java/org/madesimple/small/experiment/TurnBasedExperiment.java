package org.madesimple.small.experiment;

import org.madesimple.small.utility.Configuration;

import java.util.Map;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class TurnBasedExperiment extends Experiment {
    public String              name;
    public Map<String, String> properties;
    public int                 totalUpdates;
    public int                 observationFrequency;


    public TurnBasedExperiment(String name, Configuration cfg) {
        super(name, cfg);
    }

    public void set(String name, Configuration cfg) {
        super.set(name, cfg);

        this.totalUpdates = cfg.getInteger("Experiment.TotalUpdates");
        this.observationFrequency = cfg.getInteger("Experiment.TotalUpdates") /
                                    cfg.getInteger("Experiment.ObservationCount");
    }
}
