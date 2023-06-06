
package edu.uwm.cs351;

import java.util.AbstractCollection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A variant of the ApptBook ADT that follows the Collection model. In
 * particular, it has no sense of a current element. All access to elements by
 * the client must be through the iterator. The {@link #add(Appointment)} method
 * should add at the correct spot in sorted order in the collection.
 */
public class NewApptBook extends AbstractCollection<Appointment> implements Cloneable {

	private static final int INITIAL_CAPACITY = 1;
	private int manyItems; // Keeps track of the number of elements in the collection
	private Appointment[] data; // The array to store the appointments
	private int version; // Keeps track of the version for iterator validity

	@Override
	public Iterator<Appointment> iterator() {
		assert wellFormed() : "invariant broken in iterator()";
		return new MyIterator();
	}

	/** Calls the constructor from MyIterator */
	public Iterator<Appointment> iterator(Appointment start) {
		assert wellFormed() : "invariant broken in iterator)";
		if (start == null)
			throw new NullPointerException("Start must not be null");
		return new MyIterator(start);
	}

	/**
	 * Binary search method to use for the constructor with the appointment
	 * parameter in MyIterator.
	 */
	public static int search(Appointment[] a, int first, int size, Appointment target) {
		int middle;
		if (size <= 0)
			return -1;
		else {
			middle = first + size / 2;
			if (target.compareTo(a[middle]) == 0)
				return middle;
			else if (target.compareTo(a[middle]) > 0)
				return search(a, first, size / 2, target);
			else
				return search(a, middle + 1, (size - 1) / 2, target);
		}
	}

	/**
	 * Determine the number of elements in this book.
	 * 
	 * @return the number of elements in this book
	 **/
	public int size() {
		assert wellFormed() : "invariant broken in size)";
		return manyItems;
	}

	public NewApptBook(int capacity) {
		if (capacity < 0)
			throw new IllegalArgumentException("Negative capacity not allowed");
		data = new Appointment[capacity];
		assert wellFormed() : "constructor did not satisfy invariant!";
	}

	public NewApptBook() {
		this(INITIAL_CAPACITY);
	}

	/** Reports the errors */
	private static boolean doReport = true; // Change only in invariant tests

	private boolean report(String error) {
		if (doReport) {
			System.out.println("Invariant error: " + error);
		}
		return false;
	}

	private void ensureCapacity(int minimumCapacity) {
		if (data.length >= minimumCapacity)
			return;
		int newCap = data.length * 2;
		if (newCap < minimumCapacity)
			newCap = minimumCapacity;
		Appointment[] newData = new Appointment[newCap];
		for (int i = 0; i < manyItems; ++i) {
			newData[i] = data[i];
		}
		data = newData;
		return;
	}

	private boolean wellFormed() {
		if (data == null)
			return report("data is null"); // Test the negation of the condition
		if (data.length < manyItems)
			return report("data is too short");
		return true;
	}

	@Override
	public boolean add(Appointment element) {
		assert wellFormed() : "invariant failed at start of insert";
		ensureCapacity(manyItems + 1);
		if (element == null)
			throw new NullPointerException("Cannot add null");
		int i;
		for (i = manyItems; i > 0; --i) {
			if (data[i - 1].compareTo(element) <= 0)
				break;
			data[i] = data[i - 1];
		}
		data[i] = element;
		++manyItems;
		++version;
		assert wellFormed() : "invariant failed at end of insert";
		return true;
	}

	public void clear() {
		assert wellFormed() : "invariant broken in clear()";
		if (manyItems == 0)
			return;
		++version;
		manyItems = 0;
		assert wellFormed() : "invariant broken by clear()";
	}

	public boolean contains(Object x) {
		assert wellFormed() : "invariant broken in contains()";
		if (!(x instanceof Appointment))
			return false;
		Appointment a = (Appointment) x;
		Iterator<Appointment> it = iterator(a);
		if (it.hasNext()) {
			Appointment b = it.next();
			if (a.equals(b))
				return true;
		}
		return false;
	}

	@Override
	public NewApptBook clone() {
		assert wellFormed() : "invariant failed at start of clone";
		NewApptBook answer;
		try {
			answer = (NewApptBook) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("This class does not implement Cloneable");
		}

		answer.data = data.clone();
		assert wellFormed() : "invariant failed at end of clone";
		assert answer.wellFormed() : "invariant on answer failed at end of clone";
		return answer;
	}

	private class MyIterator implements Iterator<Appointment> {
		// The nested MyIterator class should use the following invariant checker:
		private int current, next;
		private int myVersion = version;

		private void checkVersion() {
			if (myVersion != version)
				throw new ConcurrentModificationException("Stale iterator");
		}

		MyIterator() {
			current = next = 0;
			assert wellFormed() : "iterator invariant broken";
		}

		MyIterator(Appointment start) {
			int lo = 0, hi = manyItems;
			while (lo < hi) {
				int mid = lo + (hi - lo) / 2;
				Appointment a = data[mid];
				if (a.compareTo(start) < 0)
					lo = mid + 1;
				else
					hi = mid;
			}
			current = next = lo;
			assert wellFormed();
		}

		@Override // Required
		public boolean hasNext() {
			assert wellFormed() : "invariant broken in hasNext";
			checkVersion();
			return next < manyItems;
		}

		@Override // Required
		public Appointment next() {
			assert wellFormed() : "invariant broken in next";
			if (!hasNext())
				throw new NoSuchElementException("No more elements!");
			Appointment result = data[current = next++];
			assert wellFormed() : "invariant broken by next";
			return result;
		}

		@Override // Implementation
		public void remove() {
			assert wellFormed() : "invariant broken in remove";
			checkVersion();
			if (current == next)
				throw new IllegalStateException("Cannot remove now");
			for (int i = current; i + 1 < manyItems; ++i) {
				data[i] = data[i + 1];
			}
			--next;
			--manyItems;
			myVersion = ++version;
			assert wellFormed() : "invariant broken by remove";
		}
	}
}
