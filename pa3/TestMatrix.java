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

    @Test
    public void makeZero() {
        m.changeEntry(1, 1, 2);
        m.changeEntry(1, 3, 1);
        m.changeEntry(3, 2, 3);
        assertEquals(3, m.getNNZ());

        m.makeZero();
        assertEquals(0, m.getNNZ());
    }

    @Test
    public void emptyCopy() {
        Matrix n = m.copy();
        assertEquals(0, n.getNNZ());
        assertEquals(0, m.getEntry(1, 1), 0);
    }

    @Test
    public void nonEmptyCopy() {
        m.changeEntry(1, 1, 2);
        m.changeEntry(1, 3, 1);
        m.changeEntry(3, 2, 3);

        Matrix n = m.copy();

        assertEquals(3, n.getNNZ());
        assertEquals("2.0 0 1.0\n0 0 0\n0 3.0 0\n", n.toString());
    }

    @Test
    public void add() {
        /*
         *  2 0 1   0 0 3
         *  0 0 0 + 0 0 0
         *  0 3 0   0 1 0
         */
        m.changeEntry(1, 1, 2);
        m.changeEntry(1, 3, 1);
        m.changeEntry(3, 2, 3);

        Matrix n = new Matrix(3);
        n.changeEntry(1, 3, 3);
        n.changeEntry(3, 2, 1);

        Matrix o = m.add(n);

        assertEquals("2.0 0 4.0\n0 0 0\n0 4.0 0\n", o.toString());
    }

    @Test
    public void sub() {
        /*
         *  2 0 1   0 0 3
         *  0 0 0 - 0 0 0
         *  0 3 0   0 1 0
         */
        m.changeEntry(1, 1, 2);
        m.changeEntry(1, 3, 1);
        m.changeEntry(3, 2, 3);

        Matrix n = new Matrix(3);
        n.changeEntry(1, 3, 3);
        n.changeEntry(3, 2, 1);

        Matrix o = m.sub(n);

        assertEquals("2.0 0 -2.0\n0 0 0\n0 2.0 0\n", o.toString());
    }

    @Test
    public void scalarMult() {
        /*
         *  2 0 1
         *  0 0 0 x 2
         *  0 3 0
         */
        m.changeEntry(1, 1, 2);
        m.changeEntry(1, 3, 1);
        m.changeEntry(3, 2, 3);

        Matrix n = m.scalarMult(2);

        assertEquals("4.0 0 2.0\n0 0 0\n0 6.0 0\n", n.toString());
    }

    @Test
    public void tranpose() {
        /*
         *  2 0 1    2 0 0
         *  0 0 0 -> 0 0 3
         *  0 3 0    1 0 0
         */
        m.changeEntry(1, 1, 2);
        m.changeEntry(1, 3, 1);
        m.changeEntry(3, 2, 3);

        Matrix n = m.transpose();

        assertEquals("2.0 0 0\n0 0 3.0\n1.0 0 0\n", n.toString());
    }

    @Test
    public void multiply() {
        /*
         *  2 0 1   4 0 0   13 0  9
         *  0 0 0 x 0 8 1 = 0  0  0
         *  0 3 0   5 0 9   0  24 3
         */
        m.changeEntry(1, 1, 2);
        m.changeEntry(1, 3, 1);
        m.changeEntry(3, 2, 3);

        Matrix n = new Matrix(m.getSize());
        n.changeEntry(1, 1, 4);
        n.changeEntry(2, 2, 8);
        n.changeEntry(2, 3, 1);
        n.changeEntry(3, 1, 5);
        n.changeEntry(3, 3, 9);

        Matrix o = m.mult(n);

        assertEquals("13.0 0 9.0\n0 0 0\n0 24.0 3.0\n", o.toString());
    }
}
