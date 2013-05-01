package testunitaire;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import utilities.Decimal;
import utilities.Item;

import market.Currency;
import market.Depth;
import market.ExchangeError;
import market.Market;
import market.Order;
import market.Ticker;
import market.Trades;
import market.Type;
import market.VirtualData;
import market.Wallet;
import market.btce.MarketBtceHistory;
import market.btce.MarketBtceVirtual;
import market.mtgox.MarketMtgoxHistory;
import market.mtgox.MarketMtgoxVirtual;

public class TestMarket {
	
	ArrayList<Market> marketList;
	
	@Before
	void initMarketList() throws ExchangeError{
		marketList =new ArrayList<Market>(3*2+1);
		initHistory();
	}
	
	void initHistory() throws ExchangeError{
		Wallet w_mtgox= new Wallet();
		marketList.add(new MarketMtgoxHistory(Currency.BTC, Currency.USD, w_mtgox, "mtgox25032013.txt"));
		Wallet w_btce= new Wallet();
		marketList.add(new MarketBtceHistory(Currency.BTC, Currency.USD, w_btce, "btce48h_3003.txt"));
	}
	
	void initVirtual() throws ExchangeError{
		Wallet w_mtgox= new Wallet();
		w_mtgox.setAmount(Currency.USD, new Decimal("10000"));
		marketList.add(new MarketMtgoxVirtual(Currency.BTC, Currency.USD, w_mtgox,60000,System.currentTimeMillis()+600000L ));
		
		Wallet w_btce= new Wallet();
		w_btce.setAmount(Currency.USD, new Decimal("10000"));
		marketList.add(new MarketBtceVirtual(Currency.BTC, Currency.USD, w_btce, 60000,System.currentTimeMillis()+600000L));
		
		Wallet w_bitstamp =new Wallet();
		w_bitstamp.setAmount(Currency.USD, new Decimal("10000"));
		marketList.add(new MarketBtceVirtual(Currency.BTC, Currency.USD, w_bitstamp, 60000,System.currentTimeMillis()+600000L));
	}
	
	
	@Test
	public void  testAllMarketImplementations() throws ExchangeError{
		for( Market m:this.marketList){
			testVirtualBid(m);
			testVirtualAsk(m);
		}
	}
	
	private void testVirtualBid(Market m) throws ExchangeError{

		
		Order o1=new Order(new Decimal("1000"), new Decimal("2"),Type.BID);
		Order o2=new Order(new Decimal("999"), new Decimal("2"),Type.BID);
		
		m.addBid(o1);
		Assert.assertEquals("L'ordre"+o1+" devrait etre en debut de liste", o1, m.getOpenBids().getFirst().e);
		
		m.addBid(o2);
		Assert.assertEquals("L'ordre"+o2+" odevrait etre en debut de liste", o2, m.getOpenBids().getFirst().e);
		Assert.assertEquals("Il devrait il y avoir 2 bids", m.getOpenBids().size(), 2);
		
		Item<Order> toRm=m.getOpenBids().getFirst();
		m.cancelOrder(toRm);
		Assert.assertEquals("L'ordre"+o1+" devrait etre en debut de liste", o1, m.getOpenBids().getFirst().e);
		Assert.assertEquals("Il devrait il y avoir 1 bids", m.getOpenBids().size(), 1);
		
	}
	
	private void testVirtualAsk(Market m) throws ExchangeError{

		
		Order o1=new Order(new Decimal("1"), new Decimal("2"),Type.ASK);
		Order o2=new Order(new Decimal("2"), new Decimal("2"),Type.ASK);
		
		m.addAsk(o1);
		Assert.assertEquals("L'ordre"+o1+" devrait etre en debut de liste", o1, m.getOpenAsks().getFirst().e);
		
		m.addAsk(o2);
		Assert.assertEquals("L'ordre"+o2+" odevrait etre en debut de liste", o2, m.getOpenAsks().getFirst().e);
		Assert.assertEquals("Il devrait il y avoir 2 Asks", m.getOpenAsks().size(), 2);
		
		Item<Order> toRm=m.getOpenAsks().getFirst();
		m.cancelOrder(toRm);
		Assert.assertEquals("L'ordre"+o1+" devrait etre en debut de liste", o1, m.getOpenAsks().getFirst().e);
		Assert.assertEquals("Il devrait il y avoir 1 Asks", m.getOpenAsks().size(), 1);
		
	}
}
