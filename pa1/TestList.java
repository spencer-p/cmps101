import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.Before;

public class TestList {
	List l;

	@Before
	public void setup() {
		this.l = new List();
	}

	@Test
	public void initialLength() {
		assertEquals(0, this.l.length());
	}

	@Test
	public void updateLength() {
		this.l.insertAfter(1);
		assertEquals(1, this.l.length());
	}

	@Test
	public void initialIndex() {
		assertEquals(-1, this.l.index());
	}

	@Test
	public void updateIndex() {
		this.l.insertAfter(1);
		assertEquals(0, this.l.index());
	}

	@Test
	public void insertAfter() {
		this.l.insertAfter(1);
		assertEquals(1, this.l.get());
		assertEquals(1, this.l.length());
		assertEquals(0, this.l.index());

		this.l.insertAfter(2);
		assertEquals(1, this.l.get());
		assertEquals(2, this.l.length());
		assertEquals(0, this.l.index());
	}

	@Test
	public void insertBefore() {
		this.l.insertBefore(1);
		assertEquals(1, this.l.get());
		assertEquals(1, this.l.length());
		assertEquals(0, this.l.index());

		this.l.insertBefore(2);
		assertEquals(1, this.l.get());
		assertEquals(2, this.l.length());
		assertEquals(1, this.l.index());
	}

	@Test
	public void moveNext() {
		this.l.insertAfter(1);
		this.l.insertAfter(2);

		assertEquals(1, this.l.get());
		assertEquals(0, this.l.index());

		this.l.moveNext();
		assertEquals(2, this.l.get());
		assertEquals(1, this.l.index());

		this.l.moveNext();
		assertEquals(-1, this.l.index());
	}

	@Test
	public void movePrev() {
		this.l.insertBefore(1);
		this.l.insertBefore(2);

		assertEquals(1, this.l.get());
		assertEquals(1, this.l.index());

		this.l.movePrev();
		assertEquals(2, this.l.get());
		assertEquals(0, this.l.index());

		this.l.movePrev();
		assertEquals(-1, this.l.index());
	}

	@Test
	public void deleteOne() {
		// Insert and delete single element
		this.l.insertAfter(1);
		this.l.delete();
		assertEquals(0, this.l.length());
	}

	@Test
	public void deleteAfter() {
		// Insert two after and delete one
		this.l.insertAfter(1);
		this.l.insertAfter(2);
		assertEquals(1, this.l.get());
		assertEquals(2, this.l.length());

		this.l.delete();
		assertEquals(2, this.l.get());
		assertEquals(1, this.l.length());
	}

	@Test
	public void deleteBefore() {
		// Insert two before and delete one
		this.l.insertBefore(1);
		this.l.insertBefore(2);
		assertEquals(1, this.l.get());
		assertEquals(2, this.l.length());

		this.l.delete();
		assertEquals(2, this.l.get());
		assertEquals(1, this.l.length());

	}

	@Test
	public void clear() {
		this.l.insertAfter(1);
		this.l.insertAfter(2);
		this.l.clear();
		assertEquals(this.l.length(), 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void emptyGet() {
		this.l.get();
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void emptyFront() {
		this.l.front();
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void emptyBack() {
		this.l.back();
	}

	@Test
	public void append() {
		this.l.append(1);
		assertEquals(1, this.l.length());
		assertEquals(0, this.l.index());
		assertEquals(1, this.l.get());
		assertEquals(1, this.l.back());

		this.l.append(2);
		assertEquals(2, this.l.length());
		assertEquals(0, this.l.index());
		assertEquals(1, this.l.get());
		assertEquals(2, this.l.back());
	}

	@Test
	public void prepend() {
		this.l.prepend(1);
		assertEquals(1, this.l.length());
		assertEquals(0, this.l.index());
		assertEquals(1, this.l.get());
		assertEquals(1, this.l.front());

		this.l.prepend(2);
		assertEquals(2, this.l.length());
		assertEquals(1, this.l.index());
		assertEquals(1, this.l.get());
		assertEquals(2, this.l.front());
	}

	@Test
	public void arrayEquals() {
		int data[] = {10, 9, 8, 7};
		int wrong[] = {7, 8, 9, 10};
		this.l = new List(data);
		assertTrue(this.l.equals(data));
		assertFalse(this.l.equals(wrong));
	}

	@Test
	public void listEquals() {
		int data[] = {10, 9, 8, 7};
		int wrong[] = {7, 8, 9, 10};
		int slightlyWrong[] = {10, 9, 8, 6};

		assertTrue(new List(data).equals(new List(data)));
		assertFalse(new List(data).equals(new List(wrong)));
		assertFalse(new List(data).equals(new List(slightlyWrong)));
	}

	@Test
	public void iterator() {
		int data[] = {10, 9, 8, 7};
		int sum = 0;

		this.l = new List(data);

		for (int i : this.l) {
			sum += i;
		}

		assertEquals(10+9+8+7, sum);
	}

	@Test
	public void emptyIterator() {
		int data[] = {};
		int sum = 0;

		this.l = new List(data);

		for (int i : this.l) {
			sum += i;
		}

		assertEquals(0, sum);
	}

	@Test
	public void copyConstructor() {
		int data[] = {1, 2, 3, 4};
		int fake[] = {5, 6, 7, 8};
		this.l = new List(data);

		assertTrue(this.l.equals(new List(this.l)));
		assertFalse(this.l.equals(new List(fake)));
	}

	@Test
	public void copy() {
		int data[] = {1, 2, 3, 4};
		int fake[] = {5, 6, 7, 8};
		this.l = new List(data);

		assertTrue(this.l.equals(this.l.copy()));
		assertFalse(this.l.copy().equals(new List(fake)));
	}

	@Test
	public void concat() {
		int data[] = {1, 2, 3, 4};
		int more[] = {5, 6, 7, 8};

		this.l = new List(data);
		List other = new List(more);

		this.l.concat(other);

		int sum = 0;

		for (int i : this.l) {
			sum += i;
		}

		assertEquals(sum, 1+2+3+4+5+6+7+8);

	}

	@Test
	public void insertionSort() {
		this.l.moveFront();
		this.l.insertBefore(1);

		this.l.moveFront();
		this.l.append(2);

		this.l.moveFront();
		this.l.insertBefore(3);

		assertEquals("3 1 2", this.l.toString());
	}

	@Test
	public void frontIsCorrect() {
		this.l.append(1);
		this.l.insertBefore(2);

		this.l.moveFront();
		assertEquals(2, this.l.get());
	}

	@Test
	public void backIsCorrect() {
		this.l.prepend(1);
		this.l.insertAfter(2);

		this.l.moveBack();
		assertEquals(2, this.l.get());
	}
}
