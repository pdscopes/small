package org.madesimple.small.utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * <p>
 * Config is a utility class that allows the user to easily read in
 * configuration values from a given configuration file. The file must be in the
 * following format:
 * </p>
 * <pre>
 * # comments must be on separate lines
 * ; and are denoted but either a '#' or a ';'
 * ; being the *first* character of the line
 *
 * # values can be integers, doubles, strings
 * # arrays of integers, boolean, and arrays of doubles
 * # i.e. integer array
 * variableName1 = 1,2,3,4,5,6,7,8,9
 * ; a string
 * variableName2 = This is a string
 * ; a double
 * variableName3 = -0.07
 * ; a boolean [true|false]
 * variableName4 = true
 * </pre>
 * <p>
 * You must know the exact name of the variable you want remembering it is cASe
 * SenSitiVe. Use the appropriate method to retrieve the correct type.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Configuration extends Properties {

    public Configuration() {
        super();
    }

    public Configuration(Properties properties) {
        super(properties);
    }

    /**
     * @param key the property key
     * @return True if key exists, false otherwise
     */
    public boolean hasProperty(String key) {
        return null != getProperty(key);
    }

    public String getString(String key) {
        return getProperty(key);
    }

    public String getString(String key, String defaultValue) {
        return getProperty(key, defaultValue);
    }

    public char getCharacter(String key) {
        String val = getProperty(key);
        return val.charAt(0);
    }

    public char getCharacter(String key, char defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : val.charAt(0);
    }

    public boolean getBoolean(String key) {
        return "true".equals(getProperty(key));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : "true".equals(val);
    }

    public int getInteger(String key) throws NumberFormatException {
        return Integer.parseInt(getProperty(key));
    }

    public int getInteger(String key, int defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : Integer.parseInt(val);
    }

    public double getDouble(String key) throws NumberFormatException {
        return Double.parseDouble(getProperty(key));
    }

    public double getDouble(String key, double defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : Double.parseDouble(val);
    }

    public char[] getCharacterArray(String key) {
        return stringToCharacterArray(getProperty(key));
    }

    public int[] getIntegerArray(String key) throws NumberFormatException {
        return stringToIntegerArray(getProperty(key));
    }

    public double[] getDoubleArray(String key) throws NumberFormatException {
        return stringToDoubleArray(getProperty(key));
    }

    public Object getInstance(String key) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return Class.forName(getProperty(key)).newInstance();
    }

    private static char[] stringToCharacterArray(String str) {
        String[] raw = str.split(",");
        char[]   arr = new char[raw.length];
        for (int i = 0; i < raw.length; i++) {
            arr[i] = raw[i].charAt(0);
        }

        return arr;
    }

    private static int[] stringToIntegerArray(String str) {
        String[] raw = str.split(",");
        int[]    arr = new int[raw.length];
        for (int i = 0; i < raw.length; i++) {
            arr[i] = Integer.parseInt(raw[i]);
        }

        return arr;
    }

    private static double[] stringToDoubleArray(String str) {
        String[] raw = str.split(",");
        double[] arr = new double[raw.length];
        for (int i = 0; i < raw.length; i++) {
            arr[i] = Double.parseDouble(raw[i]);
        }

        return arr;
    }
}
