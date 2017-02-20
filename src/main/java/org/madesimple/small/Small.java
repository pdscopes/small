package org.madesimple.small;

import java.io.FileInputStream;
import java.nio.file.Paths;

import org.madesimple.small.agent.Agent;
import org.madesimple.small.environment.EnvironmentRegister;
import org.madesimple.small.environment.EnvironmentService;
import org.madesimple.small.environment.TurnBasedEnvironment;
import org.madesimple.small.experiment.Simulation;
import org.madesimple.small.experiment.Simulator;
import org.madesimple.small.experiment.TurnBasedExperiment;
import org.madesimple.small.experiment.observer.ToPrintStreamObserver;
import org.madesimple.small.utility.Configuration;
import org.madesimple.small.utility.Factory;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Small {

    public static void main(String[] args) throws Exception {
        final Configuration properties = loadProperties();

        EnvironmentService.register(Paths.get("src/main/resources/environment", "available.list"));

        System.out.printf("Welcome to SMALL\nPossible environments are:\n");

        for (EnvironmentRegister envReg : EnvironmentService.collection()) {
            System.out.printf("\t%s\n", envReg.getName());
        }

        System.out.println();


        final TurnBasedExperiment experiment = new TurnBasedExperiment("FooBar", properties);
        Factory<Simulation> factory = () -> {
            try {
                // Create the parts of the simulation
                TurnBasedEnvironment environment = (TurnBasedEnvironment) properties.getInstance("Experiment.Environment");
                environment.setConfiguration(properties);

                TurnBasedEnvironment evaluation = (TurnBasedEnvironment) properties.getInstance("Experiment.Environment");
                evaluation.setConfiguration(properties);

                Agent[] agents = new Agent[environment.requiredAgentCount()];
                for (int i = 0; i < agents.length; i++) {
                    agents[i] = (Agent) properties.getInstance("Experiment.Agent");
                    agents[i].setConfiguration(properties);
                }

                // Create and return the simulation
                Simulation simulation = (Simulation) properties.getInstance("Experiment.Simulation");
                simulation.setExperiment(experiment);
                simulation.setEnvironment(environment);
                simulation.setEvaluation(evaluation);
                simulation.setAgents(agents);
                simulation.addObserver(new ToPrintStreamObserver(System.out));

                return simulation;
            }
            catch (Exception e) {
                return null;
            }
        };
        Simulator simulator = new Simulator(experiment, factory);
        simulator.run();
    }


    private static Configuration loadProperties() throws Exception {
        // Create and load default properties
        Configuration   defaultProperties = new Configuration();
        FileInputStream in                = new FileInputStream(Paths.get("src/main/resources/experiment", "default.properties").toFile());
        defaultProperties.load(in);
        in.close();

        // Create application properties with defaults
        Configuration applicationProperties = new Configuration(defaultProperties);

        // @TODO load custom properties

        return applicationProperties;
    }
}