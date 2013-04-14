package market;

import utilities.CircularArray;

/**
 * Une liste des derniers echanges
 *
 */
public class Trades extends CircularArray<Trade>{
	
	/**
	 * @param length Le nombre d'echanges qu'on veut conserver en memoire
	 */
	public Trades(int length) {
		super(Trade.class, length);
	}
}
