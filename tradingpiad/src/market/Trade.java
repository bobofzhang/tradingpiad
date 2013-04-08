package market;

import java.math.BigDecimal;



public class Trade extends AbstractOrder{
	public long date;
	public String tid;
	
	public Trade(long date, BigDecimal price, BigDecimal amount, String tid,Type type){
		super(price,amount,type);
		this.date=date;
		this.tid=tid;
	}
	
	public Trade(){
		super();
	}
	
	public String toString(){
		return "["+date+","+price+","+amount+","+tid+","+trade_type+"]";
	}

}
