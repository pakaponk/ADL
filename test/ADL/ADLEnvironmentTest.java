package ADL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ADLEnvironmentTest {

    private ADLEnvironment environment;

    @BeforeEach
    void setUp() {
        environment = new ADLEnvironment("TestEnv", -10, 20, 100, 100, true);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testClone() {

        ADLEnvironment cloned = null;
        try {
            cloned = environment.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        environment.setPosition(10, -30);
        assertEquals(10, environment.getX());
        assertEquals(-30, environment.getY());

        assertEquals("TestEnv", cloned.name);
        assertEquals(-10, cloned.getX());
        assertEquals(20, cloned.getY());
        assertEquals(100, cloned.getWidth());
        assertEquals(100, cloned.getHeight());
        assertEquals(true, cloned.isHorizontalAlignment());
    }
}