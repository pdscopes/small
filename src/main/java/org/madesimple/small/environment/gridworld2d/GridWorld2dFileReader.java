package org.madesimple.small.environment.gridworld2d;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class GridWorld2dFileReader {
    protected int      count;
    protected int      stateWidth;
    protected int      stateHeight;
    protected int      layoutWidth;
    protected int      layoutHeight;
    protected char[][] layout;

    protected Map<Character, Map<Character, Double>> probabilities;

    public GridWorld2dFileReader() {
        reset();
    }

    public void parse(Path path) throws IOException {
        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new IllegalArgumentException("Cannot locate readable file " + path);
        }

        Scanner input = new Scanner(path.toFile());
        reset();
        while (input.hasNext()) {
            String line = input.nextLine();

            // Ignore empty lines
            if (line.isEmpty()) {
                continue;
            }

            // Check for layout size data
            if (line.startsWith("[") && line.endsWith("]")) {
                parseLayoutSize(line);
                continue;
            }

            // Parse the line
            parseLine(line.charAt(0), line.substring(1).trim());
        }
    }

    protected void reset() {
        layout = null;
        stateWidth = 0;
        stateHeight = 0;
        layoutWidth = 0;
        layoutHeight = 0;
        probabilities = new HashMap<>();
    }

    protected void parseLayoutSize(String line) {
        String[] size = line.substring(1, line.length() - 1).split(",");
        stateWidth = Integer.parseInt(size[0].trim());
        stateHeight = Integer.parseInt(size[1].trim());
        layoutWidth = (stateWidth * 2) + 1;
        layoutHeight = (stateHeight * 2) + 1;
        count = layoutHeight - 1;
        layout = new char[layoutHeight][layoutWidth];
    }

    protected void parseLine(char c, String line) {
        switch (c) {
            // Ignore comments
            case ';':
                break;

            // Store layout segments
            case '>':
                if (layout == null) {
                    throw new IllegalArgumentException("Layout dimensions must come before layout segments");
                }
                if (count < 0) {
                    throw new IllegalArgumentException("No more space for layout segments");
                }
                char[] segment = new char[layoutWidth];
                for (int i = 0; i < layoutWidth; i++) {
                    segment[i] = i < line.length() ? line.charAt(i) : ' ';
                }
                layout[count--] = segment;
                break;

            // Store gate probabilities
            case 'x':
                String[] parts = line.split(" ");
                Map<Character, Double> probMap = new HashMap<>();
                for (String part : parts) {
                    probMap.put(part.charAt(0), Double.parseDouble(part.substring(1)));
                }
                probabilities.put('x', probMap);
                break;
        }
    }

    public char[][] getLayout() {
        return layout;
    }

    public int getStateWidth() {
        return stateWidth;
    }

    public int getStateHeight() {
        return stateHeight;
    }

    public int getLayoutWidth() {
        return layoutWidth;
    }

    public int getLayoutHeight() {
        return layoutHeight;
    }

    public Map<Character, Map<Character, Double>> getProbabilities() {
        return probabilities;
    }
}
