/*
 * Matrix.java
 * Author: Spencer Peterson
 * cruzid: spjpeter
 * Implements a fast, sparse matrix object
 */

public class Matrix {
    private List rows;
    private int n;

    /*
     * Constructs an nxn matrix.
     */
    public Matrix(int n) {
        this.n = n;
        this.rows = new List();
    }

    /*
     * Returns dimension of matrix (n)
     */
    public int getSize() {
        return n;
    }

    /*
     * Returns number of non zero entries in the matrix
     */
    public int getNNZ() {
        int entries = 0;
        for (rows.moveFront(); rows.index() != -1; rows.moveNext()) {
            entries += ((List) rows.get()).length();
        }
        return entries;
    }

    /*
     * Get method for (row, col) of Matrix - for unit tests
     */
    public double getEntry(int row, int column) {
        if (row > getSize() || column > getSize()) {
            throw new IndexOutOfBoundsException("Matrix: getEntry() out of bounds");
        }
        List r = getRow(row);
        if (r != null) {
            for (r.moveFront(); r.index() != -1; r.moveNext()) {
                if (((Entry) r.get()).column == column) {
                    return ((Entry) r.get()).value;
                }
            }
        }
        return 0;
    }

    /*
     * Strict equality
     */
    public boolean equals(Object o) {
        // If not the same class, they are not equal
        if (this.getClass() != o.getClass()) {
            return false;
        }

        // Literally same object, must be equal
        if (o == this) {
            return true;
        }

        // Cast o to a matrix
        Matrix M = (Matrix) o;

        // Matrices of different sizes cannot be equal
        if (this.getSize() != M.getSize()) {
            return false;
        }

        // Matrices with different counts of NNZ are unequal
        if (this.getNNZ() != M.getNNZ()) {
            return false;
        }

        // If the row space is different they cannot be equal
        if (this.rows.length() != M.rows.length()) {
            return false;
        }

        // Loop through entries
        for (this.rows.moveFront(), M.rows.moveFront();
                this.rows.index() != -1 && M.rows.index() != -1;
                this.rows.moveNext(), M.rows.moveNext()) {

            List thisRow = (List) this.rows.get();
            List MRow = (List) M.rows.get();

            // Unequal amounts of NNZ, fail
            if (thisRow.length() != MRow.length()) {
                return false;
            }

            for (thisRow.moveFront(), MRow.moveFront();
                    thisRow.index() != -1 && MRow.index() != -1;
                    thisRow.moveNext(), MRow.moveNext()) {

                Entry thisEntry = (Entry) thisRow.get();
                Entry MEntry = (Entry) MRow.get();

                // If these entries are not equal then fail
                if (!thisEntry.equals(MEntry)) {
                    return false;
                }
            }

        }

        // They must be equal
        return true;
    }

    /*
     * Sets all entries in the matrix to 0
     */
    public void makeZero() {
        rows.clear();
    }

    /*
     * Returns a new matrix with entries identical to this one
     */
    public Matrix copy() {
        // Initialize empty matrix
        Matrix newMatrix = new Matrix(getSize());

        // Loop through this's rows
        for (rows.moveFront(); rows.index() != -1; rows.moveNext()) {
            // Get current row, create new current row
            List r = (List) rows.get();
            List newRow = new List();

            // Fill the newRow with the old row's entries
            for (r.moveFront(); r.index() != -1; r.moveNext()) {
                newRow.append(((Entry) r.get()).copy());
            }

            // Add the new row to the new matrix
            newMatrix.rows.append(newRow);
        }

        return newMatrix;
    }

    /*
     * Sets the entry at (i, j) to x
     */
    public void changeEntry(int i, int j, double x) {
        // Check i, j is in the matrix first
        if (i > getSize() || j > getSize()) {
            throw new IndexOutOfBoundsException("Matrix: changeEntry() outside of bounds");
        }

        List r = getRow(i);
        if (r == null && x != 0) {
            // If the row doesn't exist, create it (as long as the value != 0)
            r = createRow(i);
            r.append(new Entry(i, j, x));
            return;
        }
        else if (r != null) {
            for (r.moveFront(); r.index() != -1; r.moveNext()) {
                Entry e = (Entry) r.get();
                if (e.column > j && x != 0) {
                    r.insertBefore(new Entry(i, j, x));
                    return;
                }
                else if (e.column == j) {
                    if (x != 0) {
                        e.value = x;
                        return;
                    }
                    else {
                        // Remove the element
                        r.delete();
                        // Remove list if necessary
                        if (r.length() == 0) {
                            deleteRow(r);
                        }
                        return;
                    }
                }
            }
            // Scrubbed through entire row, therefore the entry must go at end
            if (x != 0) {
                r.append(new Entry(i, j, x));
            }
        }
    }

