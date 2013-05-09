package arbitrage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import market.Currency;
import market.EndOfRun;
import market.ExchangeError;
import market.Market;
import market.Order;
import market.Type;
import market.Wallet;
import strategies.Agent;
import strategies.PriceManager;
import strategies.Strategy;
import utilities.Assert;
import utilities.Decimal;
import utilities.Item;
import utilities.Op;
import utilities.PosInf;
import utilities.Util;

public class Arbitrage implements Strategy {


	List<Order> listOrders ;
	private PriceManager priceManager;
	private BestMatchingDeals[] bmdTab;
	private ArrayList<Currency> listeMonnaies;
	private BigDecimal[] amountToInvest;
	
	public Arbitrage(PriceManager priceManager,Market[] markets){
		this.priceManager=priceManager;
		listeMonnaies = Util.getCurrencyList(markets);
		bmdTab=new BestMatchingDeals[listeMonnaies.size()]; 
		
		for(int i=0; i<listeMonnaies.size();i++){
			bmdTab[i]= new BestMatchingDeals(markets, listeMonnaies.get(i), priceManager);
		}
		
		amountToInvest=new BigDecimal[Currency.values().length];
		for (int i=0;i<amountToInvest.length;i++){
			amountToInvest[i]=Decimal.ZERO;
		}
		
	}

	@Override
	public void execute(Market[] marketTab) throws EndOfRun {
		
		for(Market m: marketTab){
			try {
				m.updateAll();
			} catch (ExchangeError e) {
				/* Si une erreur s'est produite (une erreur http qui est de la faute du serveur par exemple)
				alors ne rien faire au temps t courant (on retenhtera de faire quelque chose au prochain temps t+1 ) )*/
				return;
			}
		}
		//if(Math.random()>0.25)
			Agent.removeAllOrder(marketTab);
			System.out.println(marketTab[0].getWallet());
		
		this.setInvestAmount(new Decimal("0.0001"), marketTab[0].getWallet());
		
		
		List<CustomEdge> serieEchangesOpt=this.getSerieEchangesOpt();
		if (serieEchangesOpt != null) {
			BigDecimal gainOpt = gain(serieEchangesOpt);

			if (gainOpt.compareTo(Decimal.ONE) > 0) {
				System.out.println(gainOpt);
				CustomEdge e1 = serieEchangesOpt.get(0);
				System.out.println(serieEchangesOpt);
				Market m1 = serieEchangesOpt.get(0).getmarket();
				Currency cur;
				if (!e1.isReversed())
					cur = m1.cur1;
				else
					cur = m1.cur2;

				BigDecimal amount = m1.roundAmount(amountToInvest[cur.ordinal()]);

				amount = amount.min(getMaxInitAmount(serieEchangesOpt));
				if (((m1.cur1 == cur) && amount.compareTo(m1.getAmountPrecision()) > 0) || (m1.cur2 == cur) && amount.compareTo(m1.getPricePrecision()) > 0) {

					placeOrdres(amount, serieEchangesOpt);
				}
			}
		}
		marketTab[0].sleep();
			
		for (Market m : marketTab){
			System.out.println(m.cur1 +"/"+m.cur2+"-asks:"+m.getOpenAsks());
			System.out.println(m.cur1 +"/"+m.cur2+"-bids:"+m.getOpenBids()+"\n");
			m.nextTimeDelta();
		}
		System.out.println("*****************\n");
	}


	
	public List<CustomEdge> getSerieEchangesOpt(){
		BigDecimal gainOpt=Decimal.ONE;
		List<CustomEdge> serieEchangesOpt=null;
		for(int i=0; i<listeMonnaies.size();i++){
			List<CustomEdge> tmpSerieEchanges=bmdTab[i].serieEchangesOptimal();
			//System.out.println(tmpSerieEchanges);
			BigDecimal tmpGain=gain(tmpSerieEchanges);
			//System.out.println("gain="+tmpGain);
			if(tmpGain.compareTo(gainOpt)>0){
				gainOpt=tmpGain;
				serieEchangesOpt=tmpSerieEchanges;
				
			}
		}
		return serieEchangesOpt;
	}

	/**
	 * Calcul la quantite de monnaie obtenue a l'issue de la serie d'echange si on commence avec une quantite de 1
	 * de monnaie de depart. 
	 * @param listEdges la liste des arretes echanges effectue = un cheminde le graphe
	 * @return La quantite de monnaie obtenu a l'issu de la serie d'echange: >1 si la serie d'echange est profitable
	 */
	public BigDecimal gain (List<CustomEdge> listEdges){
		BigDecimal quantiteMonnaie = Decimal.ONE;

		for (CustomEdge edge : listEdges){
			quantiteMonnaie = edge.quantiteApresEchange(quantiteMonnaie);
		}	
		return quantiteMonnaie ; 
	}
	
	/**
	 * Determine, pour une serie d'echange qu'on souhaite executer,
	 * la somme de depart maximum qu'on peut utiliser
	 * @param listEdges La serie d'echange qu'on souhaite executer
	 * @return La quantite (en monnaie de depart de la serie) maximale que l'on peut utiliser.
	 */
	public BigDecimal getMaxInitAmount (List<CustomEdge> listEdges){
		BigDecimal inf=new PosInf();
		BigDecimal maxInitAmount = inf;
		Market market ; 

		for (int i =listEdges.size()-1;i>=0;i--){
			CustomEdge edge=listEdges.get(i);
			market = edge.getmarket();
			if(maxInitAmount != inf){
				maxInitAmount=edge.quantiteAvantEchange(maxInitAmount);
			}

			if (!edge.isReversed()) {
				maxInitAmount = maxInitAmount.min(market.getWallet().getAmount(market.cur1));
				System.out.println("montant inital"+market.cur1+":"+maxInitAmount);
			} 
			else {
				maxInitAmount = maxInitAmount.min( market.getWallet().getAmount(market.cur2));
				System.out.println("montant inital"+market.cur2+":"+maxInitAmount);
			}
			
		}	

		return maxInitAmount ; 

	}


	public void placeOrdres (BigDecimal quantiteInitiale , List<CustomEdge> listEdges){

		BigDecimal quantite, quantiteApres;
		Market market;
		
		quantite= Op.sub(quantiteInitiale,!listEdges.get(0).isReversed()?listEdges.get(0).getmarket().getAmountPrecision():listEdges.get(0).getmarket().getPricePrecision());

		for (CustomEdge edge : listEdges){

			market = edge.getmarket();
			if (!edge.isReversed()){
				System.out.println("quantite"+market.cur1+":"+quantite);
				edge.getmarket().addAsk(new Order(edge.getSellPrice() ,quantite,Type.ASK));				
				quantiteApres =edge.quantiteApresEchange(quantite);

			}else{
				System.out.println("quantite"+market.cur2+":"+quantite);
				BigDecimal amount = market.roundAmount(Op.div(quantite, edge.getBuyPrice()));
				edge.getmarket().addBid(new Order(edge.getBuyPrice() , amount,Type.BID));
				quantiteApres=edge.quantiteApresEchange(quantite);

			}
			quantite=quantiteApres;

		}



	}
	
	public void setInvestAmount(BigDecimal percent,Wallet w){
		Assert.checkPrecond(Op.sup0(percent) && percent.compareTo(Decimal.ONE)<=0, "Valeur de pourcentage invalide");
		for( Currency cur:listeMonnaies){
			amountToInvest[cur.ordinal()]=Op.mult(w.getAmount(cur),percent);
		}
	}
	
	


}
