package ADL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ADLBaseAgentTest {

    ADLBaseAgent baseAgent;

    @BeforeEach
    void setUp() {
        baseAgent = new ADLBaseAgent("IceMan");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testClone() {
        ADLBaseAgent cloned = null;
        try {
            cloned = baseAgent.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        baseAgent.setVelocity(5, -5);
        baseAgent.setPosition(10, 20);

        assertEquals(0, cloned.getVelocityX());
        assertEquals(0, cloned.getVelocityY());
        assertEquals(0, cloned.getX());
        assertEquals(0, cloned.getY());

        assertEquals(5, baseAgent.getVelocityX());
        assertEquals(-5, baseAgent.getVelocityY());
        assertEquals(10, baseAgent.getX());
        assertEquals(20, baseAgent.getY());

    }
}