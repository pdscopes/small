package org.madesimple.small.environment;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class EnvironmentService {
    private static Map<String, EnvironmentRegister> registerMap;

    public static synchronized void register(Path availableList) throws Exception {
        if (!Files.isRegularFile(availableList) || !Files.isReadable(availableList)) {
            throw new IllegalArgumentException("Cannot locate readable file " + availableList);
        }

        Scanner input = new Scanner(availableList.toFile());
        while (input.hasNext()) {
            String line = input.nextLine();

            EnvironmentRegister register = (EnvironmentRegister) Class.forName(line).newInstance();
            getEnvironmentRegisters().put(register.getIdentifier(), register);
        }
    }

    public static synchronized EnvironmentRegister get(String identifier) {
        return getEnvironmentRegisters().get(identifier);
    }

    public static synchronized Map<String, EnvironmentRegister> collection() {
        return getEnvironmentRegisters();
    }

    private static synchronized Map<String, EnvironmentRegister> getEnvironmentRegisters() {
        if (null == registerMap) {
            registerMap = new HashMap<>();
        }

        return registerMap;
    }
}