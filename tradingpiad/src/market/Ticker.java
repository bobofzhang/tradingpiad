package market;

import java.math.BigDecimal;

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
