/*
 * List.h
 *
 * Public interface to the list object
 *
 * Author: Spencer Peterson
 */

#ifndef LIST_H
#define LIST_H

#include <stdio.h>

typedef struct List_t {
    struct List_t *current;
    struct List_t *front;
    struct List_t *back;
    int prev_count;
    int next_count;
} List;

// Constructors-destructors
List newList(void);
void freeList(List *pL);

// Access functions
int length(List L);
int index(List L);
int front(List L);
int back(List L);
int get(List L);
int equals(List A, List B);

// Manipulation procedures
void clear(List L);
void moveFront(List L);
void moveBack(List L);
void movePrev(List L);
void moveNext(List L);
void prepend(List L, int data);
void append(List L, int data);
void insertBefore(List L, int data);
void insertAfter(List L, int data);
void deleteFront(List L);
void deleteBack(List L);
void delete(List L);

// Other operations
void printList(FILE* out, List L);
List copyList(List L) ;

#endif
