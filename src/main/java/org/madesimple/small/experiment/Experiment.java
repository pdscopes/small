package org.madesimple.small.experiment;

import org.madesimple.small.utility.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Experiment {
    public String              name;
    public Map<String, String> properties;
    public Simulation.Type     simulationType;
    public int                 randomSeed;
    public int                 totalRuns;
    public int                 numAgents;

    public Experiment(String name, Configuration cfg) {
        set(name, cfg);
    }

    public void set(String name, Configuration cfg) {
        this.name = name;
        this.properties = new LinkedHashMap<>();
        this.simulationType = Simulation.Type.valueOf(cfg.getString("Experiment.SimulationType").toUpperCase());
        this.randomSeed = cfg.getInteger("Experiment.RandomSeed");
        this.totalRuns = cfg.getInteger("Experiment.TotalRuns");
        this.numAgents = cfg.getInteger("Experiment.NumAgents");
    }
}
