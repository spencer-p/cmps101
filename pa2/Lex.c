/*
 * Lex.c
 *
 * This sorts a file's lines alphabetically using a list.
 *
 * Author: Spencer Peterson
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "List.h"

#define LINE_BUFFER_SIZE 256

char **readLines(FILE *input);

int main(int argc, char **argv) {
    FILE *in = NULL, *out = NULL;    
    List list = newList();
    char **lines = NULL;
    int i;

    // Check usage
    if (argc != 3) {
        printf("Usage: %s infile outfile\n", argv[0]);
        exit(1);
    }

    // Open files
    in = fopen(argv[1], "r");
    out = fopen(argv[2], "w");

    // Make sure files opened
    if (in == NULL || out == NULL) {
        printf("Could not open file(s)");
        exit(1);
    }

    // Read in the input
    lines = readLines(in);

    // Sort the lines into the list
    for (i = 0; lines[i] != NULL; i++) {

        moveFront(list);

        // Scrub to location the line belongs
        while (index(list) != -1 && strcmp(lines[i], lines[get(list)]) >= 0) {
            moveNext(list);
        }

        // If index is -1, then this line belongs at the end
        if (index(list) == -1) {
            append(list, i);
        }
        // Otherwise put it right before where we stopped
        else {
            insertBefore(list, i);
        }
    }

    // Print out the sorted list into the output file
    moveFront(list);
    while (index(list) != -1) {
        fprintf(out, "%s", lines[get(list)]);
        moveNext(list);
    }

    // Free the lines
    for (i = 0; lines[i] != NULL; i++) {
        free(lines[i]);
    }
    free(lines);

    // Free the list
    freeList(&list);

    // Close the files
    fclose(in);
    fclose(out);
}

char **readLines(FILE *input) {
    char **lines = NULL;
    char line[LINE_BUFFER_SIZE];
    int lineslen = 0;

    // Read lines in a loop
    while (fgets(line, LINE_BUFFER_SIZE, input) != NULL) {
        // Lengthen the lines array
        lineslen++;
        lines = realloc(lines, lineslen*sizeof(char*));

        // Allocate memory for the line
        lines[lineslen-1] = malloc(sizeof(char)*(strlen(line)+1));

        // Copy it in from the buffer
        strcpy(lines[lineslen-1], line);
    }

    // Add a terminator
    lineslen++;
    lines = realloc(lines, lineslen*sizeof(char*));
    lines[lineslen-1] = NULL;

    return lines;
}
