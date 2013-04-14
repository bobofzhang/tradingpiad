package utilities;

import java.lang.reflect.Array;
import java.util.Iterator;


/**
 * @author 3262553
 * Tableay circulaire destructive.
 * Si plus de place dans la file --> elem le plus vieux supprimmer
 * @param <E> Le type des elements dans la file circulaire
 */
public class CircularArray<E> implements Iterable<E>{
	private int size;
	private int d;
	private int f;
	private E[] tab;
	
	/**
	 * @param componentType Type de données des elements de la file
	 * @param length Capacité de la file
	 */
	@SuppressWarnings("unchecked")
	public CircularArray(Class<E> componentType, int length){
		Assert.nullCheck(componentType);
		tab=(E[]) Array.newInstance(componentType, length);
		size=0;
		d=0;
		f=0;
	}
	
	/**
	 * Ajouter un element.
	 * Si file plein --> suppression de l'élemnt le plus vieux pour mettre le nouveau à la place
	 * @param e
	 */
	public void add(E e){
		Assert.nullCheck(e);
		tab[f]=e;
		if (f== d && size!=0)
			d=(d+1)%tab.length;
		else
			size++;
		f=(f+1)%tab.length;	
	}
	
	/**
	 * @param i
	 * @return Le ieme element du tableau
	 */
	public E get(int i){
		Assert.checkPrecond(i>=0 && i<size,"Index out of bound");
		return tab[(d+i)%tab.length];
	}
	
	/**
	 * @return Le dernier element du tableau
	 */
	public E getLast(){
		return tab[(d+size-1)%tab.length];
	}
	
	/**
	 * @return La taille du tableau
	 */
	public int size(){
		return size;
	}

	@Override
	public Iterator<E> iterator() {
		Iterator<E> it = new Iterator<E>() {

			private int currentIndex = 0;

			@Override
			public boolean hasNext() {
				return currentIndex < size
						&& tab[currentIndex] != null;
			}

			@Override
			public E next() {
				return get(currentIndex++);
			}

			@Override
			public void remove() {
				// Pas besoin
			}
		};
		return it;
	}
	
	public String toString(){
		StringBuffer str=new StringBuffer(1000);
		str.append("{");
		for (E x:this)
			str.append(x+",\n");
		str.append("}");
		return str.toString();
	}
}
