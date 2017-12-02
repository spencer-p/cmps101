/*
 * FindComponents.c
 * Author: Spencer Peterson
 * cruzid: spjpeter
 * pa5
 *
 * A client program to find strongly connected components of graphs.
 */

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#include "Graph.h"
#include "List.h"

int main(int argc, char **argv) {
	FILE *input = NULL, *output = NULL;
	Graph G = NULL, Gt = NULL;
	List S = NULL;
	List *trees;
	int u, v, order, num_trees = 0, lastcc, *cc;

	// Check usage
	if (argc != 3) {
		printf("Usage: %s infile outfile\n", argv[0]);
		exit(1);
	}

	// Open files
	input = fopen(argv[1], "r");
	output = fopen(argv[2], "w");
	if (input == NULL || output == NULL) {
		printf("Could not open file(s)");
		exit(1);
	}

	// Set up graph
	fscanf(input, "%d\n", &order);
	G = newGraph(order);

	// Read in edges
	while (fscanf(input, "%d %d\n", &u, &v) != EOF && u != 0 && v != 0) {
		addArc(G, u, v);
	}

	// Create Gt
	Gt = transpose(G);

	// Create S
	S = newList();
	for (int i = 1; i <= order; i++) {
		append(S, i);
	}

	// Run DFS on both
	DFS(G, S);
	DFS(Gt, S);

	/*
	 * Find strongly connected components.
	 * First count # of trees.
	 * Then assign a CC for each vertex based on its parent's CC.
	 */

	// Allocate connected component assignment array
	cc = calloc(getOrder(Gt)+1, sizeof(int));

	// First pass - identify # of trees and roots
	for (int i = 1; i <= getOrder(Gt); i++) {
		if (getParent(Gt, i) == NIL) {
			num_trees++;
		}
	}

	// Allocate List array
	trees = calloc(num_trees, sizeof(List));

	// Find each tree
	lastcc = num_trees;
	for (moveFront(S); index(S) != -1; moveNext(S)) {
		u = get(S);
		if (getParent(Gt, u) != NIL) {
			// If it has a parent, take that parent's CC.
			cc[u] = cc[getParent(Gt, u)];
		}
		else {
			// If no parent, choose a new CC
			cc[u] = --lastcc;
		}

		// Put it on the correct list
		if (trees[cc[u]] == NULL) {
			trees[cc[u]] = newList();
		}
		append(trees[cc[u]], u);
	}

	// Print the graph
	fprintf(output, "Adjacency list representation of G:\n");
	printGraph(output, G);
	fprintf(output, "\n\n");

	// Print the connected components
	fprintf(output, "G contains %d strongly connected components:\n", num_trees);
	for (int i = 0; i < num_trees; i++) {
		fprintf(output, "Component %d: ", i+1);
		printList(output, trees[i]);
		fprintf(output, "\n");
	}

	// Clean up
	free(cc);
	for (int i = 0; i < num_trees; i++) {
		freeList(&trees[i]);
	}
	free(trees);
	freeGraph(&G);
	freeGraph(&Gt);
	freeList(&S);
	fclose(input);
	fclose(output);
}
