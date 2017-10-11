/*
 * List.java
 *
 * Copyright (C) 2017, Spencer Peterson
 */

import java.util.Iterator;

public class List implements Iterable<Integer> {

	int prevCount = 0, nextCount = 0;
	Node current = null, front = null, back = null;

	// Node object for data
	class Node {
		public Node next = null, prev = null;
		public int data;

		Node(int d) {
			this.data = d;
		}
	}


	// Constructors
	public List() {}

	public List(int data[]) {
		extend(data);
	}

	public List(List toCopy) {
		concat(toCopy);
	}

	public List copy() {
		return new List(this);
	}

	// Access functions
	public int length() {
		return prevCount+nextCount;
	}

	public int index() {
		if (current == null) {
			return -1;
		}
		else {
			return prevCount;
		}
	}

	public int front() {
		if (front == null) {
			throw new IndexOutOfBoundsException("front() from empty list");
		}
		else {
			return front.data;
		}
	}

	public int back() {
		if (back == null) {
			throw new IndexOutOfBoundsException("back() from empty list");
		}
		else {
			return back.data;
		}
	}

	public int get() {
		if (current == null) {
			throw new IndexOutOfBoundsException("get() from null cursor");
		}
		else {
			return current.data;
		}
	}

	public boolean equals(List L) {
		// Lists of different lengths are never equal
		if (L.length() != length()) {
			return false;
		}

		// Scrub both to front
		moveFront();
		L.moveFront();

		while(index() >= 0) {
			// Early exit if any are not equal
			if (get() != L.get()) {
				return false;
			}

			// next
			moveNext();
			L.moveNext();
		}

		// Everything was equal
		return true;
	}

	public boolean equals(int data[]) {
		// Same structure as equals(List)

		if (data.length != length()) {
			return false;
		}

		moveFront();
		for (int i : data) {
			if (i != get()) {
				return false;
			}
			moveNext();
		}

		return true;
	}


	// Mutability
	public void clear() {
		// TODO this probably leaks memory
		prevCount = 0;
		nextCount = 0;
		current = null;
	}

	public void moveFront() {
		current = front;
		nextCount = length();
		prevCount = 0;
	}

	public void moveBack() {
		current = back;
		prevCount = length()-1;
		nextCount = 1;
	}

	public void movePrev() {
		if (current != null) {
			current = current.prev;
			prevCount--;
			nextCount++;
		}
	}

	public void moveNext() {
		if (current != null) {
			current = current.next;
			prevCount++;
			nextCount--;
		}
	}

	public void prepend(int data) {
		if (length() == 0) {
			createCurrent(data);
		}
		else {
			front.prev = new Node(data);
			front.prev.next = front;
			front = front.prev;
			prevCount++;
		}
	}

	public void append(int data) {
		if (length() == 0) {
			createCurrent(data);
		}
		else {
			back.next = new Node(data);
			back.next.prev = back;
			back = back.next;
			nextCount++;
		}
	}

	public void insertBefore(int data) {
		if (length() == 0) {
			createCurrent(data);
		}
		else {
			Node tmp = current.prev;
			current.prev = new Node(data);
			current.prev.prev = tmp;
			if (tmp != null) {
				tmp.next = current.prev;
			}
			else {
				front = current.prev;
			}
			current.prev.next = current;
			prevCount++;
		}
	}

	public void insertAfter(int data) {
		if (length() == 0) {
			createCurrent(data);
		}
		else {
			Node tmp = current.next;
			current.next = new Node(data);
			current.next.next = tmp;
			if (tmp != null) {
				tmp.prev = current.next;
			}
			else {
				back = current.next;
			}
			current.next.prev = current;
			nextCount++;
		}
	}

	public void deleteFront() {
		if (length() <= 0) {
			throw new IndexOutOfBoundsException("deleteFront() from empty list");
		}
		if (current == front) {
			current = null;
		}

		front = front.next;

		if (front == current) {
			nextCount--;
		}
		else {
			prevCount--;
		}
	}

	public void deleteBack() {
		if (length() <= 0) {
			throw new IndexOutOfBoundsException("deleteBack() from empty list");
		}
		if (current == back) {
			current = null;
		}

		back = back.prev;

		nextCount--;
	}

	public void delete() {
		if (length() <= 0 || length() < 0) {
			throw new IndexOutOfBoundsException("deleteBack() from empty list");
		}
		// Close previous
		if (current.prev != null) {
			current.prev.next = current.next;
		}

		// Close next
		if (current.next != null) {
			current.next.prev = current.prev;
		}

		// Update front if necessary
		if (current == front) {
			front = current.next;
		}

		// Update back if necessary
		if (current == back) {
			back = current.prev;
		}

		// Clear current
		current = null;

		// Current was 'next' so decrease nextcount
		nextCount--;
	}

	public void extend(int data[]) {
		for (int d : data) {
			append(d);
		}
	}

	public void concat(List l) {
		for (int d : l) {
			append(d);
		}
	}


	public String toString() {
		String s = "";
		for (int d : this) {
			s += d;
			if (this.current != null) {
				s += " ";
			}
		}
		return s;
	}

	// Private utilities
	private void createCurrent(int data) {
		current = new Node(data);
		front = current;
		back = current;
		nextCount++;
	}

	// Iteration
	private class ListIterator implements Iterator<Integer> {
		List list = null;
		Node tmpCurrent = null;
		int tmpPrevCount = -1, tmpNextCount = -1;

		ListIterator(List list) {
			this.list = list;
			this.tmpCurrent = list.current;
			this.tmpPrevCount = list.prevCount;
			this.tmpNextCount = list.nextCount;
			this.list.moveFront();
		}

		public boolean hasNext() {
			if (list.index() >= 0) {
				return true;
			}
			else {
				list.current = tmpCurrent;
				list.prevCount = tmpPrevCount;
				list.nextCount = tmpNextCount;
				return false;
			}
		}

		public Integer next() {
			Integer toReturn = list.get();
			list.moveNext();
			return toReturn;
		}
	}

	public Iterator<Integer> iterator() {
		return new ListIterator(this);
	}
}
