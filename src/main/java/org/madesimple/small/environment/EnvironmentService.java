package org.madesimple.small.environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class EnvironmentService
{
    protected static Set<EnvironmentRegister> envs;

    public static synchronized void register(Path availableList)
    {
        if (!Files.isRegularFile(availableList) || !Files.isReadable(availableList)) {
            throw new IllegalArgumentException("Cannot locate readable file " + availableList);
        }

        try (Scanner input = new Scanner(availableList.toFile())) {
            while (input.hasNext()) {
                String line = input.nextLine();

                Object register = Class.forName(line).newInstance();
                if (register instanceof EnvironmentRegister) {
                    getEnvs().add((EnvironmentRegister) register);
                }
            }
        }
        catch (IOException e) {}
        catch (Exception e) {}
    }

    public static synchronized Set<EnvironmentRegister> collection()
    {
        return getEnvs();
    }

    protected static synchronized Set<EnvironmentRegister> getEnvs()
    {
        if(null == envs)
        {
            envs = new HashSet<>();
        }

        return envs;
    }
}