package org.madesimple.small.environment.gridworld2d;

/**
 * <p>
 * The Compass interface defines how the three implementations given
 * should behave. A compass should be able to get the clockwise angle
 * from North, get the opposite direction, and the next direction
 * anti/clockwise.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface Compass<E> {

    /**
     * @return The clockwise angle in degrees of compass direction from North
     */
    double angle();

    /**
     * @return The opposite compass direction
     */
    E opposite();

    /**
     * @return The compass direction that is one step anti-clockwise
     */
    E antiClockwise();

    /**
     * @return The compass direction that is one step clockwise
     */
    E clockwise();

    public static enum Cardinal implements Compass<Cardinal> {
        NORTH(0), EAST(90), SOUTH(180), WEST(270);

        private double angle;

        Cardinal(int angle) {
            this.angle = angle;
        }

        @Override
        public double angle() {
            return angle;
        }

        @Override
        public Cardinal opposite() {
            switch (this) {
                case NORTH:
                    return SOUTH;
                case SOUTH:
                    return NORTH;
                case EAST:
                    return WEST;
                case WEST:
                    return EAST;
                default:
                    throw new NullPointerException();
            }
        }

        @Override
        public Cardinal antiClockwise() {
            switch (this) {
                case NORTH:
                    return WEST;
                case EAST:
                    return NORTH;
                case SOUTH:
                    return EAST;
                case WEST:
                    return SOUTH;
                default:
                    throw new NullPointerException();
            }
        }

        @Override
        public Cardinal clockwise() {
            switch (this) {
                case NORTH:
                    return EAST;
                case EAST:
                    return SOUTH;
                case SOUTH:
                    return WEST;
                case WEST:
                    return NORTH;
                default:
                    throw new NullPointerException();
            }
        }

        /**
         * @param rad The angle (in radians) to get the closest compass direction of
         * @return The closest compass direction to the specified angle (in radians)
         */
        public static Cardinal getClosest(double rad) {
            double degrees = (Math.toDegrees(rad) + 360) % 360;

            if (Math.abs(degrees - NORTH.angle) <= 45.0d || Math.abs(degrees - 360) <= 45.0d) {
                return NORTH;
            }
            if (Math.abs(degrees - EAST.angle) <= 45.0d) {
                return EAST;
            }
            if (Math.abs(degrees - SOUTH.angle) <= 45.0d) {
                return SOUTH;
            }
            if (Math.abs(degrees - WEST.angle) <= 45.0d) {
                return WEST;
            }
            throw new RuntimeException("No closest angle to: " + rad + " (" + degrees + ")");
        }
    }

    public static enum Ordinal implements Compass<Ordinal> {
        NORTH(0), EAST(90), SOUTH(180), WEST(270),
        NORTHEAST(45), SOUTHEAST(135), SOUTHWEST(225), NORTHWEST(315);

        private double angle;

        private Ordinal(int angle) {
            this.angle = angle;
        }

        @Override
        public double angle() {
            return angle;
        }

        @Override
        public Ordinal opposite() {
            switch (this) {
                case NORTH:
                    return SOUTH;
                case NORTHEAST:
                    return SOUTHWEST;
                case EAST:
                    return WEST;
                case SOUTHEAST:
                    return NORTHWEST;
                case SOUTH:
                    return NORTH;
                case SOUTHWEST:
                    return NORTHEAST;
                case WEST:
                    return EAST;
                case NORTHWEST:
                    return SOUTHEAST;
                default:
                    throw new NullPointerException();
            }
        }

        @Override
        public Ordinal antiClockwise() {
            switch (this) {
                case NORTH:
                    return NORTHWEST;
                case NORTHEAST:
                    return NORTH;
                case EAST:
                    return NORTHEAST;
                case SOUTHEAST:
                    return EAST;
                case SOUTH:
                    return SOUTHEAST;
                case SOUTHWEST:
                    return SOUTH;
                case WEST:
                    return SOUTHWEST;
                case NORTHWEST:
                    return WEST;
                default:
                    throw new NullPointerException();
            }
        }

        @Override
        public Ordinal clockwise() {
            switch (this) {
                case NORTH:
                    return NORTHEAST;
                case NORTHEAST:
                    return EAST;
                case EAST:
                    return SOUTHEAST;
                case SOUTHEAST:
                    return SOUTH;
                case SOUTH:
                    return SOUTHWEST;
                case SOUTHWEST:
                    return WEST;
                case WEST:
                    return NORTHWEST;
                case NORTHWEST:
                    return NORTH;
                default:
                    throw new NullPointerException();
            }
        }

        /**
         * @param rad The angle (in radians) to get the closest compass direction of
         * @return The closest compass direction to the specified angle (in radians)
         */
        public static Ordinal getClosest(double rad) {
            double degrees = (Math.toDegrees(rad) + 360) % 360;

            if (Math.abs(degrees - NORTH.angle) <= 22.5d) {
                return NORTH;
            }
            if (Math.abs(degrees - NORTHEAST.angle) <= 22.5d) {
                return NORTHEAST;
            }
            if (Math.abs(degrees - EAST.angle) <= 22.5d) {
                return EAST;
            }
            if (Math.abs(degrees - SOUTHEAST.angle) <= 22.5d) {
                return SOUTHEAST;
            }
            if (Math.abs(degrees - SOUTH.angle) <= 22.5d) {
                return SOUTH;
            }
            if (Math.abs(degrees - SOUTHWEST.angle) <= 22.5d) {
                return SOUTHWEST;
            }
            if (Math.abs(degrees - WEST.angle) <= 22.5d) {
                return WEST;
            }
            if (Math.abs(degrees - NORTHWEST.angle) <= 22.5d) {
                return NORTHWEST;
            }
            throw new RuntimeException("No closest angle to: " + rad);
        }
    }
}
