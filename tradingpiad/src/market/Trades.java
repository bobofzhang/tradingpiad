package market;

import utilities.CircularArray;

public class Trades extends CircularArray<Trade>{
	
	public Trades(int length) {
		super(Trade.class, length);
	}
}
