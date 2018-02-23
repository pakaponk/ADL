package ADL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ADLAgentTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testHashMapClone() {
        HashMap<String, Integer> original = new HashMap<>();
        original.put("Seq1", 10);
        original.put("Seq2", 20);

        HashMap<String, Integer> cloned = (HashMap<String, Integer>) original.clone();
        assertEquals(10, cloned.get("Seq1").intValue());
        assertEquals(20, cloned.get("Seq2").intValue());

        //Changes to original map should not affect the cloned
        original.put("Seq1", 20);
        original.put("Seq2", 30);
        assertEquals(20, original.get("Seq1").intValue());
        assertEquals(30, original.get("Seq2").intValue());

        assertEquals(10, cloned.get("Seq1").intValue());
        assertEquals(20, cloned.get("Seq2").intValue());
    }
}