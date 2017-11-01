/*
 * Matrix.java
 * Implements a fast, sparse matrix object
 * Copyright (C) 2017 Spencer Peterson
 * cruzid: spjpeter
 */

public class Matrix {
    List rows;
    int n;

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
        return new Matrix(0);
    }

    /*
     * Sets the entry at (i, j) to x
     */
    void changeEntry(int i, int j, double x) {
        // Check i, j is in the matrix first
        if (i > getSize() || j > getSize()) {
            throw new IndexOutOfBoundsException("Matrix: changeEntry() outside of bounds");
        }

    }

    /*
     * Returns a new matrix that equals this matrix * x
     */
    Matrix scalarMult(double x) {
        return new Matrix(0);
    }

    /*
     * Returns a new matrix, the sum of this and M
     */
    Matrix add(Matrix M) {
        if (getSize() != M.getSize()) {
            throw new IndexOutOfBoundsException("Matrix: add() dimensions are inequal");
        }
        return new Matrix(0);
    }

    /*
     * Returns a new matrix this minus M
     */
    Matrix sub(Matrix M) {
        if (getSize() != M.getSize()) {
            throw new IndexOutOfBoundsException("Matrix: sub() dimensions are inequal");
        }
        return new Matrix(0);
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
        return "";
    }
}
