package org.madesimple.small.agent.learning.storage.tilecoding;

import org.madesimple.small.agent.learning.storage.TileCoding;
import org.madesimple.small.environment.ContinuousEnvironment;
import org.madesimple.small.environment.ContinuousState;
import org.madesimple.small.utility.Configuration;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * <strong>
 * Taken from: http://incompleteideas.net/rlai.cs.ualberta.ca/RLAI/RLtoolkit/tiles.html, 2014-12-11
 * </strong>
 * </p>
 * <p>
 * External documentation and recommendations on the use of this code is available
 * at <strike>http://www.cs.umass.edu/~rich/tiles.html</strike>.
 * http://incompleteideas.net/rlai.cs.ualberta.ca/RLAI/RLtoolkit/tiles.html
 * </p>
 * <p>
 * This is an implementation of grid-style tile codings, based originally on
 * the UNH CMAC code (see <strike>http://www.ece.unh.edu/robots/cmac.htm</strike>).
 * Here we provide a procedure, "GetTiles", that maps floating-point and integer
 * variables to a list of tiles. This function is memoryless and requires no
 * setup. We assume that hashing collisions are to be ignored. There may be
 * duplicates in the list of tiles, but this is unlikely if memory-size is
 * large.
 * </p>
 * <p>
 * The floating-point input variables will be gridded at unit intervals, so
 * generalisation will be by 1 in each direction, and any scaling will have
 * to be done externally before calling tiles.  There is no generalisation
 * across integer values.
 * </p>
 * <p>
 * It is recommended by the UNH folks that number of tilings be a power
 * of 2, e.g., 16.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Sutton extends TileCoding {
    private static final int  MAX_NUM_VARS   = 20;
    private static final int  MAX_NUM_COORDS = 100;
    private static final long MAX_LONG_INT   = Long.MAX_VALUE;

    private static long[] rndseq;

    private int      nTiles;
    private int      memorySize;
    private double[] minimumValues;
    private double[] tileSpacings;

    public Sutton() {
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        super.setConfiguration(cfg);
        nTiles = cfg.getInteger("TileCoding.Sutton.NumTiles");
    }

    @Override
    public void initialise(ContinuousEnvironment environment) {
        this.memorySize = (int) Math.pow(nTiles, environment.countBounds()) * nTilings;

        minimumValues = environment.lowerBounds();
        tileSpacings = new double[environment.countBounds()];

        for (int i = 0; i < environment.countBounds(); i++) {
            double range = environment.upperBounds()[i] - minimumValues[i];
            tileSpacings[i] = range / (double) nTiles;
        }
    }

    @Override
    public int[] tiles(ContinuousState state) {
        int[]    tiles   = new int[nTilings];
        double[] doubles = new double[state.countFeatures()];

        for (int i = 0; i < state.countFeatures(); i++) {
            // ( (value - min) / (max - min) ) * ( (max - min) / spacing)
            // === (value - min) / spacing
            doubles[i] = (state.tuple(i) - minimumValues[i]) - tileSpacings[i];
        }

        getTiles(tiles, memorySize, doubles);

        return tiles;
    }






    /**
     * @param tiles        provided array contains returned tiles (tile indices)
     * @param memory_size  total number of possible tiles
     * @param doubles      array of doubling point variables
     */
    private void getTiles(int tiles[], int memory_size, double doubles[]) {

        int   i,j;
        int[] qstate = new int[MAX_NUM_VARS];
        int[] base   = new int[MAX_NUM_VARS];
        // one interval number per relevant dimension
        int[] coordinates     = new int[MAX_NUM_VARS * 2 + 1];
        int   num_coordinates = doubles.length + 1;

        // quantise state to integers (henceforth, tile widths == nTilings)
        for( i = 0; i < doubles.length; i++ ) {
            qstate[i] = (int) Math.floor(doubles[i] * nTilings);
            base[i] = 0;
        }

        // compute the tile numbers
        for( j = 0; j < nTilings; j++ ) {

            // loop over each relevant dimension
            for( i = 0; i < doubles.length; i++ ) {

                // find coordinates of activated tile in tiling space
                if( qstate[i] >= base[i] )
                    coordinates[i] = qstate[i] - ((qstate[i] - base[i]) % nTilings);
                else
                    coordinates[i] = qstate[i]+1 + ((base[i] - qstate[i] - 1) % nTilings) - nTilings;

                // compute displacement of next tiling in quantised space
                base[i] += 1 + (2 * i);
            }
            // add additional indices for tiling and hashing_set so they hash differently
            coordinates[i] = j;

            tiles[j] = hash_UNH(coordinates, num_coordinates, memory_size, 449);
        }
        return;
    }



    /**
     * Takes an array of integers and returns the corresponding tile after hashing
     */
    private int hash_UNH(int[] ints, int num_ints, long m, int increment) {
        int  i,k;
        long index;
        long sum = 0;

        // if first call to hashing, initialise table of random numbers
        if( rndseq == null ) {
            rndseq = new long[2048];
            for( k = 0; k < 2048; k++ ) {
                rndseq[k] = 0;
                // 4 === number of bytes in an integer (32 bits === 4 bytes)
                for( i=0; i < 4; ++i )
                    rndseq[k] = (rndseq[k] << 8) | (ThreadLocalRandom.current().nextInt() & 0xff);
            }
        }

        for( i = 0; i < num_ints; i++ ) {
            // add random table offset for this dimension and wrap around
            index  = ints[i];
            index += (increment * i);
            // index %= 2048;
            index = index & 2047;
            while( index < 0 )
                index += 2048;

            // add selected random number to sum
            sum += (long)rndseq[(int)index];
        }
        index = (int)(sum % m);
        while( index < 0 )
            index += m;

        return (int) index;
    }
}