    /*
     * Returns a new matrix that equals this matrix * x
     */
    public Matrix scalarMult(double x) {
        // This (ab)uses the addScalar method by creating a zero matrix and
        // adding this matrix times x to it. The cost of creating a zero matrix
        // is O(1), so no time is lost by this method.
        return (new Matrix(getSize())).addScalarMultiple(this, x);
    }

    /*
     * Returns a new matrix, the sum of this and M
     */
    public Matrix add(Matrix M) {
        if (getSize() != M.getSize()) {
            throw new IndexOutOfBoundsException("Matrix: add() dimensions are inequal");
        }
        if (M == this) {
            // To avoid List issues
            M = M.copy();
        }
        return addScalarMultiple(M, 1);
    }

    /*
     * Returns a new matrix this minus M
     */
    public Matrix sub(Matrix M) {
        if (getSize() != M.getSize()) {
            throw new IndexOutOfBoundsException("Matrix: sub() dimensions are inequal");
        }
        if (M == this) {
            // To avoid List issues
            M = M.copy();
        }
        return addScalarMultiple(M, -1);
    }

    /*
     * Returns this matrix transposed (rotated)
     */
    public Matrix transpose() {
        Matrix transposed = new Matrix(getSize());

        // Naive use of changeEntry, may not be O(n + a)
        for (rows.moveFront(); rows.index() != -1; rows.moveNext()) {
            List r = (List) rows.get();
            for (r.moveFront(); r.index() != -1; r.moveNext()) {
                Entry e = (Entry) r.get();
                transposed.changeEntry(e.column, e.row, e.value);
            }
        }

        return transposed;
    }

    /*
     * Returns a matrix that is the product of this and M
     */
    public Matrix mult(Matrix M) {
        if (getSize() != M.getSize()) {
            throw new IndexOutOfBoundsException("Matrix: mult() dimensions are inequal");
        }
        if (M == this) {
            // To avoid List issues
            M = M.copy();
        }

        Matrix newMatrix = new Matrix(getSize());

        // First rotate M so that its columns are now rows
        M = M.transpose();

        // Loop through this's rows
        this.rows.moveFront();
        for (this.rows.moveFront(); this.rows.index() != -1; this.rows.moveNext()) {

            // Get this row and its index
            List thisRow = (List) this.rows.get();
            thisRow.moveFront();
            int thisRowNumber = ((Entry) thisRow.get()).row;

            // Create new row for new matrix
            List newRow = new List();

            // Loop through M's rows (actually columns)
            for (M.rows.moveFront(); M.rows.index() != -1; M.rows.moveNext()) {
                List MRow = (List) M.rows.get();
                MRow.moveFront();
                int MRowNumber = ((Entry) MRow.get()).row;

                // Do the dot product and insert it (if it's nonzero)
                double newVal = dotProduct(thisRow, MRow);
                if (newVal != 0) {
                    newRow.append(new Entry(thisRowNumber, MRowNumber, newVal));
                }
            }

            // Insert the new row
            if (newRow.length() > 0) {
                newMatrix.rows.append(newRow);
            }
        }

        return newMatrix;
    }

    /*
     * Overrides Object's toString
     */
    public String toString() {
        String matrixString = "";

        // Iterate through rows
        for (rows.moveFront(); rows.index() != -1; rows.moveNext()) {
            // Determine which row we're looking at (rowNum)
            List r = (List) rows.get();
            r.moveFront();
            int rowNum = ((Entry) r.get()).row;

            matrixString += rowNum + ":";

            // Iterate through each element in this row
            for (r.moveFront(); r.index() != -1; r.moveNext()) {
                // Add the entry and a space if necessary
                Entry e = (Entry) r.get();
                matrixString += " (" + e.column + ", " + e.value + ")";
            }

            // append new line
            matrixString += '\n';
        }

        return matrixString;
    }

    /*
     * Get the ith row of the matrix.
     * If it does not exist, returns null.
     */
    private List getRow(int i) {
        // Scrub to the correct position
        // This while either go to 1. The end of the list, or
        // 2. A row equal to or past row i
        for (rows.moveFront(); rows.index() != -1; rows.moveNext()) {

            // Check if this row is past or equal
            // First we scrub to its front, then check the row on element 1
            List r = (List) rows.get();
            r.moveFront();
            if (((Entry) r.get()).row >= i) {
                break;
            }
        }

        // Determine what we found
        if (rows.index() == -1 || ((Entry)((List) rows.get()).get()).row > i) {
            // Off the end or the row wasn't in there
            return null;
        }
        else {
            // rows should be scrubbed exactly to the correct row
            return (List) rows.get();
        }
    }

    /*
     * Creates the new row at row i.
     * Returns the new row for immediate use.
     * Assumes that row i does not already exist.
     */
    private List createRow(int i) {
        // Follows a similar pattern to getRow().
        for (rows.moveFront(); rows.index() != -1; rows.moveNext()) {
            List r = (List) rows.get();
            r.moveFront();
            if (((Entry) r.get()).row >= i) {
                break;
            }
        }

        // New list to return
        List newRow = new List();

        if (rows.index() == -1) {
            // If we ran off the edge, put it there
            rows.append(newRow);
        }
        else {
            // The new list goes right before the current cursor
            rows.insertBefore(newRow);
        }

        return newRow;
    }

