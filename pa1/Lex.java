/*
 * Lex.java
 *
 * Copyright (C) 2017, Spencer Peterson
 */

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Lex {
	public static void main(String args[]) throws IOException {
		Scanner input = null;
		PrintWriter output = null;
		List list = new List();
		ArrayList<String> lines = new ArrayList<String>();

		// Check for useage
		if (args.length != 2) {
			System.out.println("Usage: infile outfile");
			System.exit(-1);
		}

		// Setup input/output
		input = new Scanner(new File(args[0]));
		output = new PrintWriter(new FileWriter(args[1]));

		// Read in the input
		while (input.hasNextLine()) {
			lines.add(input.nextLine());
		}

		// Iterate through the lines
		for (int i = 0; i < lines.size(); i++) {

			// Start each iteration at the front
			list.moveFront();

			// Scrub the list to where the line belongs
			while (list.index() != -1 
					&& lines.get(i).compareTo(lines.get(list.get())) >= 0) {
				list.moveNext();
			}

			// If the index went off, then append the word
			if (list.index() == -1) {
				list.append(i);
			}

			// Otherwise insert it before the word we're at
			else {
				list.insertBefore(i);
			}
		}

		// Print it out
		for (int i : list) {
			output.println(lines.get(i));
		}

		// Clean up
		input.close();
		output.close();
	}
}
