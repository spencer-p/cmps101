/*
 * Graph.c
 * Author: Spencer Peterson
 * cruzid: spjpeter
 *
 * Private implementation of graph ADT
 */

#include "Graph.h"
#include <stdlib.h>
#include <string.h>

typedef struct GraphObj {
    int order;
    int size;
    List *adjacents;
    enum visibility *seen;
    int *parents;
    int *distance;
} GraphObj;

enum visibility { UNSEEN = 0, ADJACENT, SEEN };

void throw(const char *err_string);
void check_null(Graph G, const char *method_name);

/*** Constructors - Destructors ***/
Graph newGraph(int n) {
    Graph new;
    int i;

    // Create uninitialized graph
    new = malloc(sizeof(GraphObj));

    if (new != NULL) {

        // Create the uninitialized adjacency list
        new->order = n;
        new->adjacents = malloc(n * sizeof(List));

        if (new->adjacents != NULL) {

            // Initialize each adjacency list
            for (i = 0; i < n; i++) {
                new->adjacents[i] = newList();
            }

            // Done
            return new;
        }

        // TODO initialize like everything
    }

    throw("Failed to allocate new Graph");
    return NULL;
}

void freeGraph(Graph *pG) {
    int i;

    for (i = 0; i < getOrder(*pG); i++) {
        freeList(&(*pG)->adjacents[i]);
    }

    // TODO free a lot of other stuff

    free(pG);
}

/*** Access functions ***/
int getOrder(Graph G) {
    return G->order;
}

int getSize(Graph G) {
    return G->size;
}

int getSource(Graph G);
int getParent(Graph G, int u);
int getDist(Graph G, int u);
void getPath(List L, Graph G, int u);

/*** Manipulation procedures ***/
void makeNull(Graph G) {
    // TODO
    // Steal a whole bunch of stuff from freeGraph and newGraph
}

void addEdge(Graph G, int u, int v) {
    if (u < 1 || u > getOrder(G) || v < 1 || v > getOrder(G)) {
        throw("addEdge: vertex is not in graph order");
    }

    // An edge is two arcs
    addArc(G, u, v);
    addArc(G, v, u);

    // Add arc added 1 to size each, so remove one of those
    G->size--;
}

void addArc(Graph G, int u, int v) {
    if (u < 1 || u > getOrder(G) || v < 1 || v > getOrder(G)) {
        throw("addArc: vertex is not in graph order");
    }

    List uList = G->adjacents[u];

    // Increase size
    G->size++;

    // Try to insert sorted
    for (moveFront(uList); index(uList) != -1; moveNext(uList)) {
        if (get(uList) > v) {
            insertBefore(uList, v);
            return;
        }
    }

    // Didn't exit early, v must go at end
    append(uList, v);
}

void BFS(Graph G, int s) {
    List queue = NULL, currentEdges = NULL;
    int current = NIL, adj = NIL;

    if (s < 1 || s > getOrder(G)) {
        throw("BFS: vertex not in graph order");
    }

    // Allocate seen array
    if (G->seen != NULL) {
        free(G->seen);
        G->seen = NULL;
    }
    G->seen = calloc(getOrder(G), sizeof(enum visibility));

    // Allocate distance array
    if (G->distance != NULL) {
        free(G->distance);
        G->distance = NULL;
    }
    G->distance = malloc(getOrder(G)*sizeof(int));
    memset(G->distance, INF, getOrder(G)*sizeof(int));

    // Allocate parent array
    if (G->parents != NULL) {
        free(G->parents);
        G->parents = NULL;
    }
    G->parents = calloc(getOrder(G), sizeof(int));

    // Initialize the queue and begin looping
    queue = newList();
    append(queue, s);
    while (moveFront(queue), index(queue) != -1) {
        // Get current and mark it as seen
        current = get(queue);
        G->seen[current] = SEEN;

        for (currentEdges = G->adjacents[current], moveFront(currentEdges);
                index(currentEdges) != -1; moveNext(currentEdges)) {

            // Get adjacent vertex
            adj = get(currentEdges);

            // Update its distance
            if (G->distance[adj] == INF
                    || G->distance[adj] > G->distance[current]+1) {
                G->distance[adj] = G->distance[current] + 1;
            }

            // Set its parent
            G->parents[adj] = current;

            // Set it to adjacent
            G->seen[adj] = ADJACENT;

            // Add it to the queue
            append(queue, adj);
        }
    }

    freeList(&queue);
}

/*** Other operations ***/
void printGraph(FILE* out, Graph G);

/*** Private utils ***/

// Prints a generic error and quits
void throw(const char *err_string) {
    printf("Graph Error: %s\n", err_string);
    exit(1);
}

// If G is NULL, prints a null reference error and quits
void check_null(Graph G, const char *method_name) {
    if (G == NULL) {
        printf("Graph Error: calling %s on NULL reference\n", method_name);
        exit(1);
    }
}
