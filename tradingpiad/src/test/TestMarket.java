package test;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import utilities.Decimal;
import utilities.Item;
import utilities.Op;

import market.Currency;
import market.ExchangeError;
import market.Market;
import market.Order;
import market.Trade;
import market.Type;
import market.Wallet;
import market.bitstamp.MarketBitstampHistory;
import market.bitstamp.MarketBitstampVirtual;
import market.btce.MarketBtceHistory;
import market.btce.MarketBtceVirtual;
import market.mtgox.MarketMtgoxHistory;
import market.mtgox.MarketMtgoxVirtual;

public class TestMarket {
	
	ArrayList<Market> marketList;
	
	@Before
	public void initMarketList() throws ExchangeError{
		marketList =new ArrayList<Market>(3*2+1);
		initHistory();
	}
	
	public void initHistory() throws ExchangeError{
		Wallet w_mtgox= new Wallet();
		w_mtgox.setAmount(Currency.USD, new Decimal("10000"));
		w_mtgox.setAmount(Currency.BTC, new Decimal("100"));
		marketList.add(new MarketMtgoxHistory(Currency.BTC, Currency.USD, w_mtgox, "falsedatahist.txt"));
		
		Wallet w_btce= new Wallet();
		w_btce.setAmount(Currency.USD, new Decimal("10000"));
		w_btce.setAmount(Currency.BTC, new Decimal("100"));
		marketList.add(new MarketBtceHistory(Currency.BTC, Currency.USD, w_btce, "falsedatahist.txt"));
		
		Wallet w_bitstamp= new Wallet();
		w_bitstamp.setAmount(Currency.USD, new Decimal("10000"));
		w_bitstamp.setAmount(Currency.BTC, new Decimal("100"));
		marketList.add(new MarketBitstampHistory(Currency.BTC, Currency.USD, w_bitstamp, "falsedatahist.txt"));
	}
	
	public void initVirtual() throws ExchangeError{
		Wallet w_mtgox= new Wallet();
		w_mtgox.setAmount(Currency.USD, new Decimal("10000"));
		w_mtgox.setAmount(Currency.BTC, new Decimal("100"));
		marketList.add(new MarketMtgoxVirtual(Currency.BTC, Currency.USD, w_mtgox,60000,System.currentTimeMillis()+600000L ));
		
		Wallet w_btce= new Wallet();
		w_btce.setAmount(Currency.USD, new Decimal("10000"));
		w_btce.setAmount(Currency.BTC, new Decimal("100"));
		marketList.add(new MarketBtceVirtual(Currency.BTC, Currency.USD, w_btce, 60000,System.currentTimeMillis()+600000L));
		
		Wallet w_bitstamp =new Wallet();
		w_bitstamp.setAmount(Currency.USD, new Decimal("10000"));
		w_bitstamp.setAmount(Currency.BTC, new Decimal("100"));
		marketList.add(new MarketBitstampVirtual(Currency.BTC, Currency.USD, w_bitstamp, 60000,System.currentTimeMillis()+600000L));
	}
	
	
	@Test
	public void  testAllMarketImplementations() throws ExchangeError{
		for( Market m:this.marketList){
			testVirtualBid(m);
		}
		initMarketList();
		for( Market m:this.marketList){
			testVirtualAsk(m);
		}
	}
	
	@Test
	public void testsGeneraux(){
		
	}
	
	private void testVirtualBid(Market m) throws ExchangeError{

		Order o1=new Order(new Decimal("1000"), new Decimal("2"),Type.BID);
		Order o2=new Order(new Decimal("900"), new Decimal("2"),Type.BID);
		Order o3=new Order(new Decimal("700"),new Decimal("1"),Type.BID);
		
		m.addBid(o1);
		
		Assert.assertEquals(m.getClass().getName()+": L'ordre au debut de la liste ->", o1,m.getOpenBids().getFirst().e);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 dans le marche ->",new Decimal("2000").compareTo(m.getInMarketCur2())==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 dans le portfeuille ->",new Decimal("8000").compareTo(m.getWallet().getAmount(m.cur2))==0);
		Assert.assertTrue(m.getClass().getName()+" :Qte de cur2 au total ->",new Decimal("10000").compareTo( m.getTotalCur2Amount())==0);
		
		m.addBid(o2);
		
