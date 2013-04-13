package market;

import java.math.BigDecimal;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import forecast.TimeSerie;

import utilities.Assert;
import utilities.Item;
import utilities.LinkedList;
import utilities.Op;


public abstract class Market extends Observable{
	public Currency cur1;
	public Currency cur2;
	public Ticker ticker;
	public Depth depth;
	public Trades trades;
	public Trade[] last_trades;
	protected String exchangeName;
	protected String jsonTicker;
	protected String jsonDepth;
	protected String jsonTrades;
	private TimeSerie ts ;
	
	Market() {
		trades = new Trades(500);
		ts = new TimeSerie(500, 600);
	}
	
	public Market(Currency cur1, Currency cur2,String exchangeName){
		Assert.nullCheck(cur1, cur2);
		this.cur1=cur1;
		this.cur2=cur2;
		this.exchangeName=exchangeName;
		trades=new Trades(500);
		ts = new TimeSerie(500, 600);
		
	}
	
	public Ticker getTicker(){
		return ticker;
	}
	public  Depth getDepth(){
		return depth;
	}
	public  Trades getTrades(){
		return trades;
	}
	
	public void updateAll() throws ExchangeError{
	    ExecutorService executor = Executors.newFixedThreadPool(3);
	    Callable<ExchangeError> ticker_callable = new Callable<ExchangeError>() {
	        @Override
	        public ExchangeError call() {
	            try {
					updateTicker();
				} catch (ExchangeError e) {
					return e;
				}
	            return null;
	        }
	    };
	    Future<ExchangeError> ticker_future = executor.submit(ticker_callable);
	    
	    Callable<ExchangeError> trades_callable = new Callable<ExchangeError>() {
	        @Override
	        public ExchangeError call() {
	            try {
					updateTrades();
				} catch (ExchangeError e) {
					return e;
				}
	            return null;
	        }
	    };
	    Future<ExchangeError> trades_future = executor.submit(trades_callable);
	    
	    Callable<ExchangeError> depth_callable = new Callable<ExchangeError>() {
	        @Override
	        public ExchangeError call() {
	            try {
					updateDepth();
				} catch (ExchangeError e) {
					return e;
				}
	            return null;
	        }
	    };
	    Future<ExchangeError> depth_future = executor.submit(depth_callable);
	    
	    executor.shutdown();
	    try {
	    	ExchangeError ticker_ex=ticker_future.get();
			if (ticker_ex != null)
				throw ticker_ex;
	    	ExchangeError trades_ex=trades_future.get();
	    	if (trades_ex != null)
				throw trades_ex;
	    	ExchangeError depth_ex=depth_future.get();
	    	if (depth_ex != null)
				throw depth_ex;
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	    check();
	}
	

	public String getJsonTicker(){
		return jsonTicker;
	}
	public String getJsonDepth(){
		return jsonDepth;
	}
	
	public String getJsonTrades(){
		return jsonTrades;
	}
	
	
	public abstract void updateTicker() throws ExchangeError;
	public abstract void updateDepth() throws ExchangeError;
	public abstract void updateTrades() throws ExchangeError;
	
	public abstract void waitTimeDelta() throws EndOfRun;
	public abstract long getStartTime();
	public abstract long getCurrentTime();
	public abstract long getEndTime();
	
	public abstract LinkedList<Order> getOpenAsks();
	public abstract LinkedList<Order> getOpenBids();
	public abstract LinkedList<Order> getExecutedBids();
	public abstract void addAsk(Order o);
	public abstract void addBid(Order o);
	public abstract void cancelOrder(Item<Order> item);
	public abstract Wallet getWallet();
	public abstract BigDecimal getInMarketCur1();
	public abstract BigDecimal getInMarketCur2();
	
	public BigDecimal getTotalCur1Amount(){
		return Op.add(this.getInMarketCur1(),getWallet().getAmount(cur1));
	}
	public BigDecimal getTotalCur2Amount(){
		return Op.add(this.getInMarketCur2(),getWallet().getAmount(cur2));
	}
	public abstract void check();
	
	public abstract BigDecimal roundPrice(BigDecimal price);
	public abstract BigDecimal roundAmount(BigDecimal amount);
	public abstract BigDecimal getPricePrecision();
	public abstract BigDecimal getAmountPrecision();
	
	public abstract BigDecimal subFee(BigDecimal amount);
	
	public BigDecimal getProfit(BigDecimal ask, BigDecimal bid){
		return Op.sub(subFee(subFee(ask)),bid);
	}

	public String getExchangeName(){
		return exchangeName;
	}
	
	public TimeSerie getTs() {
		return ts;
	}

	public void setTs(TimeSerie ts) {
		this.ts = ts;
	}
	
}
