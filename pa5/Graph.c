/*
 * Graph.c
 * Author: Spencer Peterson
 * cruzid: spjpeter
 * pa5
 *
 * Private implementation of graph ADT
 */

#include "Graph.h"
#include <stdlib.h>
#include <string.h>

typedef struct GraphObj {
    int order;
    int size;
	int dfs_run;
    List *adjacents;
    enum visibility *seen;
    int *parents;
    int *discovered;
	int *finished;
} GraphObj;

enum visibility { UNSEEN = 0, ADJACENT, SEEN };

void initInside(Graph G, int n);
void freeInside(Graph G);
void graph_throw(const char *err_string);
void graph_check_null(void *G, const char *method_name);

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
    graph_check_null(G, "getOrder");
    return G->order;
}

int getSize(Graph G) {
    graph_check_null(G, "getSize");
    return G->size;
}

int getParent(Graph G, int u) {
    graph_check_null(G, "getParent");
    if (u < 1 || u > getOrder(G)) {
        graph_throw("getParent: vertex not in graph");
    }
    if (!G->dfs_run) {
        // DFS not run yet
        return NIL;
    }

    return G->parents[u];
}

int getDiscover(Graph G, int u) {
	graph_check_null(G, "getDiscover");
	if (u < 1 || u > getOrder(G)) {
		graph_throw("getDiscover: vertex not in graph");
	}
	if (!G->dfs_run) {
		// DFS not run yet
		return UNDEF;
	}

	return G->discovered[u];
}

int getFinish(Graph G, int u) {
	graph_check_null(G, "getFinish");
	if (u < 1 || u > getOrder(G)) {
		graph_throw("getFinish: vertex not in graph");
	}
	if (!G->dfs_run) {
		// DFS not run yet
		return UNDEF;
	}

	return G->finished[u];
}

/*** Manipulation procedures ***/
void addEdge(Graph G, int u, int v) {
    graph_check_null(G, "addEdge");
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
    graph_check_null(G, "addArc");
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

void DFS(Graph G, int s) {
    List queue = NULL;

    graph_check_null(G, "BFS");
    if (s < 1 || s > getOrder(G)) {
        graph_throw("BFS: vertex not in graph order");
    }

    // Set run already
    G->dfs_run = 1;

    // Allocate seen array
    if (G->seen != NULL) {
        free(G->seen);
        G->seen = NULL;
    }
    G->seen = calloc(getOrder(G)+1, sizeof(enum visibility));

    // Allocate discovered array
    if (G->discovered != NULL) {
        free(G->discovered);
        G->discovered = NULL;
    }
    G->discovered = malloc((getOrder(G)+1)*sizeof(int));
    memset(G->discovered, UNDEF, (getOrder(G)+1)*sizeof(int));

    // Allocate finished array
    if (G->finished != NULL) {
        free(G->finished);
        G->discovered = NULL;
    }
    G->finished = malloc((getOrder(G)+1)*sizeof(int));
    memset(G->finished, UNDEF, (getOrder(G)+1)*sizeof(int));

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
		// TODO lol
    }

    freeList(&queue);
}

/*** Other operations ***/
void printGraph(FILE* out, Graph G) {
    graph_check_null(G, "printGraph");
    for (int i = 1; i <= getOrder(G); i++) {
        fprintf(out, "%d: ", i);
        printList(out, G->adjacents[i]);
        if (i != getOrder(G)) {
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
    G->dfs_run = 0;
    G->adjacents = NULL;
    G->seen = NULL;
    G->parents = NULL;
    G->discovered = NULL;
    G->finished = NULL;

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

    if (G->discovered != NULL) {
        free(G->discovered);
    }

    if (G->finished != NULL) {
        free(G->finished);
    }
}

// Prints a generic error and quits
void graph_throw(const char *err_string) {
    printf("Graph Error: %s\n", err_string);
    exit(1);
}

// If G is NULL, prints a null reference error and quits
void graph_check_null(void *G, const char *method_name) {
    if (G == NULL) {
        printf("Graph Error: calling %s on NULL reference\n", method_name);
        exit(1);
    }
}
