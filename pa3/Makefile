# Makefile

SRC_TARGETS=List.class Matrix.class
TEST_TARGETS=TestList.class TestMatrix.class

# Libraries for testing
ifdef DEBUG
JUNIT=/usr/share/java/junit.jar
HAMCREST=/usr/share/java/hamcrest-core.jar
JAVA_LIB_OPTIONS=-cp .:$(JUNIT):$(HAMCREST)
endif

JAVA_OPTIONS=-Xlint $(JAVA_LIB_OPTIONS)

.PHONY: all tests runtests clean

.INTERMEDIATE: Manifest

all: $(SRC_TARGETS)

tests: $(SRC_TARGETS) $(TEST_TARGETS)

runtests: tests
	java $(JAVA_LIB_OPTIONS) org.junit.runner.JUnitCore TestList TestMatrix

clean:
	rm -rf *.class

%.class: %.java
	javac $(JAVA_OPTIONS) $<
