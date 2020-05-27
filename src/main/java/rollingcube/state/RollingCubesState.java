package rollingcube.state;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;

/**
 * Class representing the state of the puzzle.
 */
@Data
@Slf4j
public class RollingCubesState implements Cloneable {

    /**
     * The array representing the initial configuration of the tray.
     */
    public static final int[][] INITIAL = {
            {6, 6, 6, 6, 7, 6, 6},
            {6, 7, 7, 6, 6, 8, 7},
            {6, 6, 6, 6, 7, 7, 6},
            {6, 6, 6, 6, 7, 6, 6},
            {6, 7, 6, 6, 6, 6, 6},
            {6, 6, 6, 7, 7, 6, 7},
            {7, 6, 6, 6, 6, 6, 0}
    };

    /**
     * The array storing the current configuration of the tray.
     */
    @Setter(AccessLevel.NONE)
    private int[][] tray;

    /**
     * The x coordinate of the player's cube.
     */
    private int player_pos_x;

    /**
     * The y coordinate of the player's cube.
     */
    private int player_pos_y;

    /**
     * The x coordinate of the goal field.
     */

    private int goal_pos_x;

    /**
     * The y coordinate of the goal field.
     */
    private int goal_pos_y;

    private int player_face, player_leftside, player_upside, player_rightside, player_downside, player_under;

    /**
     * Creates a {@code RollingCubesState} object that is initialized it with
     * the specified array.
     *
     *
     */
    public RollingCubesState() {
        this(INITIAL);
    }

    /**
     * A constructor called by the basic constructor.
     *
     * @param a the tray.
     * @throws IllegalArgumentException if the array does not represent a valid
     *                                    configuration of the tray
     */
    public RollingCubesState(int[][] a) {
        isValidTray(a);
        initTray(a);
        this.player_face = 0;
        this.player_leftside = 1;
        this.player_downside = 2;
        this.player_rightside = 3;
        this.player_upside = 4;
        this.player_under = 5;
    }

    /**
     * Returns whether the tray is a valid tray for playing the game by the rules.
     *
     * @param a the checked tray;
     * @return {@code true} if the tray is valid. Throwing {@code IllegalArgumentException} with the message "Not a valid tray", if the tray is invalid.
     */
    public boolean isValidTray(int[][] a) {
        boolean isGoal = false,
                isStarting = false;
        int cubeCount = 0;

        for (int i = 0; i < 7; i++)
            for (int j = 0; j < 7; j++)
                if (a[i][j] == 0) {
                    isStarting = true;
                    cubeCount++;
                } else
                    if (a[i][j] == 8)
                        isGoal = true;
                    else
                        if (a[i][j] != 6 && a[i][j] != 7)
                            throw new IllegalArgumentException("Not a valid tray");

        if (isGoal && isStarting && cubeCount == 1)
            return true;
        else
            throw new IllegalArgumentException("Not a valid tray");
    }

