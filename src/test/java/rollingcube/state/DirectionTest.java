package rollingcube.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void testOf() {
        assertEquals(Direction.UP, Direction.of(1, 0));
        assertEquals(Direction.RIGHT, Direction.of(0, -1));
        assertEquals(Direction.DOWN, Direction.of(-1, 0));
        assertEquals(Direction.LEFT, Direction.of(0, 1));
        assertThrows(IllegalArgumentException.class, () -> Direction.of(1, 1));
    }

    @Test
    void testToString() {
        assertEquals("LEFT", Direction.LEFT.toString());
        assertEquals("DOWN", Direction.DOWN.toString());
        assertEquals("RIGHT", Direction.RIGHT.toString());
        assertEquals("UP", Direction.UP.toString());
    }
}