    /*
     * Deletes a row if it exists.
     */
    private void deleteRow(List rowToDelete) {
        // Follows a similar pattern to getRow().
        for (rows.moveFront(); rows.index() != -1; rows.moveNext()) {
            if ((List) rows.get() == rowToDelete) {
                rows.delete();
                return;
            }
        }
    }

    /*
     * Adds matrix A to B*x.
     */
    private Matrix addScalarMultiple(Matrix B, double x) {
        // The sizes of this and Matrix B *will* be checked beforehand
        Matrix C = new Matrix(getSize());

        // Iterate through both matrices' rows
        this.rows.moveFront();
        B.rows.moveFront();
        while (B.rows.index() != -1 || this.rows.index() != -1) {

            // First we'll identify the row number of each matrix,
            // if it still has a row (otherwise -1)
            int BNextRow = -1, thisNextRow = -1;
            List thisRow = null, BRow = null;
            if (B.rows.index() != -1) {
                BRow = (List) B.rows.get();
                BRow.moveFront();
                BNextRow = ((Entry) BRow.get()).row;
            }
            if (this.rows.index() != -1) {
                thisRow = (List) this.rows.get();
                thisRow.moveFront();
                thisNextRow = ((Entry) thisRow.get()).row;
            }

            // Empty new row to be filled
            List newRow = new List();

            if (thisRow == null || (BNextRow < thisNextRow && BRow != null)) {
                // Case 1: Adding a zero row to a row from B
                for (BRow.moveFront(); BRow.index() != -1; BRow.moveNext()) {
                    Entry e = ((Entry) BRow.get()).copy();
                    e.value *= x;
                    if (e.value != 0) {
                        newRow.append(e);
                    }
                }
                B.rows.moveNext();
            }
            else if (BRow == null || (thisNextRow < BNextRow && thisRow != null)) {
                // Case 2: Adding a zero row to a row from this
                for (thisRow.moveFront(); thisRow.index() != -1; thisRow.moveNext()) {
                    newRow.append(((Entry) thisRow.get()).copy());
                }
                this.rows.moveNext();
            }
            else /* thisRow == BRow */ {
                // Case 3: Adding nonzero rows
                while (thisRow.index() != -1 || BRow.index() != -1) {
                    // Get entries
                    Entry BEntry = null, thisEntry = null;
                    if (BRow.index() != -1) {
                        // pull from B
                        BEntry = (Entry) BRow.get();
                    }
                    if (thisRow.index() != -1) {
                        // pull from this
                        thisEntry = (Entry) thisRow.get();
                    }

                    if (thisEntry == null || (BEntry != null && BEntry.column < thisEntry.column)) {
                        // Case 1: Take Entry from B
                        Entry e = ((Entry) BRow.get()).copy();
                        e.value *= x;
                        if (e.value != 0) {
                            newRow.append(e);
                        }
                        BRow.moveNext();
                    }
                    else if (BEntry == null || (thisEntry != null && thisEntry.column < BEntry.column)) {
                        // Case 2: Take Entry from this
                        newRow.append(((Entry) thisRow.get()).copy());
                        thisRow.moveNext();
                    }
                    else /* thisEntry.column == BEntry.column */ {
                        // Case 3: The entries must be added
                        Entry e = ((Entry) thisRow.get()).copy();
                        e.value += x * ((Entry) BRow.get()).value;
                        if (e.value != 0) {
                            newRow.append(e);
                        }
                        thisRow.moveNext();
                        BRow.moveNext();
                    }
                }
                B.rows.moveNext();
                this.rows.moveNext();
            }

            // Insert the new row
            if (newRow.length() > 0) {
                C.rows.append(newRow);
            }
        }

        return C;
    }

    private double dotProduct(List A, List B) {
        double product = 0;

        A.moveFront();
        B.moveFront();
        while (A.index() != -1 && B.index() != -1) {
            Entry Ae = (Entry) A.get();
            Entry Be = (Entry) B.get();
            if (Ae.column == Be.column) {
                product += Ae.value * Be.value;
                A.moveNext();
                B.moveNext();
            }
            else if (Ae.column < Be.column) {
                A.moveNext();
            }
            else /* Be.column < Ae.column */ {
                B.moveNext();
            }
        }

        return product;
    }

    private class Entry {
        int row, column;
        double value;
        Entry(int row, int column, double value) {
            this.row = row;
            this.column = column;
            this.value = value;
        }

        Entry copy() {
            return new Entry(this.row, this.column, this.value);
        }

        boolean equals(Entry e) {
            return this.row == e.row
                && this.column == e.column
                && this.value == e.value;
        }
    }
}
