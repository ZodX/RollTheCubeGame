package rollingcube.state;

/**
 * Enum representing the four cardinal directions.
 */
public enum Direction {


    /**
     *  Enum constant describing a direction where the player's cube can roll.
     */
    LEFT(0, 1),
    /**
     *  Enum constant describing a direction where the player's cube can roll.
     */
    DOWN(-1, 0),
    /**
     *  Enum constant describing a direction where the player's cube can roll.
     */
    RIGHT(0, -1),
    /**
     *  Enum constant describing a direction where the player's cube can roll.
     */
    UP(1, 0);

    private int dx;
    private int dy;

    /**
     * Constructor for setting the instance's parameters for the parameters given.
     *
     * @param dx the x coordinate.
     * @param dy the y coordinate.
     */
    private Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Returns the direction that corresponds to the changes in the x-coordinate
     * and the y-coordinate specified.
     *
     * @param dx the change in the x-coordinate
     * @param dy the change in the y-coordinate
     * @return the direction that corresponds to the changes in the x-coordinate
     * and the y-coordinate specified
     */
    public static Direction of(int dx, int dy) {
        for (Direction direction : values()) {
            if (direction.dx == dx && direction.dy == dy) {
                return direction;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Method for putting the actual enum constant's name into string format.
     *
     * @param dx the x parameter.
     * @param dy the y parameter.
     * @return the actual enum constant's name in string format.
     */
    public String toString(int dx, int dy) {
        return Integer.toString(ordinal());
    }
}
