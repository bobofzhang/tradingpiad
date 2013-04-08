package market;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.NoSuchElementException;


import utilities.Assert;
import utilities.Decimal;
import utilities.Item;
import utilities.LinkedList;
import utilities.Op;


public class VirtualData {
	
	private Wallet wallet;
	protected Market market;
	private BigDecimal cur1Amount, cur2Amount;
	
	 
	
	//1st impelementation
	//private List<Order> asks = new ArrayList<Order>();
	//private List<Order> bids = new ArrayList<Order>();

	//Here i'm simply testing a new approach 
	private LinkedList<Order> linked_asks = new LinkedList<Order>(new CompareAsk());
	private LinkedList<Order> linked_bids = new LinkedList<Order>(new CompareBid());
	
	public LinkedList<Order> hist_bids = new LinkedList<Order>(new CompareBid());
	//private LinkedList<Order> hist_asks = new LinkedList<Order>(new CompareBid());

	
	public VirtualData(Market market, Wallet wallet) {
		Assert.nullCheck(market,wallet);
		this.wallet=wallet;
		this.market=market;
		this.cur1Amount=Decimal.ZERO;
		this.cur2Amount=Decimal.ZERO;
	}

	public void addBid(Order o){
		Assert.nullCheck(o);
		Assert.checkPrecond(!o.trade_type.equals(Type.ASK), "Wrong trade type, should be BID");
		o.price=market.roundPrice(o.price);
		o.amount=market.roundAmount(o.amount);
		BigDecimal x=Op.mult(o.price, o.amount);
		wallet.setAmount(market.cur2, Op.neg(x));
		cur2Amount=Op.add(cur2Amount, x);
		linked_bids.insert(o);
		//bids.add(o);
		//Collections.sort(bids);
	}
	
	public void addAsk(Order o){
		Assert.nullCheck(o);
		Assert.checkPrecond(!o.trade_type.equals(Type.BID), "Wrong trade type, should be ASK");
		o.price=market.roundPrice(o.price);
		o.amount=market.roundAmount(o.amount);
		wallet.setAmount(market.cur1, Op.neg(o.amount));
		cur1Amount=Op.add(cur1Amount, o.amount);
		linked_asks.insert(o);
		//asks.add(o);		
		//Collections.sort(asks) ;
	}
	
	//public List<Order> getAsks(){return asks;}
	//public List<Order> getBids(){return bids;}
	
	public LinkedList<Order> getOpenAsks() { return linked_asks ; }
	public LinkedList<Order> getOpenBids() { return linked_bids ; }
	

	
	public void cancelOrder(Item<Order> item){
		if (item.e.trade_type == Type.ASK){
			linked_asks.delete(item);
			wallet.setAmount(market.cur1, item.e.amount);
			this.cur1Amount=Op.sub(this.cur1Amount, item.e.amount);
		}
		
		if(item.e.trade_type == Type.BID){
			linked_bids.delete(item);
			BigDecimal x=Op.mult(item.e.amount,item.e.price);
			wallet.setAmount(market.cur2, x);
			this.cur2Amount=Op.sub(this.cur2Amount, x);
		}		
	}
	
	public BigDecimal getInMarketCur1(){
		return this.cur1Amount;
	}
	
	public BigDecimal getInMarketCur2(){
		return this.cur2Amount;
	}
	
	
	public Wallet getWallet(){
		return wallet;
		
	}
	
	public void checkNewTrades() {	
		Assert.nullCheck(market.last_trades, market.trades);
		final LinkedList<Order> pendingAsks = this.getOpenAsks();
		final LinkedList<Order> pendingBids = this.getOpenBids();
		final Comparator<AbstractOrder> compBid = new CompareBid();
		final Comparator<AbstractOrder> compAsk = new CompareAsk();

		Trade current; // Echange en cours de traitement
		Comparator<AbstractOrder> comp;// Comparateur correspondant 
		LinkedList<Order> pending; 
		Item<Order> best_item;
		Order best;
		BigDecimal amount_executed;
		BigDecimal current_amount;
		Currency cur;
		for (int i = 0; i < market.last_trades.length; i++) {
			current = market.last_trades[i];
			System.out.println("aj"+current);
			current_amount = current.amount;
			if (current.trade_type.equals(Type.ASK)) {
				comp = compAsk;
				pending = pendingAsks;
				cur=market.cur2;
			} else {
				comp = compBid;
				pending = pendingBids;
				cur=market.cur1;
			}
			
			try {
				best_item = pending.getFirst();
				best = best_item.e;

				while (comp.compare(best, current) <= 0 && current_amount.compareTo(market.getAmountPrecision())>0) {
					amount_executed = (best.amount.compareTo(current_amount)<0) ? best.amount
							: current_amount;
					current_amount = Op.sub(current_amount,amount_executed);
					best.amount =Op.sub(best.amount,amount_executed);
					
					if(pending == pendingBids){
						Order executed_order= new Order(best.price,amount_executed,Type.BID);
						hist_bids.insert(executed_order);
					}
					
					if (current.trade_type.equals(Type.ASK))
						wallet.setAmount(cur, Op.mult(amount_executed,best.price));
					else
						wallet.setAmount(cur, amount_executed);
					
					
					if (best.amount.compareTo(market.getAmountPrecision())<=0) {
						pending.delete(best_item);
						best_item = pending.getFirst();
						best = best_item.e;
					}
				}

			} catch (NoSuchElementException e) {
				// Rien a faire, juste passer � l'echange suivant
			}
			market.trades.add(current);
			market.getTs().feed(current);
		}
	}

	
}
