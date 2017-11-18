/*
 * FindPath.c
 * Author: Spencer Peterson
 * cruzid: spjpeter
 *
 * A client to find paths using Graph.
 */

#include <stdlib.h>
#include <stdio.h>

#include "Graph.h"
#include "List.h"

int main(int argc, char **argv) {
    FILE *input = NULL, *output = NULL;
    Graph g = NULL;
    List path = NULL;
    int u, v, order;

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
    g = newGraph(order);

    // Read in edges
    while (fscanf(input, "%d %d\n", &u, &v) != EOF && u != 0 && v != 0) {
        addEdge(g, u, v);
    }

    // Print the graph
    printGraph(output, g);
    fprintf(output, "\n\n");

    // Perform searches
    while (fscanf(input, "%d %d\n", &u, &v) != EOF && u != 0 && v != 0) {
        BFS(g, u);
        fprintf(output, "The distance from %d to %d is ", u, v);

        if (getDist(g, v) == INF) {
            // If distance is infinite, say so and mark the path as nonexistant
            fprintf(output, "infinity\n");
            fprintf(output, "No %d-%d path exists", u, v);
        }
        else {
            // Otherwise print out everything as normal
            fprintf(output, "%d\n", getDist(g, v));
            path = newList();
            getPath(path, g, v);
            fprintf(output, "A shortest %d-%d path is: ", u, v);
            printList(output, path);
            freeList(&path);
        }
        fprintf(output, "\n\n");
    }

    // Clean up
    freeGraph(&g);
    fclose(input);
    fclose(output);

    return 0;
}
