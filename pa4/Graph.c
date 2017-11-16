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
    int bfs_source;
    List *adjacents;
    enum visibility *seen;
    int *parents;
    int *distance;
} GraphObj;

enum visibility { UNSEEN = 0, ADJACENT, SEEN };

void initInside(Graph G, int n);
void freeInside(Graph G);
void graph_throw(const char *err_string);
void graph_check_null(Graph G, const char *method_name);

/*** Constructors - Destructors ***/
Graph newGraph(int n) {
    Graph new;

    // Create uninitialized graph
    new = malloc(sizeof(GraphObj));

    initInside(new, n);

    // Done
    return new;
}

void freeGraph(Graph *pG) {
    freeInside(*pG);
    free(*pG);
    *pG = NULL;
}

/*** Access functions ***/
int getOrder(Graph G) {
    return G->order;
}

int getSize(Graph G) {
    return G->size;
}

int getSource(Graph G) {
    return G->bfs_source;
}

int getParent(Graph G, int u) {
    if (u < 1 || u > getOrder(G)) {
        graph_throw("getParent: vertex not in graph");
    }
    if (G->bfs_source == NIL) {
        graph_throw("getParent: BFS not yet run");
    }

    return G->parents[u];
}

int getDist(Graph G, int u) {
    if (u < 1 || u > getOrder(G)) {
        graph_throw("getDist: vertex not in graph");
    }
    if (G->bfs_source == NIL) {
        graph_throw("getDist: BFS not yet run");
    }

    return G->distance[u];
}

void getPath(List L, Graph G, int u) {
    if (u < 1 || u > getOrder(G)) {
        graph_throw("getPath: vertex not in graph");
    }
    if (G->bfs_source == NIL) {
        graph_throw("getPath: BFS not yet run");
    }

    // The spec says to append the path to the List, which we'll do by moving
    // to the back once and repeatedly inserting after, since the parent tree
    // is reversed.
    moveBack(L);
    insertAfter(L, u);
    for (int walk = G->parents[u]; walk != NIL; walk = G->parents[walk]) {
        insertAfter(L, walk);
    }
}

/*** Manipulation procedures ***/
void makeNull(Graph G) {
    int order = getOrder(G);
    freeInside(G);
    initInside(G, order);
}

void addEdge(Graph G, int u, int v) {
    if (u < 1 || u > getOrder(G) || v < 1 || v > getOrder(G)) {
        graph_throw("addEdge: vertex is not in graph order");
    }

    // An edge is two arcs
    addArc(G, u, v);
    addArc(G, v, u);

    // Add arc added 1 to size each, so remove one of those
    G->size--;
}

void addArc(Graph G, int u, int v) {
    if (u < 1 || u > getOrder(G) || v < 1 || v > getOrder(G)) {
        graph_throw("addArc: vertex is not in graph order");
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
        graph_throw("BFS: vertex not in graph order");
    }

    // Set source
    G->bfs_source = s;

    // Allocate seen array
    if (G->seen != NULL) {
        free(G->seen);
        G->seen = NULL;
    }
    G->seen = calloc(getOrder(G)+1, sizeof(enum visibility));

    // Allocate distance array
    if (G->distance != NULL) {
        free(G->distance);
        G->distance = NULL;
    }
    G->distance = malloc((getOrder(G)+1)*sizeof(int));
    memset(G->distance, INF, (getOrder(G)+1)*sizeof(int));
    G->distance[s] = 0;

    // Allocate parent array
    if (G->parents != NULL) {
        free(G->parents);
        G->parents = NULL;
    }
    G->parents = calloc((getOrder(G)+1), sizeof(int));

    // Initialize the queue and begin looping
    queue = newList();
    append(queue, s);
    while (moveFront(queue), index(queue) != -1) {
        // Get current and mark it as seen
        current = get(queue);
        delete(queue);
        G->seen[current] = SEEN;

        for (currentEdges = G->adjacents[current], moveFront(currentEdges);
                index(currentEdges) != -1; moveNext(currentEdges)) {

            // Get adjacent vertex
            adj = get(currentEdges);

            if (G->seen[adj] == UNSEEN) {
                // Set its parent
                G->parents[adj] = current;

                // Set it to adjacent
                G->seen[adj] = ADJACENT;

                // Update its distance
                G->distance[adj] = G->distance[current] + 1;

                // Add it to the queue
                append(queue, adj);
            }
        }
    }

    freeList(&queue);
}

/*** Other operations ***/
void printGraph(FILE* out, Graph G) {
    for (int i = 1; i <= getOrder(G); i++) {
        fprintf(out, "%d: ", i);
        printList(out, G->adjacents[i]);
        if (i != getOrder(G)-1) {
            fprintf(out, "\n");
        }
    }
}

/*** Private utils ***/

// Initialize/allocate private member values
void initInside(Graph G, int n) {
    int i;

    // Set everything to a default
    // Many of the BFS auxillary arrays will stay NULL for now.
    G->order = n;
    G->size = 0;
    G->bfs_source = NIL;
    G->adjacents = NULL;
    G->seen = NULL;
    G->parents = NULL;
    G->distance = NULL;

    // Create the adjacency list
    G->adjacents = malloc((n+1) * sizeof(List));
    for (i = 1; i <= n; i++) {
        G->adjacents[i] = newList();
    }
}

// Frees private members of the graph
void freeInside(Graph G) {
    int i;

    // Free adjacency list
    for (i = 1; i <= getOrder(G); i++) {
        freeList(&G->adjacents[i]);
    }
    free(G->adjacents);

    // The other allocated arrays might be null but can be simply freed.
    if (G->seen != NULL) {
        free(G->seen);
    }

    if (G->parents != NULL) {
        free(G->parents);
    }

    if (G->distance != NULL) {
        free(G->distance);
    }
}

// Prints a generic error and quits
void graph_throw(const char *err_string) {
    printf("Graph Error: %s\n", err_string);
    exit(1);
}

// If G is NULL, prints a null reference error and quits
void graph_check_null(Graph G, const char *method_name) {
    if (G == NULL) {
        printf("Graph Error: calling %s on NULL reference\n", method_name);
        exit(1);
    }
}
