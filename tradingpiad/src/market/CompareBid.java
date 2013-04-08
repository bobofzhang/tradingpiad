package market;

import java.util.Comparator;


public class CompareBid implements Comparator<AbstractOrder> {
	
	
	@Override
	public int compare(AbstractOrder o1, AbstractOrder o2) {
		return o2.compareTo(o1);
	}

}
