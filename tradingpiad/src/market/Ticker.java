package market;

import java.math.BigDecimal;

/**
 * Infromations correspondant a la requete ticker des boruses d'echanges.
 * Ensemble d'informations concises et generales sur l'etat du marche
 *
 */
public class Ticker {
	public BigDecimal sell, buy, low, high, vol,last;
	
	
	public Ticker(){};
	public Ticker(BigDecimal buy, BigDecimal sell,  BigDecimal low,BigDecimal high, BigDecimal vol, BigDecimal last) {
		this.buy=buy;
		this.sell=sell;
		this.low=low;
		this.high=high;
		this.vol=vol;
		this.last=last;
	}

	public String toString(){
		return "{sell:"+sell+",buy:"+buy+",low:"+low+",high:"+high+",vol:"+vol+",last"+last+"}";
	}
}