		Assert.assertEquals(m.getClass().getName()+": L'ordre au debut de la liste ->", o1,m.getOpenBids().getFirst().e);
		Assert.assertEquals(m.getClass().getName()+": Nb de bid ->", 2, m.getOpenBids().size());
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 dans le marche ->",new Decimal("3800").compareTo(m.getInMarketCur2())==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 dans le portfeuille ->",new Decimal("6200").compareTo(m.getWallet().getAmount(m.cur2))==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 au total ",new Decimal("10000").compareTo( m.getTotalCur2Amount())==0);
		
		Item<Order> toRm=m.getOpenBids().getFirst();
		m.cancelOrder(toRm);
		
		Assert.assertEquals(m.getClass().getName()+": L'ordre au debut de la liste ->",o2,m.getOpenBids().getFirst().e);
		Assert.assertEquals(m.getClass().getName()+": Nb de bid ->", m.getOpenBids().size(), 1);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 dans le marche ->",new Decimal("1800").compareTo(m.getInMarketCur2())==0);
		Assert.assertTrue(m.getClass().getName()+"Qte de cur2 dans le portfeuille ->",new Decimal("8200").compareTo(m.getWallet().getAmount(m.cur2))==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 au total",new Decimal("10000").compareTo( m.getTotalCur2Amount())==0);
		
		m.addBid(o1);
		
		fakeTrade(m,new Trade(m.getStartTime()+1,new Decimal("800"),new Decimal("1.5"),"id1",Type.BID ));

		
		Assert.assertEquals(m.getClass().getName()+": Nb de bid ->", m.getOpenBids().size(), 2);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 dans le marche ->",new Decimal("2300").compareTo(m.getInMarketCur2())==0);
		BigDecimal nbBtc= m.roundAmount(Op.add(new Decimal("100"),m.subFee(new Decimal("1.5"))));
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 dans le wallet ->expected="+nbBtc+"but is ="+m.getWallet().getAmount(m.cur1),nbBtc.compareTo(m.getWallet().getAmount(m.cur1))==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 au total",new Decimal("8500").compareTo( m.getTotalCur2Amount())==0);
		
		fakeTrade(m,new Trade(m.getStartTime()+2,new Decimal("800"),new Decimal("1"),"id2",Type.BID ));

		
		Assert.assertEquals(m.getClass().getName()+": Nb de bid ->", m.getOpenBids().size(), 1);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 dans le marche ->",new Decimal("1350").compareTo(m.getInMarketCur2())==0);
		
		BigDecimal nbBtc2=m.roundAmount(Op.add(new Decimal("100"),m.subFee(new Decimal("2.5"))));
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 dans le wallet ->expected="+nbBtc2+"but is ="+m.getWallet().getAmount(m.cur1),nbBtc2.compareTo(m.getWallet().getAmount(m.cur1))==0);
		
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 au total",new Decimal("7550").compareTo( m.getTotalCur2Amount())==0);
		
		m.addBid(o3);
		
		fakeTrade(m,new Trade(m.getStartTime()+3,new Decimal("800"),new Decimal("2"),"id3",Type.BID ));
		
