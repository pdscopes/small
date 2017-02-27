package org.madesimple.small.environment.gridworld2d;

import java.util.*;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class GridWorld2dLayout {
    public static final char MAP_FLOOR         = '.';
    public static final char MAP_BARRIER       = '#';
    public static final char MAP_WALL          = '‡';
    public static final char MAP_GATE          = 'x';
    public static final char MAP_ONE_WAY_WEST  = '<';
    public static final char MAP_ONE_WAY_EAST  = '>';
    public static final char MAP_ONE_WAY_NORTH = '^';
    public static final char MAP_ONE_WAY_SOUTH = 'v';

    public List<GridWorld2dEnvironment.Tuple> availableTuples;
    public Set<GridWorld2dState>              goals;
    public char[][]                           map;
    public char[][]                           raw;
    public double[][][]                       probabilities;
    public int                                height;
    public int                                width;
    public int                                stateHeight;
    public int                                stateWidth;

    public void parse(List<Map<Character, Double>> probabilities) {

        // Search the layout for start and goal positions for agents
        availableTuples = new ArrayList<>();
        goals = new HashSet<>();

        for (int y = 1; y < height; y++) {
            for (int x = 1; x < width; x++) {
                if (Character.isAlphabetic(map[y][x]) && Character.isLowerCase(map[y][x])) {
                    GridWorld2dState start  = new GridWorld2dState(toStatePosition(x), toStatePosition(y));
                    GridWorld2dState goal   = null;
                    char             letter = Character.toUpperCase(map[y][x]);
                    for (int dy = 1; dy < height; dy++) {
                        for (int dx = 1; dx < width; dx++) {
                            if (map[dy][dx] == letter) {
                                goal = new GridWorld2dState(toStatePosition(dx), toStatePosition(dy));
                            }
                        }
                    }

                    if (goal != null) {
                        GridWorld2dEnvironment.Tuple tuple = createTuple();
                        tuple.start = start;
                        tuple.goal = goal;

                        availableTuples.add(tuple);
                    }

                }
            }
        }

        if (availableTuples.isEmpty()) {
            List<GridWorld2dState> starts = new ArrayList<>();
            List<GridWorld2dState> goals  = new ArrayList<>();
            for (int y = 1; y < height; y++) {
                for (int x = 1; x < width; x++) {
                    if (map[y][x] == 'S') {
                        starts.add(new GridWorld2dState(toStatePosition(x), toStatePosition(y)));
                    } else if (map[y][x] == 'G') {
                        goals.add(new GridWorld2dState(toStatePosition(x), toStatePosition(y)));
                    }
                }
            }

            for (GridWorld2dState start : starts) {
                GridWorld2dEnvironment.Tuple tuple = createTuple();
                tuple.start = start;

                availableTuples.add(tuple);
            }
            if (goals.size() == 1) {
                for (GridWorld2dEnvironment.Tuple tuple : availableTuples) {
                    tuple.goal = goals.get(0);
                }
                goals.clear();
            }
            this.goals = new HashSet<>(goals);
        }

        // Remove all traces of the start and goal positions
        for (int y = 1; y < height; y += 2) {
            for (int x = 1; x < width; x += 2) {
                if (Character.isAlphabetic(map[y][x])) {
                    map[y][x] = MAP_FLOOR;
                }
            }
        }

        initialiseProbabilities(probabilities);
    }

    protected void initialiseProbabilities(List<Map<Character, Double>> probabilities) {
        this.probabilities = new double[stateHeight][stateWidth][Compass.Cardinal.values().length];
        GridWorld2dState tmp = new GridWorld2dState(0, 0);
        for (int y = 1, dy = 0, numGate = 0; y < height; y += 2, dy++) {
            for (int x = 1, dx = 0; x < width; x += 2, dx++) {
                switch (map[y][x]) {
                    case MAP_FLOOR:
                        for (int a = 0; a < Compass.Cardinal.values().length; a++) {
                            tmp.set(x, y);
                            GridWorld2dState.perform(tmp, Compass.Cardinal.values()[a], 1);
                            double prob = 1.0d;
                            switch (map[tmp.y][tmp.x]) {
                                case MAP_GATE:
                                    switch (Compass.Cardinal.values()[a]) {
                                        case NORTH:
                                            prob = probabilities.get(numGate).get('^');
                                            break;
                                        case SOUTH:
                                            prob = probabilities.get(numGate).get('v');
                                            break;
                                        case EAST:
                                            prob = probabilities.get(numGate).get('>');
                                            break;
                                        case WEST:
                                            prob = probabilities.get(numGate).get('<');
                                            break;
                                    }
                                    break;
                                case MAP_ONE_WAY_WEST:
                                    if (Compass.Cardinal.values()[a] == Compass.Cardinal.EAST) {
                                        prob = 0.0d;
                                    }
                                    break;
                                case MAP_ONE_WAY_EAST:
                                    if (Compass.Cardinal.values()[a] == Compass.Cardinal.WEST) {
                                        prob = 0.0d;
                                    }
                                    break;
                                case MAP_ONE_WAY_NORTH:
                                    if (Compass.Cardinal.values()[a] == Compass.Cardinal.SOUTH) {
                                        prob = 0.0d;
                                    }
                                    break;
                                case MAP_ONE_WAY_SOUTH:
                                    if (Compass.Cardinal.values()[a] == Compass.Cardinal.NORTH) {
                                        prob = 0.0d;
                                    }
                                    break;
                                case MAP_WALL:
                                    prob = 0.0d;
                                    break;
                            }
                            GridWorld2dState.perform(tmp, Compass.Cardinal.values()[a], 1);
                            switch (map[(tmp.y + (height - 1)) % (height - 1)][(tmp.x + (width - 1)) % (width - 1)]) {
                                case MAP_BARRIER:
                                    prob = 0.0d;
                            }

                            this.probabilities[dy][dx][a] = prob;
                            numGate = Math.min(probabilities.size() - 1, ++numGate);
                        }
                        break;
                }
            }
        }
    }

    protected GridWorld2dEnvironment.Tuple createTuple() {
        return new GridWorld2dEnvironment.Tuple();
    }


    protected static int toStatePosition(int p) {
        return p / 2;
    }

    protected static int toLayoutPosition(int p) {
        return 2 * p + 1;
    }
}
