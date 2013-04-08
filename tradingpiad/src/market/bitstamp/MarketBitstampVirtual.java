package market.bitstamp;

import java.math.BigDecimal;

import market.Currency;
import market.ExchangeError;
import market.Order;
import market.Trade;
import market.Type;
import market.VirtualData;
import market.Wallet;
import utilities.Item;
import utilities.LinkedList;

public class MarketBitstampVirtual extends MarketBitstampLive {
	private VirtualData virtData;

	public MarketBitstampVirtual(Currency cur1, Currency cur2, Wallet wallet, long timeDelta,long endTime) throws ExchangeError {
		super(cur1, cur2, timeDelta,endTime);
		virtData= new VirtualData(this,wallet);
	}

	@Override
	public LinkedList<Order> getOpenAsks() {
		return virtData.getOpenAsks();
	}

	@Override
	public LinkedList<Order> getOpenBids() {
		return virtData.getOpenBids();
	}

	@Override
	public void addAsk(Order o) {
		virtData.addAsk(o);
	}

	@Override
	public void addBid(Order o) {
		virtData.addBid(o);
	}

	@Override
	public void cancelOrder(Item<Order> item) {
		virtData.cancelOrder(item);
	}

	@Override
	public void check() {
		labelLastTrades();
		virtData.checkNewTrades();
	}

	@Override
	public Wallet getWallet() {
		return virtData.getWallet();
	}
	
	@Override
	public BigDecimal getInMarketCur1(){
		return virtData.getInMarketCur1();
	}
	
	@Override
	public BigDecimal getInMarketCur2(){
		return virtData.getInMarketCur2();
	}
	
	private void labelLastTrades(){
		for(Trade x: this.last_trades){
			if(ticker.sell.subtract(x.price).abs().compareTo(x.price.subtract(ticker.buy).abs())<0)
				x.trade_type=Type.ASK;
			else
				x.trade_type=Type.BID;
		}
	}
	
	public LinkedList<Order> getExecutedBids(){
		return virtData.hist_bids;  
	}
	
	
}
