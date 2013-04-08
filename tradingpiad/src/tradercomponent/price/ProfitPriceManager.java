package tradercomponent.price;

import java.math.BigDecimal;

import utilities.Assert;
import utilities.Decimal;
import utilities.Op;

import market.Market;
import market.Type;

public class ProfitPriceManager implements PriceManager{
	
	private BigDecimal profit;
	private Market m;
	private BigDecimal wa;
	private BigDecimal wb;
	
	private long nb_sec=300;
	private BigDecimal timeLimit;
	
	public ProfitPriceManager(Market m,BigDecimal profit, long timeLimit){
		Assert.checkPrecond(profit.compareTo(m.getPricePrecision())>0, "Profit trop petit");
		this.profit=profit;
		this.m=m;
		this.timeLimit=new Decimal(timeLimit);
	}

	@Override
	public BigDecimal getBuyPrice() {
		setWeight();
		BigDecimal w=Op.div(wa, wb);
		BigDecimal alpha=m.subFee(m.subFee(Decimal.ONE));
		BigDecimal bid=m.getDepth().bids.length > 0 ? m.getDepth().bids[0].price : m.getTicker().buy;
		BigDecimal ask=m.getDepth().asks.length > 0 ? m.getDepth().asks[0].price : m.getTicker().sell;
		BigDecimal num=Op.sub(Op.sub(profit, Op.mult(Op.mult(alpha, w), bid)),Op.mult(alpha, ask));
		BigDecimal denom=Op.add(Decimal.ONE, Op.mult(alpha, w));
		return m.roundPrice(Op.div(num, denom));
	}

	@Override
	public BigDecimal getSellPrice(BigDecimal buyPrice) {
		return m.roundPrice(Op.div(Op.add(profit, buyPrice),m.subFee(m.subFee(Decimal.ONE))));
	}
	
	private void setWeight(){
		BigDecimal tmp_wa=Decimal.ONE;
		BigDecimal tmp_wb=Decimal.ONE;
		
		int i=m.getTrades().size()-1;
		long dateLim= m.getCurrentTime()/1000-nb_sec;
		while(m.getTrades().get(i).date>dateLim){
			if(m.getTrades().get(i).trade_type.equals(Type.ASK))
				tmp_wa=Op.add(tmp_wa, m.getTrades().get(i).amount);
			else
				tmp_wb=Op.add(tmp_wb, m.getTrades().get(i).amount);
			i--;
		}
		
		wa=Op.sub(Op.mult(tmp_wa,new Decimal("5")),tmp_wb);// On limite a un rapport de 1/5
		wb=Op.sub(Op.mult(tmp_wb,new Decimal("5")),tmp_wa);// On limite a un rapport de 1/5
		
	}

	@Override
	public boolean shouldBuy() {
		BigDecimal buyPrice= getBuyPrice();
		BigDecimal sellPrice= getSellPrice(buyPrice);
		BigDecimal t= new Decimal(this.nb_sec);
		BigDecimal askSpeed= Op.div(wa, t);
		BigDecimal bidSpeed= Op.div(wb, t);
		BigDecimal askVol=m.getDepth().getAskSum(sellPrice);
		BigDecimal bidVol=m.getDepth().getBidSum(buyPrice);
		BigDecimal time= Op.add(Op.div(askVol,askSpeed), Op.div( bidVol,bidSpeed));
		return time.compareTo(timeLimit)<0;
		
	}

	@Override
	public boolean shouldSell() {
		return true;
	}
}
