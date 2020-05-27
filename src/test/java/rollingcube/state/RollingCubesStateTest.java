package rollingcube.state;

import org.junit.jupiter.api.Test;

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
        RollingCubesState testGameState = new RollingCubesState();
        assertTrue(testGameState.isValidTray(testGameState.getTray()));

        testGameState.getTray()[6][6] = 6;
        assertThrows(IllegalArgumentException.class, () -> testGameState.isValidTray(testGameState.getTray()));
    }

    @Test
    void testInitTray() {
        RollingCubesState testGameState = new RollingCubesState();
        int[][] testTray = {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 6, 6, 6, 6, 6},
                {6, 6, 6, 7, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 0}
        };
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
    }

    @Test
    void testCanRollToDirection() {
        RollingCubesState testGameState = new RollingCubesState(new int[][] {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 7, 7, 6, 6},
                {6, 7, 6, 0, 7, 6, 6},
                {6, 6, 6, 6, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        });

        assertTrue(testGameState.canRollToDirection("LEFT"));
        assertTrue(testGameState.canRollToDirection("DOWN"));
        assertTrue(testGameState.canRollToDirection("RIGHT"));
        assertTrue(testGameState.canRollToDirection("UP"));
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

        int[][] expected = {
                {6, 6, 6, 6, 7, 6, 6},
                {6, 7, 7, 6, 6, 8, 7},
                {6, 6, 6, 6, 7, 7, 6},
                {6, 6, 6, 7, 7, 6, 6},
                {6, 7, 3, 6, 7, 6, 6},
                {6, 6, 6, 6, 7, 6, 7},
                {7, 6, 6, 6, 6, 6, 7}
        };

        assertArrayEquals(expected, testGameState.getTray());
    }

    @Test
    void testToString() {
        RollingCubesState testGameState = new RollingCubesState();

        assertEquals("6 6 6 6 7 6 6 \n"
                            + "6 7 7 6 6 8 7 \n"
                            + "6 6 6 6 7 7 6 \n"
                            + "6 6 6 6 7 6 6 \n"
                            + "6 7 6 6 6 6 6 \n"
                            + "6 6 6 7 7 6 7 \n"
                            + "7 6 6 6 6 6 0 \n", testGameState.toString());
    }
}
