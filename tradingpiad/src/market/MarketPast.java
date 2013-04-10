package market;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import org.codehaus.jackson.map.ObjectMapper;

import market.mtgox.MarketMtgox;

import utilities.Assert;
import utilities.Decimal;
import utilities.Item;
import utilities.LinkedList;
import utilities.Op;
import utilities.Util;

public class MarketPast extends Market{
	
	VirtualData virtData;
	
	private String filename;
	private String runName;	
	
	private long startTime;
	private long endTime;
	private long curTime;
	private long timeDelta;

	private Decimal pricePrec;
	private Decimal amountPrec;
	private int priceScale;
	private int amountScale;
	private Decimal feePercent;
	
	private InputStream in;
	private BufferedReader br;
	
	private Trade currentTrade;
	private ArrayList<Trade> new_trades;

	private boolean endofrun;
	

	public MarketPast(String filename,Wallet wallet,long timeDelta) throws ExchangeError {
		super();
		Assert.checkPrecond(timeDelta>0, "Erreur timeDeltra doit etre > a 0");
		virtData= new VirtualData(this, wallet);
		this.filename = filename;
		this.timeDelta=timeDelta;

		try {
			FileInputStream fstream = new FileInputStream(this.filename);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));

			runName = br.readLine(); // Ligne 1: Le nom du run
			super.exchangeName = br.readLine(); // Ligne 2: Le nom de la bourse d'echange
			this.cur1=Currency.valueOf(br.readLine());
			this.cur2=Currency.valueOf(br.readLine());
			
			this.pricePrec= new Decimal(br.readLine());
			this.amountPrec= new Decimal(br.readLine());;
			this.priceScale=Integer.parseInt(br.readLine());
			this.amountScale=Integer.parseInt(br.readLine());
			this.feePercent=new Decimal(br.readLine());
			
			startTime = Long.parseLong(br.readLine()); // Ligne 3: L'heure du debut du run en timestamp (celui qu'on recupere avec System.currentTimeMillis() )
			endTime = Long.parseLong(br.readLine()); // Ligne 4: L'heure du debut du run en timestamp (celui qu'on recupere avec System.currentTimeMillis() )
			
			curTime=startTime+1;
			
			this.ticker=new Ticker();
			this.depth=new Depth();
			this.depth.asks=new Order[1];
			this.depth.bids=new Order[1];
			
			try{
				currentTrade=currentTrade(br.readLine());
			}catch(IOException e){
				e.printStackTrace();
				System.exit(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void updateTicker() throws ExchangeError {
	}

	@Override
	public void updateDepth() throws ExchangeError {
	}

	@Override
	public void updateTrades() throws ExchangeError {
		new_trades = new ArrayList<Trade>(100);
		
		while (currentTrade !=null && currentTrade.date <= this.curTime / 1000) {

				new_trades.add(currentTrade);
				
				if(currentTrade.trade_type.equals(Type.ASK)){
					ticker.sell=currentTrade.price;
					depth.asks[0]=new Order(currentTrade.price,currentTrade.amount,Type.ASK);
				}
				else{
					ticker.buy=currentTrade.price;
					depth.bids[0]=new Order(currentTrade.price,currentTrade.amount,Type.BID);
				}
				ticker.last=currentTrade.price;
				
				if(currentTrade.price.compareTo(ticker.low)<0)
					ticker.low=currentTrade.price;
				
				if(currentTrade.price.compareTo(ticker.high)<0)
					ticker.high=currentTrade.price;
				ticker.vol=Op.div(Op.add(ticker.vol, currentTrade.amount),new Decimal(curTime-startTime));
				
				
				try {
					currentTrade=currentTrade(br.readLine());
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
		}
		
		super.last_trades=new Trade[new_trades.size()];
		for(int i=0;i<new_trades.size();i++)
			super.last_trades[i]=new_trades.get(i);
		
	}

	@Override
	public void waitTimeDelta() throws EndOfRun {
		if(currentTrade==null)
			throw new EndOfRun();
		this.curTime += this.timeDelta;
	}

	@Override
	public long getStartTime() {
		return this.startTime;
	}

	@Override
	public long getCurrentTime() {
		return this.curTime;
	}

	@Override
	public long getEndTime() {
		return this.endTime;
	}

	@Override
	public LinkedList<Order> getOpenAsks() {
		return this.virtData.getOpenAsks();
	}

	@Override
	public LinkedList<Order> getOpenBids() {
		return this.getOpenBids();
	}

	@Override
	public LinkedList<Order> getExecutedBids() {
		return this.getExecutedBids();
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
	public Wallet getWallet() {
		return virtData.getWallet();
	}

	@Override
	public BigDecimal getInMarketCur1() {
		return virtData.getInMarketCur1();
	}

	@Override
	public BigDecimal getInMarketCur2() {
		return virtData.getInMarketCur2();
	}

	@Override
	public void check() {
		this.virtData.checkNewTrades();
		
	}

	public BigDecimal subFee(BigDecimal amount) {
		return Op.mult(Op.sub(Decimal.ONE, feePercent), amount);
	}

	public BigDecimal getPricePrecision() {
		return pricePrec;
	}

	public BigDecimal getAmountPrecision() {
		return amountPrec;
	}

	public BigDecimal roundPrice(BigDecimal price) {
		return price.setScale(priceScale, RoundingMode.FLOOR);
	}

	public BigDecimal roundAmount(BigDecimal amount) {
		return amount.setScale(amountScale, RoundingMode.FLOOR);
	}
	
	public static Trade currentTrade(String line){
		if(line != null){
			String [] tab=line.split(",");
			return new Trade(Long.parseLong(tab[0]), new Decimal(tab[1]),new Decimal(tab[2]), tab[3],Type.valueOf(tab[4]));
		}
		else
			return null;
	}
	
	
	public static void retrieveMtgox(String filename, Currency cur, String startDateStr, String endDateStr) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date startDate = dateFormat.parse(startDateStr);
		Date endDate = dateFormat.parse(endDateStr);

		try {
			startDate = dateFormat.parse(startDateStr);
			endDate = dateFormat.parse(endDateStr);

			long start = startDate.getTime();
			long end = endDate.getTime();
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(filename + "\n");
			out.write("mtgox\n");
			out.write("BTC");
			out.write(cur.toString());
			out.write("0.00001");
			out.write("0.00000001");
			out.write("5");
			out.write("8");
			out.write("0.6");
			out.write(String.valueOf(start));
			out.write(String.valueOf(end));

			String last_tid = String.valueOf(start);
			long last_time = start;

			ObjectMapper mapper = MarketMtgox.produceMapper(Currency.BTC, cur);

			while (last_time < end) {
				URL url = new URL("http://data.mtgox.com/api/0/data/getTrades.php?Currency=" + cur + "&since=" + last_tid);
				try {
					String json = Util.getData(url);

					Trade[] last_trades = mapper.readValue(json, Trade[].class);
					for (Trade t : last_trades)
						out.write(String.valueOf(t.date) + t.price.toString() + t.amount.toString() + t.tid + t.trade_type.toString() + "\n");
					last_tid = last_trades[last_trades.length - 1].tid;
					last_time = last_trades[last_trades.length - 1].date * 1000;
				} catch (ExchangeError e) {
					Thread.sleep(10000);
					continue;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

	}

		

}
