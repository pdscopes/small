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
    public Simulator.Type      simulatorType;
    public int                 randomSeed;
    public int                 totalRuns;

    public Experiment(String name, Configuration cfg) {
        set(name, cfg);
    }

    public void set(String name, Configuration cfg) {
        this.name = name;
        this.properties = new LinkedHashMap<>();
        this.simulatorType = Simulator.Type.valueOf(cfg.getString("Experiment.SimulatorType").toUpperCase());
        this.randomSeed = cfg.getInteger("Experiment.RandomSeed");
        this.totalRuns = cfg.getInteger("Experiment.TotalRuns");
    }
}
