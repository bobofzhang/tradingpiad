package market;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.NoSuchElementException;


import utilities.Assert;
import utilities.Decimal;
import utilities.Item;
import utilities.LinkedList;
import utilities.Op;


/**
 * Classe representant les donnees virtuelles lorsqu'on fait une simulation.
 * Les donnes des ordres sont constitues des ordres virtuels de l'agent.
 * Cette classe implemente toutes les operations permettant de manipuler ces ordres virtuels:
 * - placer/retirer des ordres
 * - verifier si les ordres laces auraient pu etre executes
 */
public class VirtualData {
	
	private Wallet wallet; // Le portefeuille associe au marche.
	protected Market market; // Le marche concerne par ces donnes virtuelles

	private LinkedList<Order> linked_asks = new LinkedList<Order>(new CompareAsk()); // La liste des ASK virtuels de l'agent trader
	private LinkedList<Order> linked_bids = new LinkedList<Order>(new CompareBid()); // La liste des BID virtuels de l'agent trader
	
	public LinkedList<Order> hist_bids = new LinkedList<Order>(new CompareBid()); // l'historique des BID executes
	
	//private LinkedList<Order> hist_asks = new LinkedList<Order>(new CompareBid());

	
	public VirtualData(Market market, Wallet wallet) {
		Assert.nullCheck(market,wallet);
		this.wallet=wallet;
		this.market=market;
	}

	
	
	/**
	 * 
	 * Ajouter un ordre dans le cahier des ordres virtuel
	 * @param o Un ordre de type BID
	 */
	public void addBid(Order o){
		Assert.nullCheck(o);
		Assert.checkPrecond(!o.trade_type.equals(Type.ASK), "Wrong trade type, should be BID");
		
		//Arrondis  selon la precision du marche
		o.price=market.roundPrice(o.price);
		o.amount=market.roundAmount(o.amount);
		
		// On retire du wallet l'argent mise dans le marche
		BigDecimal x=market.roundPrice(Op.mult(o.price, o.amount));
		wallet.setAmount(market.cur2, Op.neg(x));
		
		linked_bids.insert(o);
	}
	
	/**
	 *  Ajouter un ordre dans le cahier des ordres virtuel
	 * @param o Un ordre de type ASK
	 */
	public void addAsk(Order o){
		Assert.nullCheck(o);
		Assert.checkPrecond(!o.trade_type.equals(Type.BID), "Wrong trade type, should be ASK");
		
		//Arrondis  selon la precision du marche
		o.price=market.roundPrice(o.price);
		o.amount=market.roundAmount(o.amount);
		
		// On retire du wallet l'argent mise dans le marche
		wallet.setAmount(market.cur1, Op.neg(o.amount));
		
		linked_asks.insert(o);
	}
	
	/**
	 * @return La liste des asks virtuels
	 */
	public LinkedList<Order> getOpenAsks() { return linked_asks ; }
	
	/**
	 * @return La liste des bids virtuels
	 */
	public LinkedList<Order> getOpenBids() { return linked_bids ; }
	

	
	/**
	 * Supprimer un ordre virtuel
	 * @param item L'item contenant l'ordre a supprimmer
	 */
	public void cancelOrder(Item<Order> item){
		if (item.e.trade_type == Type.ASK){
			linked_asks.delete(item);
			wallet.setAmount(market.cur1, item.e.amount);// on recupere l'argent investi dans l'ordre dans le wallet
		}
		
		if(item.e.trade_type == Type.BID){
			linked_bids.delete(item);
			BigDecimal x=market.roundPrice(Op.mult(item.e.amount,item.e.price));
			wallet.setAmount(market.cur2, x);// on recupere l'argent investi dans l'ordre dans le wallet
		}		
	}
	
	/**
	 * @return La quantite de cur1 investi dans le marche
	 */
	public BigDecimal getInMarketCur1(){
		BigDecimal cur1Amount=Decimal.ZERO;
		for(Item<Order> item:this.getOpenAsks()){
			cur1Amount=Op.add(item.e.amount,cur1Amount);
		}
		return cur1Amount;
	}
	
	/**
	 * @return La quantite de cur2 investsi dans le marche
	 */
	public BigDecimal getInMarketCur2(){
		BigDecimal cur2Amount=Decimal.ZERO;
		for(Item<Order> item:this.getOpenBids()){
			cur2Amount=Op.add(Op.mult(item.e.amount,item.e.price),cur2Amount);
		}
		return cur2Amount;
	}
	
	
	/**
	 * @return Le portefeuille lie au marche auquel est destine les ordres virtuels
	 */
	public Wallet getWallet(){
		return wallet;
		
	}
	
	/**
	 * Verifie si les ordres virtuels auraient pu etre executes en vrai en comparant
	 * les ordres virtuels avec les echanges recents de l'historique pas encore analyses.
	 */
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
		for (int i = 0; i < market.last_trades.length; i++) { // pour chaque echange recent non encore verifie
			current = market.last_trades[i];
			//System.out.println("aj"+current);
			current_amount = current.amount;
			
			// Selon si l'echnage courant est un bid ou un ask on choisit des avriabels differentes (après le traitement est quasi similaire à ces variables pres)
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
				best = best_item.e; // On recupere l'ordre  virtuel le plus competitif

				
				// tant que il y a un ordre virtuel plus competitif que celui execute en vrai et que la quantite de l'echange courant ne vaut pas 0
				while (comp.compare(best, current) <= 0 && current_amount.compareTo(market.getAmountPrecision())>0) {
					
					// Calcul de la qauntite a execute
					amount_executed = (best.amount.compareTo(current_amount)<0) ? best.amount: current_amount;
					
					// On vide l'echange courant de cette quantite (sur une copie, l'historique reste intact)
					current_amount = Op.sub(current_amount,amount_executed); 
					
					// On vide le meilleur ordre virtuel partiellement
					best.amount =Op.sub(best.amount,amount_executed);
					
					// On remplit l'historique des bids executes
					if(pending == pendingBids){
						Order executed_order= new Order(best.price, market.roundAmount(market.subFee(amount_executed)),Type.BID);
						hist_bids.insert(executed_order);
					}
					
					// On remplit le wallet avec la quantite gagne a l'execution
					if (current.trade_type.equals(Type.ASK)){
						wallet.setAmount(cur,market.roundPrice(market.subFee( Op.mult(amount_executed,best.price))));
					}
					else{
						wallet.setAmount(cur, market.roundAmount(market.subFee( amount_executed)));
					}
					
					// Si le meilleur ordre virtuel est totalement vide, on le retire
					if (best.amount.compareTo(market.getAmountPrecision())<=0) {
						pending.delete(best_item);
						best_item = pending.getFirst();
						best = best_item.e;
					}
				}

			} catch (NoSuchElementException e) {
				// Rien a faire, juste passer a l'echange suivant
			}
			market.trades.add(current); // On fait passer l'echange courant à l'historique deja verifiee
			market.getTs().feed(current);// On remplis la time serie de l'evolution du marche
		}
	}

	
}
