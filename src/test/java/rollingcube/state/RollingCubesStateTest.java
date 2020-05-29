package rollingcube.state;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class RollingCubesStateTest {
    @Test
    void testConstructor() {
        RollingCubesState testGameState = new RollingCubesState();
        assertEquals(0, testGameState.getPlayer_face());
        assertEquals(1, testGameState.getPlayer_leftside());
        assertEquals(2, testGameState.getPlayer_downside());
        assertEquals(3, testGameState.getPlayer_rightside());
        assertEquals(4, testGameState.getPlayer_upside());
        assertEquals(5, testGameState.getPlayer_under());
    }

    @Test
    void testIsValidTray() {
        RollingCubesState testGameState = new RollingCubesState(new int[][] {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 6, 6, 6, 6, 6},
                {6, 6, 6, 7, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 0}}
                );
        assertTrue(testGameState.isValidTray(testGameState.getTray()));

        testGameState.getTray()[6][6] = 6;
        assertThrows(IllegalArgumentException.class, () -> testGameState.isValidTray(testGameState.getTray()));

        testGameState.getTray()[6][6] = 0;
        testGameState.getTray()[1][5] = 6;
        assertThrows(IllegalArgumentException.class, () -> testGameState.isValidTray(testGameState.getTray()));

        testGameState.getTray()[4][4] = 9;
        assertThrows(IllegalArgumentException.class, () -> testGameState.isValidTray(testGameState.getTray()));
    }

    @Test
    void testInitTray() {
        RollingCubesState testGameState = new RollingCubesState();
        int[][] testTray = new int[7][7];

        String url = getClass().getResource("/map/map.txt").toExternalForm().toString();
        String newUrl = "";

        for (int i = 5; i < url.length(); i++)
            newUrl = newUrl + url.charAt(i);

        try {
            Scanner sc = new Scanner(new File(newUrl));

            for (int i = 0; i < 7; i++)
                for (int j = 0; j < 7; j++)
                    testTray[i][j] = sc.nextInt();

            sc.close();
        } catch (FileNotFoundException e) {
        }

        assertArrayEquals(testTray, testGameState.getTray());
    }

    @Test
    void testIsSolved() {
        RollingCubesState testGameState = new RollingCubesState();
        assertFalse(testGameState.isSolved());

        RollingCubesState testGameState2 = new RollingCubesState();
        testGameState2.setPlayer_pos_x(testGameState2.getGoal_pos_x());
        testGameState2.setPlayer_pos_y(testGameState2.getGoal_pos_y());

        assertTrue(testGameState2.isSolved());
    }

    @Test
    void testIsGoalField() {
        RollingCubesState testGameState = new RollingCubesState(new int[][]{
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 6, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 8, 7, 6, 6},
                {6, 7, 6, 0, 6, 6, 6},
                {6, 6, 6, 6, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        });

        assertTrue(testGameState.isGoalField(3, 3));
        assertFalse(testGameState.isGoalField(4, 3));
        assertFalse(testGameState.isGoalField(2, 4));
    }

    @Test
    void testIsFieldNear() {
        RollingCubesState testGameState = new RollingCubesState();

        assertTrue(testGameState.isFieldNear(6,5));
        assertTrue(testGameState.isFieldNear(5, 6));
        assertFalse(testGameState.isFieldNear(5,5));
        assertFalse(testGameState.isFieldNear(6, 4));
    }

    @Test
    void testIsFieldBlocked() {
        RollingCubesState testGameState = new RollingCubesState(new int[][] {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 7, 7, 6, 6},
                {6, 7, 6, 0, 7, 6, 6},
                {6, 6, 6, 7, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        });

        assertTrue(testGameState.isFieldBlocked(3,3));
        assertFalse(testGameState.isFieldBlocked(2, 0));
        assertFalse(testGameState.isFieldBlocked(1, 5));
    }

    @Test
    void testGetRollDirection() {
        RollingCubesState testGameState = new RollingCubesState(new int[][] {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 6, 0, 6, 6, 6},
                {6, 6, 6, 6, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        });

        assertEquals("LEFT", testGameState.getRollDirection(testGameState.getPlayer_pos_x(), testGameState.getPlayer_pos_y(), 4, 2));
        assertEquals("UP", testGameState.getRollDirection(testGameState.getPlayer_pos_x(), testGameState.getPlayer_pos_y(), 3, 3));
        assertEquals("RIGHT", testGameState.getRollDirection(testGameState.getPlayer_pos_x(), testGameState.getPlayer_pos_y(), 4, 4));
        assertEquals("DOWN", testGameState.getRollDirection(testGameState.getPlayer_pos_x(), testGameState.getPlayer_pos_y(), 5, 3));

        RollingCubesState testGameState2 = new RollingCubesState(new int[][] {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 7, 7, 6, 6},
                {6, 7, 7, 0, 7, 6, 6},
                {6, 6, 6, 7, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        });

        assertThrows(IllegalArgumentException.class, () -> testGameState2.getRollDirection(testGameState2.getPlayer_pos_x(), testGameState2.getPlayer_pos_y(), 4, 2));
        assertThrows(IllegalArgumentException.class, () -> testGameState2.getRollDirection(testGameState2.getPlayer_pos_x(), testGameState2.getPlayer_pos_y(), 3, 3));
        assertThrows(IllegalArgumentException.class, () -> testGameState2.getRollDirection(testGameState2.getPlayer_pos_x(), testGameState2.getPlayer_pos_y(), 4, 4));
        assertThrows(IllegalArgumentException.class, () -> testGameState2.getRollDirection(testGameState2.getPlayer_pos_x(), testGameState2.getPlayer_pos_y(), 5, 3));
    }

    @Test
    void testCanRollToDirection() {
        RollingCubesState testGameState = new RollingCubesState(new int[][] {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 6, 0, 6, 6, 6},
                {6, 6, 6, 6, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        });

        assertTrue(testGameState.canRollToDirection("LEFT"));
        assertTrue(testGameState.canRollToDirection("DOWN"));
        assertTrue(testGameState.canRollToDirection("RIGHT"));
        assertTrue(testGameState.canRollToDirection("UP"));

        testGameState.rollToEmptySpace(4, 2);
        assertFalse(testGameState.canRollToDirection("LEFT"));
        testGameState.rollToEmptySpace(4, 3);
        testGameState.rollToEmptySpace(5, 3);
        assertFalse(testGameState.canRollToDirection("DOWN"));
        testGameState.rollToEmptySpace(4, 3);
        testGameState.rollToEmptySpace(4, 4);
        assertFalse(testGameState.canRollToDirection("RIGHT"));
        testGameState.rollToEmptySpace(4, 3);
        testGameState.rollToEmptySpace(3, 3);
        assertFalse(testGameState.canRollToDirection("UP"));
    }

    @Test
    void testCanRollToField() {
        RollingCubesState testGameState = new RollingCubesState(new int[][] {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 7, 7, 6, 6},
                {6, 7, 6, 0, 7, 6, 6},
                {6, 6, 6, 6, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        });

        assertTrue(testGameState.canRollToField(4, 2));
        assertTrue(testGameState.canRollToField(5, 3));
        assertFalse(testGameState.canRollToField(4, 4));
        assertFalse(testGameState.canRollToField(3, 3));
        assertFalse(testGameState.canRollToField(4,3));
        assertFalse(testGameState.canRollToField(2, 1));
    }

    @Test
    void testRollToEmptySpace() {
        RollingCubesState testGameState = new RollingCubesState(new int[][] {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 7, 7, 6, 6},
                {6, 7, 6, 0, 7, 6, 6},
                {6, 6, 6, 6, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        });

        testGameState.rollToEmptySpace(4,2);

        int[][] expectedLeft = {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 7, 7, 6, 6},
                {6, 7, 3, 6, 7, 6, 6},
                {6, 6, 6, 6, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        };

        assertArrayEquals(expectedLeft, testGameState.getTray());

        testGameState.rollToEmptySpace(5,2);

        int[][] expectedDown = {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 7, 7, 6, 6},
                {6, 7, 6, 6, 7, 6, 6},
                {6, 6, 4, 6, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        };

        assertArrayEquals(expectedDown, testGameState.getTray());

        testGameState.rollToEmptySpace(5,3);

        int[][] expectedRight = {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 7, 7, 6, 6},
                {6, 7, 6, 6, 7, 6, 6},
                {6, 6, 6, 0, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        };

        assertArrayEquals(expectedRight, testGameState.getTray());

        testGameState.rollToEmptySpace(4,3);

        int[][] expectedUp = {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 7, 7, 6, 6},
                {6, 7, 6, 3, 7, 6, 6},
                {6, 6, 6, 6, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        };

        assertArrayEquals(expectedUp, testGameState.getTray());
    }

    @Test
    void testToString() {
        RollingCubesState testGameState = new RollingCubesState();

        int[][] testTray = new int[7][7];

        String url = getClass().getResource("/map/map.txt").toExternalForm().toString();
        String newUrl = "";

        for (int i = 5; i < url.length(); i++)
            newUrl = newUrl + url.charAt(i);

        try {
            Scanner sc = new Scanner(new File(newUrl));

            for (int i = 0; i < 7; i++)
                for (int j = 0; j < 7; j++)
                    testTray[i][j] = sc.nextInt();

            sc.close();
        } catch (FileNotFoundException e) {
        }

        String testString = "";
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                testString += testTray[i][j] + " ";
            }
            testString += "\n";
        }



        assertEquals(testString, testGameState.toString());
    }
}