		Assert.assertEquals(m.getClass().getName()+": Nb de bid ->", m.getOpenBids().size(), 1);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 dans le marche expected="+new Decimal("700")+" but="+m.getInMarketCur2()+"->",new Decimal("700").compareTo(m.getInMarketCur2())==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 dans le marche ->",Op.add(new Decimal("100"),m.subFee(new Decimal("4"))).compareTo(m.getWallet().getAmount(m.cur1))==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 au total",new Decimal("6200").compareTo( m.getTotalCur2Amount())==0);
	}
	
	private void fakeTrade(Market m, Trade t){
		m.last_trades=new Trade[1];
		m.last_trades[0]= t;
		m.check();
		m.getTrades().add(m.last_trades[0]);
	}
	
	private void testVirtualAsk(Market m) throws ExchangeError{
		Order o1=new Order(new Decimal("800"), new Decimal("2"),Type.ASK);
		Order o2=new Order(new Decimal("900"), new Decimal("2"),Type.ASK);
		Order o3=new Order(new Decimal("1100"),new Decimal("1"),Type.ASK);
		
		m.addAsk(o1);
		
		Assert.assertEquals(m.getClass().getName()+": L'ordre au debut de la liste ->", o1,m.getOpenAsks().getFirst().e);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 dans le marche ->",new Decimal("2").compareTo(m.getInMarketCur1())==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 dans le portfeuille expected="+new Decimal("98")+" bu is="+m.getWallet().getAmount(m.cur1)+"->",new Decimal("98").compareTo(m.getWallet().getAmount(m.cur1))==0);
		Assert.assertTrue(m.getClass().getName()+" :Qte de cur1 au total ->",new Decimal("100").compareTo( m.getTotalCur1Amount())==0);
		
		m.addAsk(o2);
		
		Assert.assertEquals(m.getClass().getName()+": L'ordre au debut de la liste ->", o1,m.getOpenAsks().getFirst().e);
		Assert.assertEquals(m.getClass().getName()+": Nb de ask ->", 2, m.getOpenAsks().size());
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 dans le marche ->",new Decimal("4").compareTo(m.getInMarketCur1())==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 dans le portfeuille ->",new Decimal("96").compareTo(m.getWallet().getAmount(m.cur1))==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 au total ",new Decimal("100").compareTo( m.getTotalCur1Amount())==0);
		
		Item<Order> toRm=m.getOpenAsks().getFirst();
		m.cancelOrder(toRm);
		
		Assert.assertEquals(m.getClass().getName()+": L'ordre au debut de la liste ->",o2,m.getOpenAsks().getFirst().e);
		Assert.assertEquals(m.getClass().getName()+": Nb de ask->", m.getOpenAsks().size(), 1);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 dans le marche ->",new Decimal("2").compareTo(m.getInMarketCur1())==0);
		Assert.assertTrue(m.getClass().getName()+"Qte de cur1 dans le portfeuille ->",new Decimal("98").compareTo(m.getWallet().getAmount(m.cur1))==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 au total",new Decimal("100").compareTo( m.getTotalCur1Amount())==0);
		
		m.addAsk(o1);
		
		fakeTrade(m,new Trade(m.getStartTime()+1,new Decimal("1000"),new Decimal("1.5"),"id1",Type.ASK ));

		
		Assert.assertEquals(m.getClass().getName()+": Nb de ask ->", m.getOpenAsks().size(), 2);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 dans le marche ->",new Decimal("2.5").compareTo(m.getInMarketCur1())==0);
		BigDecimal nbDollar= m.roundAmount(Op.add(new Decimal("10000"),m.subFee(new Decimal("1200"))));
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 dans le wallet ->expected="+nbDollar+"but is ="+m.getWallet().getAmount(m.cur2),nbDollar.compareTo(m.getWallet().getAmount(m.cur2))==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 au total",new Decimal("98.5").compareTo( m.getTotalCur1Amount())==0);
		
		fakeTrade(m,new Trade(m.getStartTime()+2,new Decimal("1000"),new Decimal("1"),"id2",Type.ASK ));

		
		Assert.assertEquals(m.getClass().getName()+": Nb de ask ->", m.getOpenAsks().size(), 1);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 dans le marche expected="+new Decimal("1.5")+" but is "+m.getInMarketCur1()+"->",new Decimal("1.5").compareTo(m.getInMarketCur1())==0);
		
		BigDecimal nbDollar2=m.roundAmount(Op.add(new Decimal("10000"),m.subFee(new Decimal("2050"))));
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 dans le wallet ->expected="+nbDollar2+"but is ="+m.getWallet().getAmount(m.cur2),nbDollar2.compareTo(m.getWallet().getAmount(m.cur2))==0);
		
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 au total",new Decimal("97.5").compareTo( m.getTotalCur1Amount())==0);
		
		m.addAsk(o3);
		
		fakeTrade(m,new Trade(m.getStartTime()+3,new Decimal("1000"),new Decimal("2"),"id3",Type.ASK ));
		
		Assert.assertEquals(m.getClass().getName()+": Nb de ask ->", m.getOpenAsks().size(), 1);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 dans le marche expected="+new Decimal("96")+" but="+m.getInMarketCur1()+"->",new Decimal("1").compareTo(m.getInMarketCur1())==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur2 dans le marche ->",Op.add(new Decimal("10000"),m.subFee(new Decimal("3400"))).compareTo(m.getWallet().getAmount(m.cur2))==0);
		Assert.assertTrue(m.getClass().getName()+": Qte de cur1 au total",new Decimal("96").compareTo( m.getTotalCur1Amount())==0);
	
		
	}
}
