import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.SwingWrapper;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.StyleManager.LegendPosition;

import forecast.TestIndicator;

import strategies.Agent;
import strategies.Strategy;
import strategies.StrategyObserver;
import strategies.marketmaking.ForecastStrategy;
import strategies.marketmaking.AchatVente;
import strategies.marketmaking.ProfitMarketMaking;
import utilities.CircularArray;
import utilities.Decimal;
import utilities.Item;
import market.Currency;
import market.DataRetriever;
import market.ExchangeError;
import market.Market;
import market.MarketPast;
import market.Order;
import market.Trade;
import market.Type;
import market.Wallet;
import market.bitstamp.MarketBitstampVirtual;
import market.btce.MarketBtceHistory;
import market.btce.MarketBtceVirtual;
import market.mtgox.MarketMtgoxHistory;
import market.mtgox.MarketMtgoxVirtual;

public class Main2 {
	public static void main(String[] args) throws ExchangeError, NoSuchAlgorithmException, KeyManagementException, IOException {
		
		// Pour que ca fonctionne a l'univ !
		System.setProperty("java.net.useSystemProxies", "true");
		
		// Pour r�soudre le probleme des certificats
		X509TrustManager trm = new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {

            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[] { trm }, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        
        System.setProperty("http.agent", "");
		
		Wallet wal=new Wallet();
		wal.setAmount(Currency.USD, new BigDecimal(1000.0));
		wal.setAmount(Currency.BTC, new BigDecimal(10.0));
		Market m =new MarketBitstampVirtual(Currency.BTC, Currency.USD,wal,60000,System.currentTimeMillis()+600000);
		
		// TEST REQUETE
		/*
		m.updateTicker();
		System.out.println(m.ticker);
		m.updateTrades();
		m.check();
		System.out.println(m.trades);
		m.updateDepth();
		System.out.println(m.depth);*/
		
		
		
		/*
		long t=System.currentTimeMillis();
		m.updateAll();
		System.out.println(System.currentTimeMillis()-t);
		System.out.println(m.getTicker().buy);
		System.out.println(m.getTrades());
		System.out.println(m.getDepth().bids[0]);
		*/
		
		/*
		Order o= new Order(new Decimal("19"),new Decimal("1"),Type.BID);
		Order o2= new Order(new Decimal("19"),new Decimal("1"),Type.BID);
		Order o3= new Order(new Decimal("19"),new Decimal("1"),Type.BID);
		
		m.addBid(o2);
		m.addBid(o3);
		m.addBid(o2);
		Order o4= new Order(new Decimal("19"),new Decimal("1"),Type.BID);
		m.addBid(o4);
		System.out.println(m.getOpenBids());
		System.out.println("tot1="+m.getTotalCur1Amount());
		System.out.println("tot2="+m.getTotalCur2Amount());
		
		
		o= new Order(new Decimal("19"),new Decimal("1"),Type.ASK);
		o2= new Order(new Decimal("19"),new Decimal("1"),Type.ASK);
		o3= new Order(new Decimal("19"),new Decimal("1"),Type.ASK);
		
		m.addAsk(o);
		m.addAsk(o2);
		m.addAsk(o3);
		m.addAsk(o2);
		o4= new Order(new Decimal("19"),new Decimal("1"),Type.ASK);
		m.addAsk(o4);
		System.out.println("tot1="+m.getTotalCur1Amount());
		System.out.println("tot2="+m.getTotalCur2Amount());
		
		System.out.println(m.getOpenAsks());
		
		
		Item<Order> toRemove=m.getOpenAsks().getFirst();
		m.cancelOrder(toRemove);
		
		toRemove=m.getOpenAsks().getFirst();
		m.cancelOrder(toRemove);
		m.cancelOrder(toRemove);
		System.out.println("tot1="+m.getTotalCur1Amount());
		System.out.println("tot2="+m.getTotalCur2Amount());
		
		toRemove=m.getOpenAsks().getFirst();
		m.cancelOrder(toRemove);
		
		toRemove=m.getOpenAsks().getFirst();
		m.cancelOrder(toRemove);
		
		m.addAsk(o4);
		
		System.out.println(m.getOpenAsks());
		*/
		
		// TEST CHECK
		/*
		System.out.println(m.getWallet());
		long t=System.currentTimeMillis();
		Order o= new Order(new BigDecimal("10.0"),new BigDecimal("1.0"),Type.ASK);
		m.addBid(new Order(new BigDecimal("100"),new BigDecimal("1.0"),Type.BID));
		
		m.addAsk(o);
		System.out.println(m.getWallet());
		m.updateTicker(); // pr bitstamp qui a besoin du ticker pour labeller l'historique
		m.updateTrades();
		m.check();
		//System.out.println(m.trades);
		System.out.println(m.getWallet());
		m.addAsk(new Order(new BigDecimal("5"),new BigDecimal("1.0"),Type.ASK));
		m.addBid(new Order(new BigDecimal("100"),new BigDecimal("1.0"),Type.BID));
		System.out.println(m.getWallet());
		try {
			Thread.sleep( 60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m.updateTrades();
		m.updateTicker(); // pr bitstamp qui a besoin du ticker pour labeller l'historique
		m.check();
		System.out.println(System.currentTimeMillis()-t);
		System.out.println(m.getWallet());
		*/
		
		
		/*
		CircularArray<Double> c= new CircularArray<Double>(Double.class,10);
		
		c.add(new Double(1.0));
		c.add(new Double(2.0));
		c.add(new Double(3.0));
		c.add(new Double(4.0));
		c.add(new Double(5.0));
		
		for(Double d: c){
			System.out.println(d.toString());
		}*/
		
		// TEST THREADING
		
		/*
		m.updateAll();
		System.out.println(m.getTicker());
		System.out.println(m.getDepth());
		System.out.println(m.getTrades());
		System.out.println(m.last_trades.length);*/
		
	
		
		//runStrat();
        //TestIndicator.test();
		
		/*try {
			MarketPast.retrieveMtgox("mtgox10042013_13042013.txt", Currency.USD, "10/04/2013", "13/04/2013");
		} catch (ParseException e) {
			System.out.println("erreur dates");
		}*/
		
		
		
		//runStratPast("mtgox01082012_15082012.txt");
		//runStratPast("mtgox10042013_13042013.txt");
        
		//runStrat();
		
	}
	
	public static void runStratPast(String filename) throws ExchangeError{
		Wallet wal=new Wallet();
		wal.setAmount(Currency.USD, new BigDecimal("1000"));
		wal.setAmount(Currency.BTC, new BigDecimal("0"));
		Market m =new MarketPast(filename,wal,60000);
		
		
		//Strategy mmaking= new ProfitMarketMaking(m, new Decimal("0.0"), 3600, new Decimal("0.1"));
		Strategy mmaking= new AchatVente();
		//Strategy mmaking = new ForecastStrategy(new Decimal("100"));
		Agent a=new Agent(mmaking,m,wal);
		StrategyObserver o=new StrategyObserver(6*3600000,Currency.USD);
		a.addObserver(o);
		a.execute();
		 new SwingWrapper(o.getChart()).displayChart();
		 new SwingWrapper(m.getTs().getChart()).displayChart();
		 
		 System.out.println("maxdd="+o.maxDrawDown());
	}
	
	
	public static void runStrat() throws ExchangeError{
		Wallet wal=new Wallet();
		wal.setAmount(Currency.USD, new BigDecimal(1000.0));
		//Market m =new MarketMtgoxVirtual(Currency.BTC, Currency.USD,wal,30000,System.currentTimeMillis()+48*60*60000);
		
		Market m =new MarketBtceHistory(Currency.BTC, Currency.USD,wal,"btce48h_3003.txt");
		
		
		//DataRetriever d= new DataRetriever("mtgox48h_3003.txt","tradehist_mtgox48h_3003.txt",m);
		//d.retrieve();
		
		
		Strategy mmaking= new ProfitMarketMaking(m, new Decimal("0.001"), 30, new Decimal("0.2"));
		Agent a=new Agent(mmaking,m,wal);
		StrategyObserver o=new StrategyObserver(6*3600000,Currency.USD);
		a.addObserver(o);
		a.execute();
		 new SwingWrapper(o.getChart()).displayChart();
		 System.out.println("maxdd="+o.maxDrawDown());
		

	}
	

}

