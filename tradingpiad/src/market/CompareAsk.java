package market;

import java.util.Comparator;


public class CompareAsk implements Comparator<AbstractOrder> {

	@Override
	public int compare(AbstractOrder o1, AbstractOrder o2) {
		return o1.compareTo(o2);
	}

}
