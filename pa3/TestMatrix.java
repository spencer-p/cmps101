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

    @Test
    public void changeEntryRowAppend() {
        m.changeEntry(1, 1, 2);
        m.changeEntry(1, 3, 1);
        assertEquals(2, m.getNNZ());
        assertEquals(2, m.getEntry(1, 1), 0);
        assertEquals(1, m.getEntry(1, 3), 0);
    }

    @Test
    public void changeEntryRowPrepend() {
        m.changeEntry(1, 3, 1);
        m.changeEntry(1, 1, 2);
        assertEquals(2, m.getNNZ());
        assertEquals(2, m.getEntry(1, 1), 0);
        assertEquals(1, m.getEntry(1, 3), 0);
    }

    @Test
    public void makeString() {
        m.changeEntry(1, 1, 2);
        m.changeEntry(1, 3, 1);
        m.changeEntry(3, 2, 3);
        assertEquals(3, m.getNNZ());
        assertEquals("2.0 0 1.0\n0 0 0\n0 3.0 0\n", m.toString());

        // Try again with dimension increased by 1
        m = new Matrix(4);
        m.changeEntry(1, 1, 2);
        m.changeEntry(1, 3, 1);
        m.changeEntry(3, 2, 3);
        assertEquals(3, m.getNNZ());
        assertEquals("2.0 0 1.0 0\n0 0 0 0\n0 3.0 0 0\n0 0 0 0\n",
                m.toString());

        // Try again with an empty matrix
        m = new Matrix(2);
        assertEquals("0 0\n0 0\n", m.toString());
    }
}
