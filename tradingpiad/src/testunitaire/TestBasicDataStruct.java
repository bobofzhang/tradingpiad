package testunitaire;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import forecast.TSPoint;
import forecast.TimeSerie;

import utilities.CircularArray;
import utilities.Decimal;
import utilities.Op;

import arbitrage.BestMatchingDeals;
import market.Currency;
import market.Market;
import market.Trade;
import market.Type;
import market.VirtualData;
import market.Wallet;

public class TestBasicDataStruct {
	
	@Rule
	 public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testWallet() {
		Wallet w= new Wallet();
		assertNotNull("Tableau doit etre non null apres contruction",w.wallet);
		for(Currency cur:Currency.values())
			assertTrue(cur+" doit =0 apres contruction", w.getAmount(cur).equals(Decimal.ZERO));
		
		w.setAmount(Currency.USD, Decimal.TEN);
		assertTrue("erreur -il devrait il y avoir 10 dollar",w.getAmount(Currency.USD).equals(Decimal.TEN));
		
		w.setAmount(Currency.USD, Op.neg(Decimal.ONE));
		assertTrue("erreur -il devrait il y avoir 9 dollar",w.getAmount(Currency.USD).equals(new Decimal("9")));
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Action interdite: Pas suffisamment de");
		w.setAmount(Currency.USD, Op.neg(Decimal.TEN));
	}
	
	
	@Test
	public void testCircularArray(){
		CircularArray<Double> c= new CircularArray<Double>(Double.class,3);
		Double d1 =new Double(1.0);
		Double d2 =new Double(2.0);
		Double d3 =new Double(3.0);
		Double d4 =new Double(4.0);
		
		c.add(d1);
		assertEquals("Erreur : la taille devrait etre 1",c.size(),1);
		
		assertEquals("Erreur le premier element devrait etre d1=new Double (1.0)", c.get(0),d1);
		
		assertEquals("Erreur : d1=new Double (1.0) devrait etre le dernier element",c.get(0),c.getLast());
		
		
		c.add(d2);
		
		assertEquals("Erreur : la taille devrait etre 2",c.size(),2);
		assertEquals("Erreur le premier element devrait etre d1=new Double (1.0)",c.get(0),d1);
		assertEquals("Erreur le second element devrait etre d2=new Double (2.0)",c.get(1),d2);
		
		assertEquals("Erreur : d2=new Double (2.0) devrait etre le dernier element",c.get(1),c.getLast());
		
		
		c.add(d3);
		assertEquals("Erreur : la taille devrait etre 3",c.size(),3);
		
		assertEquals("Erreur le premier element devrait etre d1=new Double (1.0)",c.get(0),d1);
		assertEquals("Erreur le second element devrait etre d2=new Double (2.0)",c.get(1),d2);
		assertEquals("Erreur le 3eme element devrait etre d3=new Double (3.0)",c.get(2),d3);
		
		assertEquals("Erreur : d3=new Double (3.0) devrait etre le dernier element",c.get(2),c.getLast());
		
		c.add(d3);
		assertEquals("Erreur : la taille devrait etre 3",c.size(),3);
		
		assertEquals("Erreur le premier element devrait etre d2=new Double (2.0)",c.get(0),d2);
		assertEquals("Erreur le second element devrait etre d3=new Double (3.0)",c.get(1),d3);
		assertEquals("Erreur le 3eme element devrait etre d4=new Double (4.0)",c.get(2),d4);
		
		assertEquals("Erreur : d4=new Double (4.0) devrait etre le dernier element",c.get(2),c.getLast());
		
		
	}
	
	@Test
	public void testTimeSerie(){
		TimeSerie ts= new TimeSerie(5,10);
		int i=1;
		Type t=Type.BID;
		TSPoint newPoint;
		
		newPoint=ts.feed(new Trade(500, new Decimal("10"),new Decimal("1"), "id"+(i++),t));
		assertEquals("La ts devrait etre de taille 0", ts.size(), 0);
		assertNull("Devrait etre null: aucun point devrait etre cree", newPoint);
		
		
		
		newPoint=ts.feed(new Trade(501, new Decimal("20"),new Decimal("2"), "id"+(i++),t));
		assertEquals("La ts devrait etre de taille 0", ts.size(), 0);
		assertNull("Devrait etre null: aucun point devrait etre cree", newPoint);
		
		
		newPoint=ts.feed(new Trade(510,new Decimal("15.5"),new Decimal("0.25"), "id"+(i++),t));
		assertEquals("La ts devrait etre de taille 1", ts.size(), 1);
		assertNotNull("Un point devrait etre cree", newPoint);
		
		assertEquals("La volume devrait etre 3",newPoint.getVolume().equals(new Decimal("3")));
		assertEquals("Le low devrait etre 10",newPoint.getLow().equals(new Decimal("10")));
		assertEquals("Le high devrait etre 20",newPoint.getHigh().equals(new Decimal("20")));
		assertEquals("Le close devrait etre 20",newPoint.getClose().equals(new Decimal("20")));
		assertEquals("Le open devrait etre 20",newPoint.getOpen().equals(new Decimal("10")));
		
		newPoint=ts.feed(new Trade(520, new Decimal("13.5"),new Decimal("4.75"), "id"+(i++),t));
		assertEquals("La ts devrait etre de taille 2", ts.size(), 2);
		assertNotNull("Un point devrait etre cree", newPoint);
		
		assertEquals("La volume devrait etre 0.25",newPoint.getVolume().equals(new Decimal("0.25")));
		assertEquals("Le low devrait etre 15.5",newPoint.getLow().equals(new Decimal("15.5")));
		assertEquals("Le high devrait etre 15.5",newPoint.getHigh().equals(new Decimal("15.5")));
		assertEquals("Le close devrait etre 15.5",newPoint.getClose().equals(new Decimal("15.5")));
		assertEquals("Le open devrait etre 15.5",newPoint.getOpen().equals(new Decimal("15.5")));
		
		newPoint=ts.feed(new Trade(550, new Decimal("10"),new Decimal("1"), "id"+(i++),t));
		assertEquals("La ts devrait etre de taille 5", ts.size(), 5);
		assertNotNull("Un point devrait etre cree", newPoint);
		
		assertEquals("La volume devrait etre 0",newPoint.getVolume().equals(new Decimal("0")));
		assertEquals("Le low devrait etre +Infini",newPoint.getLow().equals(new Decimal(Double.POSITIVE_INFINITY)));
		assertEquals("Le high devrait etre 0",newPoint.getHigh().equals(new Decimal("0")));
		assertEquals("Le close devrait etre 0",newPoint.getClose().equals(new Decimal("0")));
		assertEquals("Le open devrait etre 0",newPoint.getOpen().equals(new Decimal("0")));
		
	}
}
