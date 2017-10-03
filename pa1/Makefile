# Makefile

SRC_TARGETS=List.class Lex.class ListClient.class
TEST_TARGETS=TestList.class
JAR=Lex

# Libraries for testing
ifdef DEBUG
JUNIT=/usr/share/java/junit.jar
HAMCREST=/usr/share/java/hamcrest-core.jar
JAVA_LIB_OPTIONS=-cp .:$(JUNIT):$(HAMCREST)
endif

JAVA_OPTIONS=-Xlint $(JAVA_LIB_OPTIONS)

.PHONY: all tests runtests clean

.INTERMEDIATE: Manifest

all: $(SRC_TARGETS) $(JAR)

$(JAR): $(SRC_TARGETS) Manifest
	jar cvfm $@ Manifest *.class
	chmod +x $@

Manifest:
	echo "Main-class: $(JAR)" > Manifest

tests: $(SRC_TARGETS) $(TEST_TARGETS)

runtests: tests
	java $(JAVA_LIB_OPTIONS) org.junit.runner.JUnitCore TestList

clean:
	rm -rf *.class $(JAR) Manifest

%.class: %.java
	javac $(JAVA_OPTIONS) $<
