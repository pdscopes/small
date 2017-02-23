package org.madesimple.small;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.madesimple.small.agent.Agent;
import org.madesimple.small.environment.EnvironmentRegister;
import org.madesimple.small.environment.EnvironmentService;
import org.madesimple.small.environment.TurnBasedEnvironment;
import org.madesimple.small.environment.mountaincar.MountainCarAction;
import org.madesimple.small.environment.mountaincar.MountainCarEnvironment;
import org.madesimple.small.environment.mountaincar.MountainCarState;
import org.madesimple.small.environment.mountaincar.MountainCarVisualiser;
import org.madesimple.small.experiment.Simulation;
import org.madesimple.small.experiment.Simulator;
import org.madesimple.small.experiment.TurnBasedExperiment;
import org.madesimple.small.experiment.observer.ToPrintStreamObserver;
import org.madesimple.small.utility.Configuration;
import org.madesimple.small.utility.Factory;
import org.madesimple.small.visualisation.Visualiser;

import javax.swing.*;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Small {

    public static void main(String[] args) throws Exception {
        final Configuration properties = loadProperties();

        EnvironmentService.register(Paths.get("src/main/resources/environment", "available.list"));

        System.out.printf("Welcome to SMALL\nPossible environments are:\n");

        for (EnvironmentRegister envReg : EnvironmentService.collection().values()) {
            System.out.printf("\t%s\n", envReg.getName());
        }

        System.out.println();


//        visualise(properties);
        experiment(properties);
    }

    private static void visualise(Configuration properties) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.exit(1);
        }

        JFrame frame = new JFrame("SMALL");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);

        final MountainCarEnvironment env        = new MountainCarEnvironment();
        final MountainCarVisualiser  visualiser = new MountainCarVisualiser();

        visualiser.setConfiguration(properties);
        frame.add(visualiser);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);


        Thread th = new Thread(() -> {
            MountainCarState state = new MountainCarState();
            MountainCarAction[] actions = MountainCarAction.values();
            while (true) {
                try {
                    TimeUnit.MICROSECONDS.sleep(50);

                    env.move(state, actions[ThreadLocalRandom.current().nextInt(actions.length)].ordinal());
                    visualiser.update(env, state);
                } catch (InterruptedException e) {
                }
            }

        });
        th.start();
    }

    private static void experiment(Configuration properties) {
        final TurnBasedExperiment experiment = new TurnBasedExperiment("FooBar", properties);
        Factory<Simulation> factory = () -> {
            try {
                // Get the EnvironmentRegister
                EnvironmentRegister register = EnvironmentService.get(properties.getString("Experiment.Environment"));

                // Create the parts of the simulation
                TurnBasedEnvironment environment = (TurnBasedEnvironment) register.getEnvironment();
                environment.setConfiguration(properties);

                TurnBasedEnvironment evaluation = (TurnBasedEnvironment) register.getEnvironment();
                evaluation.setConfiguration(properties);

                Agent[] agents = new Agent[environment.requiredAgentCount()];
                for (int i = 0; i < agents.length; i++) {
                    agents[i] = (Agent) properties.getInstance("Experiment.Agent");
                    agents[i].setConfiguration(properties);
                }

                if (properties.getBoolean("Experiment.Visualise")) {
                    Visualiser visualiser = register.getVisualiser();
                    if (visualiser != null && evaluation instanceof Observable) {
                        ((Observable) evaluation).addObserver(visualiser);
                        generateFrame(properties, visualiser);
                    }
                }

                // Create and return the simulation
                Simulation simulation = (Simulation) properties.getInstance("Experiment.Simulation");
                simulation.setExperiment(experiment);
                simulation.setEnvironment(environment);
                simulation.setEvaluation(evaluation);
                simulation.setAgents(agents);
                simulation.addObserver(new ToPrintStreamObserver(System.out));

                return simulation;
            } catch (Exception e) {
                return null;
            }
        };
        Simulator simulator = new Simulator(experiment, factory);
        simulator.run();
    }
    private static JFrame generateFrame(Configuration properties, Visualiser visualiser) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.exit(1);
        }

        JFrame frame = new JFrame("SMALL");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);

        visualiser.setConfiguration(properties);
        frame.add((JComponent) visualiser);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);

        return frame;
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