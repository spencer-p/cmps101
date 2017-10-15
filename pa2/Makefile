LISTLIB=List.o
CLIENT=ListClient

CC=gcc
CFLAGS=-std=c99 -Wall

ifdef DEBUG
CFLAGS+=-g
endif

all: $(CLIENT)

$(CLIENT): $(LISTLIB)

ModelListTest: $(LISTLIB)

clean:
	rm -rf *.o $(CLIENT)
