package org.madesimple.small.agent.learning.storage.tilecoding;

import org.madesimple.small.agent.learning.storage.TileCoding;
import org.madesimple.small.environment.ContinuousEnvironment;
import org.madesimple.small.environment.ContinuousState;
import org.madesimple.small.utility.Configuration;
import org.madesimple.small.utility.Hash;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Whiteson extends TileCoding {

    private int[] tilePerFeature;
    private TileArray[] tileArrays;

    public Whiteson() {
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        super.setConfiguration(cfg);
        tilePerFeature = cfg.getIntegerArray("TileCoding.Whiteson.TilesPerFeature");
        tileArrays = new TileArray[nTilings];
    }

    @Override
    public void initialise(ContinuousEnvironment environment) {
        for (int i = 0; i < nTilings; i++) {
            tileArrays[i] = new TileArray(environment, tilePerFeature, i, nTilings);
        }
    }

    @Override
    public int[] tiles(ContinuousState state) {
        int[]    tiles = new int[nTilings];
        double[] tuple = state.tuple();

        for (int i = 0; i < nTilings; i++) {
            tiles[i] = Hash.pair(i, tileArrays[i].get(tuple));
        }

        return tiles;
    }

    static class TileArray {
        private ContinuousEnvironment environment;
        private double[]              offsets;
        private int[]                 tilesPerFeature;

        public TileArray(ContinuousEnvironment environment, int[] tilesPerFeature, int tiling, int nTilings) {
            this.environment = environment;
            this.tilesPerFeature = tilesPerFeature;
            this.offsets = new double[environment.countBounds()];

            for (int i = 0; i < offsets.length; i++) {
                if (tilesPerFeature[i] == 1) {
                    offsets[i] = 0.0d;
                } else {
                    offsets[i] = (double) tiling / (double) nTilings;
                    offsets[i] *= range(environment, i) / (tilesPerFeature[i] - 1);
                    double gap  = range(environment, i) * (((1.0d / (double) (tilesPerFeature[i] - 1))) / (double) nTilings);
                    double rand = ThreadLocalRandom.current().nextDouble(-1.0d, 1.0d);
                    if (tiling == 0) {
                        offsets[i] += Math.abs(rand * gap);
                    } else if (tiling == nTilings - 1) {
                        offsets[i] -= Math.abs(rand * gap);
                    } else {
                        offsets[i] += rand * gap;
                    }
                }
            }
        }

        private double range(ContinuousEnvironment environment, int feature) {
            return environment.upperBounds()[feature] - environment.lowerBounds()[feature];
        }


        public int get(double[] state) {
            return convertToIndex(convertToIndices(state));
        }

        protected int convertToIndex(int[] indices) {
            int index = 0;

            for (int i = 0; i < indices.length; i++) {
                index *= tilesPerFeature[i];
                index += indices[i];
            }

            return index;
        }

        protected int[] convertToIndices(double[] state) {
            int[] indices = new int[state.length];

            for (int i = 0; i < indices.length; i++) {
                double range    = tilesPerFeature[i] == 1 ? range(environment, i) : range(environment, i) * tilesPerFeature[i] / (tilesPerFeature[i] - 1);
                double position = state[i] + offsets[i] - environment.lowerBounds()[i];

                indices[i] = (int) (tilesPerFeature[i] * position / range);
            }

            return indices;
        }
    }
}
