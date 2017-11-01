import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.Before;

public class TestMatrix {
    Matrix m;

    @Before
    public void setup() {
        this.m = new Matrix(3);
    }

    @Test
    public void initialSize() {
        assertEquals(3, m.getSize());
    }

    @Test
    public void initialNNZ() {
        assertEquals(0, m.getNNZ());
    }

    @Test
    public void getEntry() {
        m.changeEntry(1, 1, 1);
        assertEquals(1, m.getEntry(1, 1), 0);
        assertEquals(0, m.getEntry(1, 2), 0);
        assertEquals(0, m.getEntry(2, 1), 0);
    }

    @Test
    public void changeEntryZeroToPositive() {
        m.changeEntry(1, 1, 1);
        assertEquals(1, m.getNNZ());
        assertEquals(1, m.getEntry(1, 1), 0);
    }

    @Test
    public void changeEntryPostiveToPositive() {
        m.changeEntry(1, 1, 1);
        m.changeEntry(1, 1, 2);
        assertEquals(1, m.getNNZ());
        assertEquals(2, m.getEntry(1, 1), 0);
    }

    @Test
    public void changeEntryNegativeToNegative() {
        m.changeEntry(1, 1, 0);
        assertEquals(0, m.getNNZ());
        assertEquals(0, m.getEntry(1, 1), 0);
    }

    @Test
    public void changeEntryPositiveToNegative() {
        m.changeEntry(1, 1, 2);
        m.changeEntry(1, 1, 0);
        assertEquals(0, m.getNNZ());
        assertEquals(0, m.getEntry(1, 1), 0);
    }
}
