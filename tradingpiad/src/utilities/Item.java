package utilities;

/**
 * Un item dans une LinkedList
 *
 * @param <E> Le type des donnes satellites
 */
public final class Item<E> {

	public Item<E> previous, next;
	public E e;

	public Item(E e) {
		this(null, e, null);
	}

	public Item(Item<E> previous, E e, Item<E> next) {
		this.e = e;
		this.previous = previous;
		this.next = next;
	}

	public String toString() {
		return this.e.toString();
	}
}