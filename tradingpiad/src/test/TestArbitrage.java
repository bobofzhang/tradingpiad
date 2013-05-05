package test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import market.Currency;
import market.ExchangeError;
import market.Market;
import market.Wallet;
import market.btce.MarketBtceVirtual;

import org.junit.Before;
import org.junit.Test;

import arbitrage.Arbitrage;
import arbitrage.CompetitivePrice;
import arbitrage.CustomEdge;

import strategies.Agent;
import strategies.PriceManager;
import utilities.Decimal;
import utilities.Op;
import static org.junit.Assert.*;

public class TestArbitrage {
	
	Market[] marketList=new Market[3];
	Agent agent;
	Arbitrage strat;
	
	@Before
	public void initMarketList() throws ExchangeError{
		long now =System.currentTimeMillis();
		Wallet w= new Wallet();
		
		w.setAmount(Currency.USD, new Decimal("20000"));
		w.setAmount(Currency.BTC, new Decimal("17"));
		w.setAmount(Currency.LTC, new Decimal("400"));
		
		marketList[0]=new MarketBtceVirtual(Currency.BTC, Currency.USD, w, now, now+60000);
		marketList[1]=new MarketBtceVirtual(Currency.LTC, Currency.USD, w, now, now+60000);
		marketList[2]=new MarketBtceVirtual(Currency.LTC, Currency.BTC, w, now, now+60000);
		marketList[0].updateTicker();
		marketList[1].updateTicker();
		marketList[2].updateTicker();
		
		
		PriceManager priceManager= new CompetitivePrice();
		strat=new Arbitrage(priceManager,marketList);
		agent =new Agent(strat, marketList, w);
		
		
	}
	
	public void setFakeMarketValues1(){
		BigDecimal u=  new Decimal("0.998");
		marketList[0].getTicker().buy= Op.mult(u,new Decimal("10"));
		marketList[0].getTicker().sell= Op.mult(u,new Decimal("102"));
		
		marketList[1].getTicker().buy= Op.mult(u,new Decimal("4"));
		marketList[1].getTicker().sell= Op.mult(u,new Decimal("4"));
		
		marketList[2].getTicker().buy= Op.mult(u,new Decimal("0.04"));
		marketList[2].getTicker().sell= Op.mult(u,new Decimal("0.04"));
	}
	
	@Test
	public void testCheminOptimal1(){
		setFakeMarketValues1();
		List<CustomEdge> l=strat.getSerieEchangesOpt();
		assertEquals(l.size(),2);
		assertTrue(strat.gain(l).compareTo(new Decimal("10.1592408"))==0);
	}
	
	public void setFakeMarketValues2(){
		BigDecimal u=  new Decimal("0.998");
		marketList[0].getTicker().buy= Op.mult(u,new Decimal("100"));
		marketList[0].getTicker().sell= Op.mult(u,new Decimal("102"));
		
		marketList[1].getTicker().buy= Op.mult(u,new Decimal("2"));
		marketList[1].getTicker().sell= Op.mult(u,new Decimal("4"));
		
		marketList[2].getTicker().buy= Op.mult(u,new Decimal("0.03"));
		marketList[2].getTicker().sell= Op.mult(u,new Decimal("0.04"));
	}
	
	@Test
	public void testCheminOptimal2(){
		setFakeMarketValues2();
		List<CustomEdge> l=strat.getSerieEchangesOpt();
		assertEquals(l.size(),3);
		assertTrue(strat.gain(l).compareTo(new Decimal("2.02372890"))==0);
	}
	
	@Test
	public void testMaxInitAmount(){
		setFakeMarketValues2();
		List<CustomEdge> l=strat.getSerieEchangesOpt();
		BigDecimal initAmount=strat.getMaxInitAmount(l);
		System.out.println("l="+l+"\ninitAmount="+initAmount);
		assertTrue(initAmount.compareTo(new Decimal("400"))==0);
	}
}
