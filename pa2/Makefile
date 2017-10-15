LISTLIB=List.o
CLIENT=ListClient
LEX=Lex
ALL=$(CLIENT) $(LEX)

CC=gcc
CFLAGS=-std=c99 -Wall

ifdef DEBUG
CFLAGS+=-g
endif

all: $(CLIENT) $(LEX)

$(LEX): $(LISTLIB)

$(CLIENT): $(LISTLIB)

ModelListTest: $(LISTLIB)

clean:
	rm -rf *.o $(ALL)
