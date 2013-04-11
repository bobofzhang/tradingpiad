package market;

import java.math.BigDecimal;
import java.util.Comparator;

import utilities.Decimal;
import utilities.Op;
import utilities.Util;

public class Depth {

	public Order[] asks;
	public Order[] bids;
	
	public Depth(){
		bids=new Order[0];
		asks=new Order[0];
	}
	
	public void reverseBids(){
		Util.reverse(bids);
	}
	
	public void reverseAsks(){
		Util.reverse(asks);
	}
	
	private static BigDecimal getSum(Order[] tab, Order stopOrder, Comparator<AbstractOrder> comp){
		BigDecimal sum= Decimal.ZERO;
		int i=0;
		while(i<tab.length && comp.compare(tab[i],stopOrder)<0)
			sum=Op.add(sum, tab[i].amount);
		return sum;
	}
	
	public BigDecimal getBidSum(BigDecimal stopPrice){
		return getSum(bids,new Order(stopPrice, Decimal.ONE,Type.BID), new CompareBid());
	}
	
	public BigDecimal getAskSum(BigDecimal stopPrice){
		return getSum(asks,new Order(stopPrice, Decimal.ONE,Type.ASK), new CompareBid());
	}
	
	public String toString(){
		StringBuffer buf= new StringBuffer(30+30*(bids.length+asks.length));
		buf.append("{asks:[");
		for(Order o: asks){
			buf.append(o);
			buf.append(",");
		}
		buf.append("],\n");
		buf.append("bids:[");
		for(Order o: bids){
			buf.append(o);
			buf.append(",");
		}
		buf.append("]}");
		
		return buf.toString();
	}
}
