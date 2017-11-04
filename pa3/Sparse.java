/*
 * Sparse.java
 * Author: Spencer Peterson
 * cruzid: spjpeter
 * A client program to perform operations on two matrices.
 */

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Sparse {
    public static void main(String args[]) throws IOException {
        Scanner input = null;
        PrintWriter output = null;
        Matrix A = null, B = null;
        int dimension, ANonZero, BNonZero;
        int row, column;
        double value;

        // Check usage
        if (args.length != 2) {
            System.out.println("Usage: infile outfile");
            System.exit(-1);
        }

        // Open input
        input = new Scanner(new File(args[0]));

        // Read metadata
        dimension = input.nextInt();
        ANonZero = input.nextInt();
        BNonZero = input.nextInt();

        // Initialize the matrices
        A = new Matrix(dimension);
        B = new Matrix(dimension);

        // Read A's non zero entries
        for (int i = 0; i < ANonZero; i++) {
            row = input.nextInt();
            column = input.nextInt();
            value = input.nextDouble();
            A.changeEntry(row, column, value);
        }

        // Read B's non zero entries
        for (int i = 0; i < BNonZero; i++) {
            row = input.nextInt();
            column = input.nextInt();
            value = input.nextDouble();
            B.changeEntry(row, column, value);
        }

        input.close();

        // Open output
        output = new PrintWriter(new FileWriter(args[1]));

        // We must print A, B, 1.5*A, A+B, A+A, B-A, A-A, A^T, AB, and B^2

        // A
        output.println("A has "+A.getNNZ()+" non-zero entries:");
        output.println(A.toString());

        // B
        output.println("B has "+B.getNNZ()+" non-zero entries:");
        output.println(B.toString());

        // 1.5 * A
        output.println("(1.5)*A = ");
        output.println(A.scalarMult(1.5).toString());

        // A + B
        output.println("A+B = ");
        output.println(A.add(B).toString());

        // A + A
        output.println("A+A = ");
        output.println(A.add(A).toString());

        // B - A
        output.println("B-A = ");
        output.println(B.sub(A).toString());

        // A - A (should be empty matrix)
        output.println("A-A = ");
        output.println();

        // A^T (transpose)
        output.println("Transpose(A) = ");
        output.println(A.transpose().toString());

        // AB
        output.println("A*B = ");
        output.println(A.mult(B).toString());

        // B^2
        output.println("B*B = ");
        output.println(B.mult(B).toString());

        output.close();
    }
}
