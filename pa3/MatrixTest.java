/*
 * MatrixTest.java
 * Author: Spencer Peterson
 * cruzid: spjpeter
 * Unit tests for Matrix.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.Before;

public class MatrixTest {
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
        assertEquals("1: (1, 2.0) (3, 1.0)\n3: (2, 3.0)\n", m.toString());
    }

    @Test
    public void biggerMakeString() {
        // Try again with dimension increased by 1
        m = new Matrix(4);
        m.changeEntry(1, 1, 2);
        m.changeEntry(1, 3, 1);
        m.changeEntry(3, 2, 3);
        assertEquals(3, m.getNNZ());
        assertEquals("1: (1, 2.0) (3, 1.0)\n3: (2, 3.0)\n", m.toString());
    }

    @Test
    public void emptyMakeString() {
        // Try again with an empty matrix
        m = new Matrix(2);
        assertEquals("", m.toString());
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
        assertEquals("1: (1, 2.0) (3, 1.0)\n3: (2, 3.0)\n", n.toString());
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

        assertEquals("1: (1, 2.0) (3, 4.0)\n3: (2, 4.0)\n", o.toString());
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

        assertEquals("1: (1, 2.0) (3, -2.0)\n3: (2, 2.0)\n", o.toString());
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

        assertEquals("1: (1, 4.0) (3, 2.0)\n3: (2, 6.0)\n", n.toString());
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

        assertEquals("1: (1, 2.0)\n2: (3, 3.0)\n3: (1, 1.0)\n", n.toString());
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

        assertEquals("1: (1, 13.0) (3, 9.0)\n3: (2, 24.0) (3, 3.0)\n", o.toString());
    }

    /*
     * Tests from here on out are copied from the grading scripts,
     * for convienence of testing.
     */

    @Test
    public void changeEntryGetNNZ() {
        m = new Matrix(10);
        m.changeEntry(2, 1, 2);
        m.changeEntry(3, 1, 5);
        m.changeEntry(1, 2, 2);
        m.changeEntry(1, 3, 5);
        m.changeEntry(1, 1, 4);
        m.changeEntry(2, 2, 2);
        m.changeEntry(2, 5, 0);
        m.changeEntry(2, 3, 0);
        m.changeEntry(3, 3, 5);
        assertEquals(7, m.getNNZ());
    }

    @Test
    public void addGetNNZ() {
        Matrix A = new Matrix(10);
        Matrix B = new Matrix(10);
        A.changeEntry(1, 1, 4);
        A.changeEntry(1, 2, 2);
        A.changeEntry(1, 3, 0);
        A.changeEntry(2, 1, 2);
        A.changeEntry(3, 1, 0);
        A.changeEntry(2, 2, 2);
        A.changeEntry(3, 3, 0);
        Matrix C = A.add(A);
        assertEquals(4, C.getNNZ());
        assertEquals(4, A.getNNZ());
        B.changeEntry(1, 1, -4);
        B.changeEntry(1, 2, 0);
        B.changeEntry(2, 1, 0);
        B.changeEntry(2, 2, -2);
        B.changeEntry(2, 4, 2);
        B.changeEntry(3, 1, 0);
        B.changeEntry(3, 2, 2);
        B.changeEntry(7, 8, 5);
        C = A.add(B);
        assertEquals(5, B.getNNZ());
        assertEquals(5, C.getNNZ());
    }

    @Test
    public void subGetNNZ() {
        Matrix A = new Matrix(10);
        Matrix B = new Matrix(10);
        A.changeEntry(1, 1, -4);
        A.changeEntry(1, 2, -2);
        A.changeEntry(1, 3, 0);
        A.changeEntry(2, 5, 4);
        A.changeEntry(2, 1, -2);
        A.changeEntry(3, 1, 2);
        A.changeEntry(2, 2, -2);
        A.changeEntry(3, 3, 0);
        Matrix C = A.sub(A);
        assertEquals(0, C.getNNZ());
        assertEquals(6, A.getNNZ());
        B.changeEntry(1, 1, -4);
        B.changeEntry(1, 2, 0);
        B.changeEntry(2, 1, 0);
        B.changeEntry(2, 2, -2);
        B.changeEntry(2, 4, 2);
        B.changeEntry(3, 1, 2);
        B.changeEntry(3, 2, 2);
        B.changeEntry(7, 8, 5);
        C = A.sub(B);
        assertEquals(6, C.getNNZ());
    }

    @Test
    public void nonEmptyToString() {
        Matrix A = new Matrix(10);
        A.changeEntry(1, 1, 1);
        A.changeEntry(1, 2, 0);
        A.changeEntry(1, 3, 3.5);
        A.changeEntry(5, 7, 2.5);
        A.changeEntry(3, 4, 2);
        A.changeEntry(1, 1, 2);
        assertEquals("1: (1, 2.0) (3, 3.5)\n3: (4, 2.0)\n5: (7, 2.5)\n",
                A.toString());
    }

    @Test
    public void nonEmptyEquals() {
        Matrix A = new Matrix(10);
        Matrix B = new Matrix(15);
        A.changeEntry(1, 1, 1);
        B.changeEntry(1, 1, 1);
        assertFalse(A.equals(B));
        B = new Matrix(10);
        A.changeEntry(1, 1, 1);
        A.changeEntry(1, 3, 1);
        B.changeEntry(1, 1, 1);
        B.changeEntry(1, 3, 1);
        assertTrue(A.equals(B));
        A.changeEntry(1, 3, 0);
        assertFalse(A.equals(B));
        A.changeEntry(1, 1, 0);
        B.makeZero();
        A.changeEntry(10, 10, 10);
        B.changeEntry(10, 10, 10);
        assertTrue(A.equals(B));
        A = new Matrix(100);
        B = new Matrix(100);
        int valcount = 1;
        for (int j = 1; j <= 100; j++) {
            for (int k = 1; k <= 100; k++) {
                // hint: this is 1-10000 left-to-right row-by-row
                //             A.changeEntry(j, k, valcount++);
                //
            }
            B.changeEntry(j, j, 1); // hint: this is the identity matrix
        }
        Matrix C = A.scalarMult(2);
        assertTrue(C.equals(A.add(A)));
        C = A.scalarMult(-2);
        assertTrue(C.equals(A.sub(A).sub(A).sub(A)));
        C = A.mult(B);
        assertTrue(C.equals(A));
    }
}
