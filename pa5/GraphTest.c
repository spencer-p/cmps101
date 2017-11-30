/*
 * GraphTest.c
 * Author: Spencer Peterson
 * cruzid: spjpeter
 * pa5
 *
 * Graph unit tests
 */

#include <stdlib.h>
#include <check.h>
#include "Graph.h"
#include "List.h"

#define ORDER 6
#define MODELORDER 100

Graph G, A;

void setup(void) {
    G = newGraph(ORDER);
}

void path_setup(void) {
    /*
     * 1 - 3 - 6
     * |   | \ |
     * 2 - 4 - 5
     */
    setup();
    addEdge(G, 1, 2);
    addEdge(G, 1, 3);
    addEdge(G, 2, 4);
    addEdge(G, 3, 4);
    addEdge(G, 3, 5);
    addEdge(G, 3, 6);
    addEdge(G, 4, 5);
    addEdge(G, 5, 6);
}

void model_graph_setup(void) {
    A = newGraph(MODELORDER);
}

void teardown(void) {
    freeGraph(&G);
}

void model_teardown(void) {
    freeGraph(&A);
}

START_TEST(test_create) {
	ck_assert_ptr_nonnull(G);
	ck_assert_int_eq(getOrder(G), 6);
    ck_assert_int_eq(getSize(G), 0);
    ck_assert_int_eq(getSource(G), NIL);
}
END_TEST

START_TEST(test_addArc) {
    ck_assert_int_eq(getSize(G), 0);
    addArc(G, 1, 2);
    ck_assert_int_eq(getSize(G), 1);
}
END_TEST

START_TEST(test_addEdge) {
    ck_assert_int_eq(getSize(G), 0);
    addEdge(G, 1, 2);
    ck_assert_int_eq(getSize(G), 1);
}
END_TEST

START_TEST(test_appendAddArc) {
    ck_assert_int_eq(getSize(G), 0);
    addArc(G, 1, 2);
    ck_assert_int_eq(getSize(G), 1);
    addArc(G, 1, 3);
    ck_assert_int_eq(getSize(G), 2);
}
END_TEST

START_TEST(test_makeNull) {
    ck_assert_int_eq(getSize(G), 0);
    addArc(G, 1, 2);
    ck_assert_int_eq(getSize(G), 1);
    addArc(G, 1, 3);
    ck_assert_int_eq(getSize(G), 2);
    makeNull(G);
	ck_assert_ptr_nonnull(G);
	ck_assert_int_eq(getOrder(G), 6);
    ck_assert_int_eq(getSize(G), 0);
    ck_assert_int_eq(getSource(G), NIL);
}
END_TEST

START_TEST(test_path_setup) {
    ck_assert_int_eq(getSize(G), 8);
}
END_TEST

START_TEST(test_BFS_doesnt_implode) {
    BFS(G, 1);
}
END_TEST

START_TEST(test_correct_from_1) {
    BFS(G, 1);
    int parents[] = {NIL, NIL, 1, 1, 2, 3, 3};
    int distances[] = {NIL, 0, 1, 1, 2, 2, 2};

    ck_assert_int_eq(getSource(G), 1);

    for (int i = 1; i <= getOrder(G); i++) {
        ck_assert_int_eq(getParent(G, i), parents[i]);
    }

    for (int i = 1; i <= getOrder(G); i++) {
        ck_assert_int_eq(getDist(G, i), distances[i]);
    }
}
END_TEST

START_TEST(test_getParent) {
    for (uint8_t i = 1; i <= 100; i++)
        ck_assert_int_eq(getParent(A, i), NIL);
    addArc(A, 64, 4);
    addArc(A, 64, 3);
    addArc(A, 42, 2);
    addArc(A, 2, 64);
    addArc(A, 4, 2);
    addArc(A, 3, 42);
    BFS(A, 42);
    ck_assert_int_eq(getParent(A, 42), NIL);
    ck_assert_int_eq(getParent(A, 2), 42);
    ck_assert_int_eq(getParent(A, 8), NIL);
}
END_TEST

START_TEST(test_getPath) {
    List C = newList();
    List L = newList();
    addArc(A, 64, 4);
    addArc(A, 64, 3);
    addArc(A, 42, 2);
    addArc(A, 2, 64);
    addArc(A, 4, 2);
    addArc(A, 3, 42);
    BFS(A, 3);
    getPath(L, A, 64);
    append(C, 3);
    append(C, 42);
    append(C, 2);
    append(C, 64);
    ck_assert_int_eq(equals(L, C), 1);
    moveFront(L);
    BFS(A, 2);
    getPath(L, A, 2);
    append(C, 2);
    ck_assert_int_eq(equals(L, C), 1);
    getPath(L, A, 99);
    ck_assert_int_eq(equals(L, C), 0);
    clear(L);
    clear(C);
    append(C, NIL);
    BFS(A, 99);
    getPath(L, A, 2);
    ck_assert_int_eq(equals(C, L), 1);
    freeList(&C);
    freeList(&L);
}
END_TEST

Suite *graph_suite(void) {
	Suite *s;
	TCase *tc, *tc_path, *tc_model;

	s = suite_create("Graph");

	// Core tests
	tc = tcase_create("Core");

	tcase_add_checked_fixture(tc, setup, teardown);
	tcase_add_test(tc, test_create);
	tcase_add_test(tc, test_addArc);
	tcase_add_test(tc, test_addEdge);
	tcase_add_test(tc, test_appendAddArc);
	tcase_add_test(tc, test_makeNull);

    suite_add_tcase(s, tc);

    // Pathing tests
    tc_path = tcase_create("Pathing");

    tcase_add_checked_fixture(tc_path, path_setup, teardown);
    tcase_add_test(tc_path, test_path_setup);
    tcase_add_test(tc_path, test_BFS_doesnt_implode);
    tcase_add_test(tc_path, test_correct_from_1);

    suite_add_tcase(s, tc_path);

    // Tests from ModelGraphTest from graders
    tc_model = tcase_create("Model Graph Tests");

    tcase_add_checked_fixture(tc_model, model_graph_setup, model_teardown);
    tcase_add_test(tc_model, test_getParent);
    tcase_add_test(tc_model, test_getPath);

    suite_add_tcase(s, tc_model);

	return s;
}

int main() {
	int number_failed;
	Suite *s;
	SRunner *sr;

	s = graph_suite();
	sr = srunner_create(s);

	srunner_run_all(sr, CK_NORMAL);
	number_failed = srunner_ntests_failed(sr);
	srunner_free(sr);
	return (number_failed == 0) ? EXIT_SUCCESS : EXIT_FAILURE;
}
