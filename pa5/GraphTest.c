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

Graph G;
List S;

void setup(void) {
	G = newGraph(ORDER);
	S = newList();
}

void path_setup(void) {
	/*
	 * 1 -> 2     5
	 * |  ^ |  /  |
	 * v /  v v   v
	 * 4 <- 3     6
	 */
	setup();
	addArc(G, 1, 2);
	addArc(G, 2, 3);
	addArc(G, 3, 4);
	addArc(G, 1, 4);
	addArc(G, 4, 2);
	addArc(G, 5, 3);
	addArc(G, 5, 6);
	addArc(G, 6, 6);

	// [1, 6] in S
	for (int i = 1; i <= 6; i++) {
		append(S, i);
	}
}

void teardown(void) {
	freeGraph(&G);
	freeList(&S);
}

START_TEST(test_create) {
	ck_assert_ptr_nonnull(G);
	ck_assert_int_eq(getOrder(G), 6);
	ck_assert_int_eq(getSize(G), 0);
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

START_TEST(test_path_setup) {
	ck_assert_int_eq(getSize(G), 8);
}
END_TEST

START_TEST(test_DFS_doesnt_implode) {
	DFS(G, S);
}
END_TEST

START_TEST(test_DFS) {
	DFS(G, S);
	int parents[] = {NIL, NIL, 1, 2, 3, NIL, 5};
	int discovered[] = {UNDEF, 1, 2, 3, 4, 9, 10};
	int finished[] = {UNDEF, 8, 7, 6, 5, 12, 11};

	for (int i = 1; i <= getOrder(G); i++) {
		ck_assert_int_eq(getParent(G, i), parents[i]);
	}

	for (int i = 1; i <= getOrder(G); i++) {
		ck_assert_int_eq(getDiscover(G, i), discovered[i]);
	}

	for (int i = 1; i <= getOrder(G); i++) {
		ck_assert_int_eq(getFinish(G, i), finished[i]);
	}
}
END_TEST

START_TEST(test_DFS_reverse_S) {
	clear(S);
	for (int i = 1; i <= 6; i++) {
		prepend(S, i);
	}
	DFS(G, S);
	int parents[] = {NIL, NIL, 4, 5, 3, NIL, NIL};
	int discovered[] = {UNDEF, 11, 6, 4, 5, 3, 1};
	int finished[] = {UNDEF, 12, 7, 9, 8, 10, 2};

	for (int i = 1; i <= getOrder(G); i++) {
		ck_assert_int_eq(getParent(G, i), parents[i]);
	}

	for (int i = 1; i <= getOrder(G); i++) {
		ck_assert_int_eq(getDiscover(G, i), discovered[i]);
	}

	for (int i = 1; i <= getOrder(G); i++) {
		ck_assert_int_eq(getFinish(G, i), finished[i]);
	}
}
END_TEST

Suite *graph_suite(void) {
	Suite *s;
	TCase *tc, *tc_path;

	s = suite_create("Graph");

	// Core tests
	tc = tcase_create("Core");

	tcase_add_checked_fixture(tc, setup, teardown);
	tcase_add_test(tc, test_create);
	tcase_add_test(tc, test_addArc);
	tcase_add_test(tc, test_addEdge);
	tcase_add_test(tc, test_appendAddArc);

	suite_add_tcase(s, tc);

	// Pathing tests
	tc_path = tcase_create("Pathing");

	tcase_add_checked_fixture(tc_path, path_setup, teardown);
	tcase_add_test(tc_path, test_path_setup);
	tcase_add_test(tc_path, test_DFS_doesnt_implode);
	tcase_add_test(tc_path, test_DFS);
	tcase_add_test(tc_path, test_DFS_reverse_S);

	suite_add_tcase(s, tc_path);

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
