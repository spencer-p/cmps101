/*
 * List.c
 * Author: Spencer Peterson
 * cruzid: spjpeter
 * pa5
 *
 * List object implementation
 */

#include <stdlib.h>
#include <stdio.h>

#include "List.h"

// Primitive bools
#define true  1
#define false 0

// Node definitions
typedef struct Node_t {
    struct Node_t *next, *prev;
    int data;
} Node;

Node *newNode(int d);
void freeNode(Node *n);

// List struct def
typedef struct ListObj {
    Node *current;
    Node *front;
    Node *back;
    int prevCount;
    int nextCount;
} ListObj;

// Private methods
void createCurrent(List L, int data);
void throw(const char *err_string);
void check_null(List L, const char *method_name);

// Constructors-destructors
List newList(void) {
    List new;

    new = malloc(sizeof(ListObj));

    if (new == NULL) {
        throw("Failed to allocate List");
    }

    // Initialize empty list
    new->current = NULL;
    new->front = NULL;
    new->back = NULL;
    new->prevCount = 0;
    new->nextCount = 0;

    return new;
}

void freeList(List *pL) {
    Node *tmp_to_free;

    if (pL != NULL && *pL != NULL) {
        // Free each list item
        moveFront(*pL);
        while (index(*pL) != -1) {
            tmp_to_free = (*pL)->current;
            moveNext(*pL);
            free(tmp_to_free);
        }

        // Free the list structure
        free(*pL);
        *pL = NULL;
    }
}

// Access functions
int length(List L) {
    check_null(L, "length");

    return L->prevCount + L->nextCount;
}

int index(List L) {
    check_null(L, "index");

    if (L->current == NULL) {
        return -1;
    }
    else {
        return L->prevCount;
    }
}

int front(List L) {
    int front;

    check_null(L, "front");

    if (L->front == NULL) {
        throw("front() from empty list");
    }
    else {
        front = L->front->data;
    }

    return front;
}

int back(List L) {
    int back;

    check_null(L, "back");

    if (L->back == NULL) {
        throw("back() from empty list");
    }
    else {
        back = L->back->data;
    }

    return back;
}

int get(List L) {
    int d;

    check_null(L, "get");

    if (L->current == NULL) {
        throw("get() from empty list");
    }
    else {
        d = L->current->data;
    }

    return d;
}

int equals(List A, List B) {
    Node *walkA, *walkB;

    check_null(A, "equals");
    check_null(B, "equals");

    // If the lists have different lengths they are not equal
    if (length(A) != length(B)) {
        return false;
    }

    for (walkA = A->front, walkB = B->front;
            walkA != NULL && walkB != NULL;
            walkA = walkA->next, walkB = walkB->next) {
        if (walkA->data != walkB->data) {
            return false;
        }
    }

    // Final - everything was equal
    return true;
}

// Manipulation procedures
void clear(List L) {
    Node *tmp_to_free;

    check_null(L, "clear");

    // Free each list item
    moveFront(L);
    while (index(L) != -1) {
        tmp_to_free = L->current;
        moveNext(L);
        free(tmp_to_free);
    }

    // Reset values
    L->current = NULL;
    L->front = NULL;
    L->back = NULL;
    L->prevCount = 0;
    L->nextCount = 0;
}

void moveFront(List L) {
    check_null(L, "moveFront");

    // Update front
    L->current = L->front;

    // Update counts
    L->nextCount = length(L);
    L->prevCount = 0;
}

void moveBack(List L) {
    check_null(L, "moveBack");

    // Update back
    L->current = L->back;

    // Update counts
    L->prevCount = length(L)-1;
    L->nextCount = 1;
}

void movePrev(List L) {
    check_null(L, "movePrev");

    if (L->current == NULL) {
        throw("movePrev() from undefined cursor");
    }

    // Update current
    L->current = L->current->prev;

    // Update counts
    L->prevCount--;
    L->nextCount++;
}

void moveNext(List L) {
    check_null(L, "moveNext");

    if (L->current == NULL) {
        throw("moveNext() from undefined cursor");
    }

    // Update current
    L->current = L->current->next;

    // Update counts
    L->prevCount++;
    L->nextCount--;
}

void prepend(List L, int data) {
    check_null(L, "prepend");

    if (length(L) == 0) {
        createCurrent(L, data);
    }
    else {
        L->front->prev = newNode(data);
        L->front->prev->next = L->front;
        L->front = L->front->prev;
        L->prevCount++;
    }
}

