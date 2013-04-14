package utilities;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Une liste doublement chainee
 *
 * @param <E> Le typed es donnes satellites
 */
public class LinkedList<E> implements Iterable<Item<E>> {
	private Item<E> head;
	private Comparator<? super E> comp;
	private int size;

	/*
	 * creates an empty list
	 */
	public LinkedList(Comparator<? super E> comp) {
		head = new Item<E>(null);
		head.next = head.previous = head;
		this.comp = comp;
		this.size = 0;
	}

	/*
	 * remove all elements in the list
	 */
	public final void clear() {
		head.next = head.previous = head;
	}

	/*
	 * returns true if this container is empty.
	 */
	public final boolean isEmpty() {
		return head.next == head;
	}

	public void insert(E e) {
		Item<E> h = head.next;
		if (!isEmpty()) {
			while (h != head && comp.compare(e, h.e) >= 0) {
				h = h.next;
			}
		}

		Item<E> n = new Item<E>(h.previous, e, h);
		h.previous.next = n;
		h.previous = n;
		size++;

	}

	public void delete(Item<E> item) {
		if (isEmpty()) {
			throw new IndexOutOfBoundsException("empty list.");
		}
		if (item == head) {
			throw new NoSuchElementException("cannot remove the head");
		}
		Assert.checkPrecond(!Assert.isNull(item.previous,item.next),"Item not in a list");
		item.previous.next = item.next;
		item.next.previous = item.previous;
		//item.next = null;
		//item.previous = null;
		size--;
	}

	public Item<E> getFirst() {
		if (isEmpty())
			throw new NoSuchElementException();
		else
			return head.next;
	}

	public int size() {
		return size;
	}

	@Override
	public Iterator<Item<E>> iterator() {

		
		if(isEmpty()){
			return Collections.<Item<E>>emptyList().iterator();
		}
		Iterator<Item<E>> it = new Iterator<Item<E>>() {

			private Item<E> current = head;

			@Override
			public boolean hasNext() {
				return current.next != head;
			}

			@Override
			public Item<E> next() {
				current = current.next;
				return current;
			}

			@Override
			public void remove() {
				// Pas besoin
			}
		};
		return it;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer(size * 25);
		buf.append("{");
		for (Item<E> x : this) {
			buf.append(x + ",");
		}
		buf.append("}");
		return buf.toString();
	}
	

  
 
 /*
  * insert element before current position
  */
  /*
  public final synchronized void insertBefore(E obj, ListIterator cursor) {
	  Item<E> newItem = new Item<E>(cursor.pos.previous, obj, cursor.pos);
    newItem.previous.next = newItem;
    cursor.pos.previous = newItem;
  }*/

 /*
  * remove the element at current position
  */
  /*
  public final synchronized void remove(ListIterator cursor) {
    if (isEmpty()) {
      throw new IndexOutOfBoundsException("empty list.");
    }
    if (cursor.pos == head) {
      throw new NoSuchElementException("cannot remove the head");
    }
    cursor.pos.previous.next = cursor.pos.next;
    cursor.pos.next.previous = cursor.pos.previous;
  }*/


  
  
 /*
  * Return an iterator positioned at the head.
  */
  /*
  public final ListIterator head() {
    return new ListIterator(this, head);
  }*/

 /*
  * find the first occurrence of the object in a list
  */
  /*
  public final synchronized ListIterator find(Order obj) {
    if (isEmpty()) {
      throw new IndexOutOfBoundsException("empty list.");
    }
    ListItem pos = head;
    while (pos.next != head) {  // There are still elements to be inspected
      pos = pos.next;
      if (pos.order == obj) {
        return new ListIterator(this, pos);
      }
    }
    throw new NoSuchElementException("no such object found");
  }*/

 /*
  * Returns an enumeration of the elements. Use the Enumeration methods on
  * the returned object to fetch the elements sequentially.
  */
  /*
  public final synchronized Enumeration elements() {
    return new ListEnumerator(this);
  }*/

  
  /*
   * insert element after current position
   */
  /*
   public final synchronized void insertAfter(Order obj, ListIterator cursor) {
 	  Item newItem = new Item(cursor.pos,  obj, cursor.pos.next);
     newItem.next.previous = newItem;
     cursor.pos.next = newItem;
   }*/
}