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

void DFS(Graph G, List S) {
    List stack = NULL, processOrder = NULL;

    graph_check_null(G, "DFS");
    if (length(S) != getOrder(G)) {
        graph_throw("DFS: S length != G order");
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

	// Separate S and the process order
	processOrder = copyList(S);
	clear(S);

    // Initialize time
	int time = 0;

	// Loop on order vertices should be processed
    for (moveFront(processOrder);
			index(processOrder) != -1;
			moveNext(processOrder)) {

		// Visit if unseen
		if (G->seen[get(processOrder)] == UNSEEN) {
			// This Visit() method is implemented non recursively using a stack
			// of nodes to visit and switching on their colors.

			// Init the stack with the root node
			stack = newList();
			prepend(stack, get(processOrder));

			// Iterate on the stack
			while (moveFront(stack), index(stack) != -1) {
				int x = get(stack);

				// If x is not seen, then we do the normal visit operation.
				if (G->seen[x] == UNSEEN) {
					G->seen[x] = ADJACENT;
					G->discovered[x] = ++time;
					List adj = G->adjacents[x];
					for (moveBack(adj); index(adj) != -1; movePrev(adj)) {
						int y = get(adj);
						if (G->seen[y] == UNSEEN) {
							G->parents[y] = x;
							prepend(stack, y);
						}
					}
				}

				// If x is adjacent already, we've now finished it. Mark it as
				// such and delete it.
				else if (G->seen[x] == ADJACENT) {
					G->seen[x] = SEEN;
					G->finished[x] = ++time;
					// Store in S
					prepend(S, x);
					// Pop off x
					delete(stack);
				}

				// If x is already finished, just delete it. This is the result
				// of a forward edge somewhere.
				else if (G->seen[x] == SEEN) {
					// Pop off x
					delete(stack);
				}
			}

			// Free the stack for next tree
			freeList(&stack);
		}
    }
}

/*** Other operations ***/
Graph transpose(Graph G) {
	Graph new = newGraph(getOrder(G));

	// Loop on adjacencies of G
	for (int i = 1; i <= getOrder(G); i++) {
		for (moveFront(G->adjacents[i]);
				index(G->adjacents[i]) != -1;
				moveNext(G->adjacents[i])) {

			// Add arc with (i, j) swapped
			addArc(new, get(G->adjacents[i]), i);
		}
	}

	return new;
}

Graph copyGraph(Graph G) {
	Graph new = newGraph(getOrder(G));

	// Copy lists term by term
	for (int i = 1; i <= getOrder(G); i++) {
		new->adjacents[i] = copyList(G->adjacents[i]);
	}

	// Set size
	new->size = G->size;

	return new;
}

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
