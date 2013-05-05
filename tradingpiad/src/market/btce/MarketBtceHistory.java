package market.btce;

import java.math.BigDecimal;

import market.Currency;
import market.EndOfRun;
import market.ExchangeError;
import market.HistoryData;
import market.Order;
import market.Wallet;
import utilities.Item;
import utilities.LinkedList;


/**
 * Classe permettant de faire une simulation sur des donnees historiques completes (cahier des ordres et historique)
 * En faite, elle ne fait qu'utiliser la classe HistoryData.
 *
 */
public class MarketBtceHistory extends MarketBtce {

	private HistoryData histData;

	public MarketBtceHistory(Currency cur1, Currency cur2, Wallet wallet, String filename) throws ExchangeError {
		super(cur1, cur2);
		histData = new HistoryData(this, wallet, filename);
	}

	@Override
	public LinkedList<Order> getOpenAsks() {
		return histData.getOpenAsks();
	}

	@Override
	public LinkedList<Order> getOpenBids() {
		return histData.getOpenBids();
	}

	@Override
	public void addAsk(Order o) {
		histData.addAsk(o);
	}

	@Override
	public void addBid(Order o) {
		histData.addBid(o);
	}

	@Override
	public void cancelOrder(Item<Order> item) {
		histData.cancelOrder(item);
	}

	@Override
	public void check() {
		histData.checkNewTrades();
	}

	@Override
	public Wallet getWallet() {
		return histData.getWallet();
	}
	@Override
	public BigDecimal getInMarketCur1(){
		return histData.getInMarketCur1();
	}
	
	@Override
	public BigDecimal getInMarketCur2(){
		return histData.getInMarketCur2();
	}

	public LinkedList<Order> getExecutedBids() {
		return histData.hist_bids;
	}

	@Override
	public void nextTimeDelta() throws EndOfRun {
		histData.nextData();
	}
	
	@Override 
	public void sleep(){}

	@Override
	public long getStartTime() {
		return histData.getStartTime();
	}

	@Override
	public long getCurrentTime() {
		return histData.getCurrentTime();
	}

	@Override
	public long getEndTime() {
		return histData.getEndTime();
	}

}
