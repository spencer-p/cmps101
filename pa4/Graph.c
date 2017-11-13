/*
 * Graph.c
 * Author: Spencer Peterson
 * cruzid: spjpeter
 *
 * Private implementation of graph ADT
 */

#include "Graph.h"
#include <stdlib.h>

typedef struct GraphObj {
    List *adjacents;
    int order;
} GraphObj;

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
    }

    throw("Failed to allocate new Graph");
    return NULL;
}

void freeGraph(Graph *pG) {
    int i;

    for (i = 0; i < getOrder(*pG); i++) {
        freeList(&(*pG)->adjacents[i]);
    }

    free(pG);
}

/*** Access functions ***/
int getOrder(Graph G) {
    return G->order;
}

int getSize(Graph G) {
    int i, size = 0;

    for (i = 0; i < getOrder(G); i++) {
        size += length(G->adjacents[i]);
    }

    return size;
}

int getSource(Graph G);
int getParent(Graph G, int u);
int getDist(Graph G, int u);
void getPath(List L, Graph G, int u);

/*** Manipulation procedures ***/
void makeNull(Graph G);
void addEdge(Graph G, int u, int v) {
    if (u < 1 || u > getOrder(G) || v < 1 || v > getOrder(G)) {
        throw("addEdge: vertex is not in graph order");
    }

    // An edge is two arcs
    addArc(G, u, v);
    addArc(G, v, u);
}

void addArc(Graph G, int u, int v) {
    if (u < 1 || u > getOrder(G) || v < 1 || v > getOrder(G)) {
        throw("addArc: vertex is not in graph order");
    }
    List uList = G->adjacents[u];

    for (moveFront(uList); index(uList) != -1; moveNext(uList)) {
        if (get(uList) > v) {
            insertBefore(uList, v);
            break;
        }
    }
}

void BFS(Graph G, int s);

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
