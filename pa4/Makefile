LISTLIB=List.o
GRAPHLIB=Graph.o
TESTS=GraphTest
ALL=FindPath

CC=gcc
CFLAGS += -std=c99 -Wall -Werror -lcheck -lm

ifdef DEBUG
CFLAGS+=-g
endif

.PHONY: all clean tests runtests

all: $(ALL)

$(GRAPHLIB): $(LISTLIB)

tests: $(TESTS)

$(ALL): $(GRAPHLIB) $(LISTLIB)

$(TESTS): $(GRAPHLIB) $(LISTLIB)

runtests: $(TESTS)
	@for binary in $(TESTS); do\
		./$$binary; \
	done

clean:
	rm -rf *.o $(ALL) $(TESTS)
