LISTLIB=List.o
GRAPHLIB=Graph.o
ALL=

CC=gcc
CFLAGS=-std=c99 -Wall

ifdef DEBUG
CFLAGS+=-g
endif

all: $(ALL)

$(GRAPHLIB): $(LISTLIB)

clean:
	rm -rf *.o $(ALL)
