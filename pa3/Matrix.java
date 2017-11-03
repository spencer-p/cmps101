/*
 * Matrix.java
 * Implements a fast, sparse matrix object
 * Copyright (C) 2017 Spencer Peterson
 * cruzid: spjpeter
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
        return o == this;
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
    void changeEntry(int i, int j, double x) {
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
                if (e.column > j) {
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
            r.append(new Entry(i, j, x));
        }
    }

    /*
     * Returns a new matrix that equals this matrix * x
     */
    Matrix scalarMult(double x) {
        return (new Matrix(getSize())).addScalarMultiple(this, x);
    }

    /*
     * Returns a new matrix, the sum of this and M
     */
    Matrix add(Matrix M) {
        if (getSize() != M.getSize()) {
            throw new IndexOutOfBoundsException("Matrix: add() dimensions are inequal");
        }
        return addScalarMultiple(M, 1);
    }

    /*
     * Returns a new matrix this minus M
     */
    Matrix sub(Matrix M) {
        if (getSize() != M.getSize()) {
            throw new IndexOutOfBoundsException("Matrix: sub() dimensions are inequal");
        }
        return addScalarMultiple(M, -1);
    }

    /*
     * Returns this matrix transposed (rotated)
     */
    Matrix transpose() {
        return new Matrix(0);
    }

    /*
     * Returns a matrix that is the product of this and M
     */
    Matrix mult(Matrix M) {
        if (getSize() != M.getSize()) {
            throw new IndexOutOfBoundsException("Matrix: mult() dimensions are inequal");
        }
        return new Matrix(0);
    }

    /*
     * Overrides Object's toString
     */
    public String toString() {
        int lastRow = 0;
        int lastColumn = 0;

        String matrixString = "";

        // Iterate through rows
        for (rows.moveFront(); rows.index() != -1; rows.moveNext()) {
            // Determine which row we're looking at (nextRow)
            List r = (List) rows.get();
            r.moveFront();
            int nextRow = ((Entry) r.get()).row;

            // If we skipped some rows, throw in some zero rows
            if (nextRow > lastRow+1) {
                for (lastRow++; lastRow < nextRow; lastRow++)  {
                    // Print out a row of zeroes
                    for (int i = 1; i <= getSize(); i++) {
                        matrixString += '0';
                        if (i != getSize()) {
                            matrixString += ' ';
                        }
                    }
                    matrixString += '\n';
                }
            }

            // Iterate through each element in this row
            for (r.moveFront(); r.index() != -1; r.moveNext()) {

                // If we skipped some columns, insert some zero entries
                if (((Entry) r.get()).column != lastColumn+1) {
                    // Fill zeroes
                    int nextCol = ((Entry) r.get()).column;
                    for (lastColumn++; lastColumn < nextCol; lastColumn++) {
                        matrixString += '0';
                        if (lastColumn != nextCol) {
                            matrixString += ' ';
                        }
                    }
                }

                // Add the entry and a space if necessary
                matrixString += ((Entry) r.get()).value;
                if (((Entry) r.get()).column != getSize()) {
                    matrixString += ' ';
                }

                // Update the last column placed
                lastColumn = ((Entry) r.get()).column;
            }

            // If the last column didn't reach the end, add some zero entries
            if (lastColumn < getSize()) {
                for (lastColumn++; lastColumn <= getSize(); lastColumn++) {
                    matrixString += '0';
                    if (lastColumn != getSize()) {
                        matrixString += ' ';
                    }
                }
            }

            // Update last column and row, append new line
            lastColumn = 0;
            lastRow = nextRow;
            matrixString += '\n';
        }

        // If the last column didn't reach the end, add some zero rows
        if (lastColumn < getSize()) {
            for (; lastRow < getSize(); lastRow++)  {
                // Print out a row of zeroes
                for (int i = 1; i <= getSize(); i++) {
                    matrixString += '0';
                    if (i != getSize()) {
                        matrixString += ' ';
                    }
                }
                matrixString += '\n';
            }
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
        rows.moveFront();
        while (rows.index() != -1) {

            // Check if this row is past or equal
            // First we scrub to its front, then check the row on element 1
            List r = (List) rows.get();
            r.moveFront();
            if (((Entry) r.get()).row >= i) {
                break;
            }

            rows.moveNext();
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
        rows.moveFront();
        while (rows.index() != -1) {
            List r = (List) rows.get();
            r.moveFront();
            if (((Entry) r.get()).row >= i) {
                break;
            }

            rows.moveNext();
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
        rows.moveFront();
        while (rows.index() != -1) {
            if ((List) rows.get() == rowToDelete) {
                rows.delete();
                return;
            }
            rows.moveNext();
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
                    newRow.append(e);
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
                while (thisRow.index() != -1 && BRow.index() != -1) {
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
                        newRow.append(e);
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
                        newRow.append(e);
                        thisRow.moveNext();
                        BRow.moveNext();
                    }
                }
                B.rows.moveNext();
                this.rows.moveNext();
            }

            // Insert the new row
            C.rows.append(newRow);
        }

        return C;
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
    }
}