    /**
     * Initializing the tray, the player's and the goal's coordinates.
     *
     * @param init the current tray.
     */
    public void initTray(int[][] init) {
        this.tray = new int[7][7];
        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < 7; ++j) {
                this.tray[i][j] = init[i][j];
                if (this.tray[i][j] == 0) {
                    this.player_pos_x = i;
                    this.player_pos_y = j;
                }
                if (this.tray[i][j] == 8) {
                    this.goal_pos_x = i;
                    this.goal_pos_y = j;
                }
            }
        }
    }

    /**
     * Checks whether the puzzle is solved.
     *
     * @return {@code true} if the puzzle is solved, {@code false} otherwise
     */
    public boolean isSolved() {
        if (player_pos_x == goal_pos_x && player_pos_y == goal_pos_y)
            return true;
        return false;
    }

    /**
     * Returns whether the field is a goal field.
     *
     * @param row the row.
     * @param col the column.
     * @return {@code true} if the field is a goal field, {@code false} if it's not.
     */
    public boolean isGoalField(int row, int col) {
        if (row == goal_pos_x && col == goal_pos_y)
            return true;
        return false;
    }

    /**
     * Returns whether the field is near to the player's cube.
     *
     * @param row the row of the checked field.
     * @param col the column of the checked field.
     * @return {@code true} if the field is near to the player's cube, {@code false} if the field is more than 1 block away.
     */
    public boolean isFieldNear(int row, int col) {
        if ((Math.abs(row - player_pos_x) < 2 && Math.abs(row - player_pos_x) > 0 && Math.abs(col - player_pos_y) == 0) ||
                ((Math.abs(row - player_pos_x) == 0 && Math.abs(col - player_pos_y) < 2 && Math.abs(col - player_pos_y) > 0)))
            return true;
        return false;
    }

    /**
     * Returns whether the field is empty or a goal field.
     *
     * @param row the row of the checked field.
     * @param col the column of the checked field.
     * @return {@code true} if its blocked, {@code false} if the field is empty or a goal field.
     */
    public boolean isFieldBlocked(int row, int col) {
        if (this.tray[row][col] != 7)
            return false;
        return true;
    }

    /**
     * Returns the direction to which the cube at the specified position is
     * rolled to the empty space.
     *
     * @param fromRow the base row.
     * @param fromCol the base column.
     * @param toRow the goal row.
     * @param toCol the goal column.
     * @return the direction to which the cube at the specified position is
     * rolled to the empty space
     * @throws IllegalArgumentException if the cube at the specified position
     * can not be rolled to the empty space
     */
    public String getRollDirection(int fromRow, int fromCol, int toRow, int toCol) {
        if (isFieldBlocked(toRow, toCol)) {
            throw new IllegalArgumentException("Field is blocked.");
        }
        return Direction.of(fromRow - toRow, fromCol - toCol).toString();
    }

    /**
     * Returns whether the cube can be rolled to the checked field without it's red side touching the ground.
     *
     * @param d the direction in string format.
     * @return {@code true} if the cube can be rolled, {@code false} if its not possible.
     */
    public boolean canRollToDirection(String d) {
        switch (d) {
            case "LEFT":
                if (player_leftside == 0)
                    return false;
                break;
            case "DOWN":
                if (player_downside == 0)
                    return false;
                break;
            case "RIGHT":
                if (player_rightside == 0)
                    return false;
                break;
            case "UP":
                if (player_upside == 0)
                    return false;
                break;
        }
        return true;
    }

    /**
     * Returns whether the cube can be rolled to the checked field.
     *
     * @param row the row of the checked field.
     * @param col the column of the checked field.
     * @return {@code true} if field is not blocked and the cube won't be rolled on it's red side, {@code false} if one of these fails.
     */
    public boolean canRollToField (int row, int col) {
        return isFieldNear(row, col) && !isFieldBlocked(row, col) && canRollToDirection(getRollDirection(player_pos_x, player_pos_y, row, col));
    }

    /**
     * Rolls the cube to the field given by the parameters.
     *
     * @param row the row of the field.
     * @param col the column of the field.
     */
    public void rollToEmptySpace(int row, int col) {
        int face;
        switch (getRollDirection(player_pos_x, player_pos_y, row, col)) {
            case "LEFT":
                log.info("Rolling the cube leftwards to XY({},{})", row, col);
                face = player_face;

                player_face = player_rightside;
                player_rightside = player_under;
                player_under = player_leftside;
                player_leftside = face;

                this.tray[player_pos_x][player_pos_y] = 6;
                this.tray[row][col] = player_face;
                player_pos_y--;
                break;
            case "DOWN":
                log.info("Rolling the cube downwards to XY({},{})", row, col);
                face = player_face;

                player_face = player_upside;
                player_upside = player_under;
                player_under = player_downside;
                player_downside = face;

                this.tray[player_pos_x][player_pos_y] = 6;
                this.tray[row][col] = player_face;
                player_pos_x++;
                break;
            case "RIGHT":
                log.info("Rolling the cube rightwards to XY({},{})", row, col);
                face = player_face;

                player_face = player_leftside;
                player_leftside = player_under;
                player_under = player_rightside;
                player_rightside = face;

                this.tray[player_pos_x][player_pos_y] = 6;
                this.tray[row][col] = player_face;
                player_pos_y++;
                break;
            case "UP":
                log.info("Rolling the cube upwards to XY({},{})", row, col);
                face = player_face;

                player_face = player_downside;
                player_downside = player_under;
                player_under = player_upside;
                player_upside = face;

                this.tray[player_pos_x][player_pos_y] = 6;
                this.tray[row][col] = player_face;
                player_pos_x--;
                break;
        }
    }

    /**
     * Method for outputting the tray in visible way.
     *
     * @return string format of the current game state's tray.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : tray) {
            for (int field : row) {
                sb.append(field).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        RollingCubesState state = new RollingCubesState();
        System.out.println(state);
        state.rollToEmptySpace(6, 5);
        System.out.println(state);
    }
}
