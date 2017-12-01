/*
 * Graph.h
 * Author: Spencer Peterson
 * cruzid: spjpeter
 * pa5
 *
 * Public interface to graph ADT
 */

#ifndef GRAPH_H
#define GRAPH_H

#include "List.h"
#include <stdio.h>

#define UNDEF (-1)
#define NIL (0)

typedef struct GraphObj* Graph;

/*** Constructors - Destructors ***/
Graph newGraph(int n);
void freeGraph(Graph *pG);

/*** Access functions ***/
int getOrder(Graph G);
int getSize(Graph G);
int getParent(Graph G, int u);
int getDiscover(Graph G, int u);
int getFinish(Graph G, int u);

/*** Manipulation procedures ***/
void addEdge(Graph G, int u, int v);
void addArc(Graph G, int u, int v);
void DFS(Graph G, List S);

/*** Other operations ***/
Graph transpose(Graph G);
Graph copyGraph(Graph G);
void printGraph(FILE* out, Graph G);

#endif