void append(List L, int data) {
    check_null(L, "append");

    if (length(L) == 0) {
        createCurrent(L, data);
    }
    else {
        L->back->next = newNode(data);
        L->back->next->prev = L->back;
        L->back = L->back->next;
        L->nextCount++;
    }
}

void insertBefore(List L, int data) {
    Node *tmp;

    check_null(L, "insertBefore");

    if (length(L) == 0) {
        createCurrent(L, data);
    }
    else {
        tmp = L->current->prev;
        L->current->prev = newNode(data);
        L->current->prev->prev = tmp;

        if (tmp != NULL) {
            tmp->next = L->current->prev;
        }
        else {
            L->front = L->current->prev;
        }

        L->current->prev->next = L->current;
        L->prevCount++;
    }
}

void insertAfter(List L, int data) {
    Node *tmp;
    
    check_null(L, "insertAfter");

    if (length(L) == 0) {
        createCurrent(L, data);
    }
    else {
        tmp = L->current->next;
        L->current->next = newNode(data);
        L->current->next->next = tmp;

        if (tmp != NULL) {
            tmp->prev = L->current->next;
        }
        else {
            L->back = L->current->next;
        }

        L->current->next->prev = L->current;
        L->nextCount++;
    }
}

void deleteFront(List L) {
    Node *tmp;

    check_null(L, "deleteFront");

    if (length(L) <= 0) {
        throw("deleteFront() from empty list");
    }

    // Remove reference to front if it is the current node
    if (L->current == L->front) {
        L->current = NULL;
    }

    // Save tmp
    tmp = L->front->next;

    // Free the front
    freeNode(L->front);

    // Remove ref to freed node
    if (tmp != NULL) {
        tmp->prev = NULL;
    }

    // Move the front reference
    L->front = tmp;

    // Update counts
    if (L->front == L->current) {
        L->nextCount--;
    }
    else {
        L->prevCount--;
    }
}

void deleteBack(List L) {
    Node *tmp;

    check_null(L, "deleteBack");

    if (length(L) <= 0) {
        throw("deleteBack() from empty list");
    }

    // Remove reference to back if it is the current node
    if (L->current == L->back) {
        L->current = NULL;
    }

    // Save tmp
    tmp = L->back->prev;

    // Free the back
    freeNode(L->back);

    // Remove reference to freed node
    if (tmp != NULL) {
        tmp->next = NULL;
    }

    // Move the front reference
    L->back = tmp;

    // Update counts
    L->nextCount--;
}

void delete(List L) {
    check_null(L, "delete");

    if (length(L) <= 0) {
        throw("deleteBack() from empty list");
    }

    if (L->current == NULL) {
        throw("delete() from undefined cursor");
    }

    // Close previous
    if (L->current->prev != NULL) {
        L->current->prev->next = L->current->next;
    }

    // Close next
    if (L->current->next != NULL) {
        L->current->next->prev = L->current->prev;
    }

    // Update front if needed
    if (L->current == L->front) {
        L->front = L->current->next;
    }

    // Update back if needed
    if (L->current == L->back) {
        L->back = L->current->prev;
    }

    // Free the current
    freeNode(L->current);
    L->current = NULL;

    // Update count
    L->nextCount--;
}

// Other operations
void printList(FILE* out, List L) {
    Node *walk;

    check_null(L, "printList");

    for (walk = L->front; walk != NULL; walk = walk->next) {
        fprintf(out, "%d%s", walk->data, (walk->next == NULL)?"":" ");
    }
}

List copyList(List L) {
    List new;
    Node *walk;

    check_null(L, "copyList");

    new = newList();

    for (walk = L->front; walk != NULL; walk = walk->next) {
        append(new, walk->data);
    }

    return new;
}

Node *newNode(int d) {
    Node *new;

    new = malloc(sizeof(Node));

    if (new == NULL) {
        throw("Failed to allocate Node");
    }

    // Initialize empty node
    new->next = NULL;
    new->prev = NULL;
    new->data = d;

    return new;
}

void freeNode(Node *n) {
    free(n);
}

// Creates a first entry in empty list
void createCurrent(List L, int data) {
    if (length(L) > 0) {
        throw("Internal error");
    }

    L->current = newNode(data);
    L->front = L->current;
    L->back = L->current;
    L->nextCount = 1;
    L->prevCount = 0;
}

// Prints a generic list error and quits
void throw(const char *err_string) {
    printf("List Error: %s\n", err_string);
    exit(1);
}

// If L is NULL, prints a null reference error and quits
void check_null(List L, const char *method_name) {
    if (L == NULL) {
        printf("List Error: calling %s on NULL List reference\n", method_name);
        exit(1);
    }
}
