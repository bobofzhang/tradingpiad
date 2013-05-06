package strategies;

import java.util.ArrayList;

import utilities.Item;

import market.EndOfRun;
import market.Market;
import market.Order;
import market.Wallet;

/**
 * Represente un agent trader generique.
 * Prends en parametre une strategie
 *
 */
public class Agent{
	public Strategy strat;
	public Market[] marketList;
	public Wallet w;
	public ArrayList<MyObserver> obsList;  // La liste des observeurs : classe externe anlaysant l'execution de la strategie de l'agent
	
	/**
	 * @param strat La strategie
	 * @param m Le marche
	 * @param w Le protefeuille lié au amrche
	 */
	public Agent(Strategy strat,  Market[] marketList,Wallet w){
		this.strat=strat;
		this.marketList=marketList;
		this.w=w;
		obsList=new ArrayList<MyObserver>();
	}
	
	/**
	 * Demarrer le trading en executant la strategie en parametre
	 */
	public void execute(){
		boolean fini=false;
		while(!fini){
			
			try{
				strat.execute(marketList);
			}
			catch(EndOfRun e){
				fini=true;
			}
			this.updateAllObserver();
		}
		removeAllOrder(marketList);
		System.out.println(w);
	}
	

	public Market[] getMarkets() {
		return marketList;
	}

	public Wallet getWallet() {
		return w;
	}
	
	/**
	 * Ajouter un observeur
	 */
	public void addObserver(MyObserver a){
		obsList.add(a);
	}
	
	/**
	 * Mise a jour des observeurs
	 */
	public void updateAllObserver(){
		for(MyObserver a:obsList)
			a.update(this);
	}
	
	/**
	 * Suprime tous les ordres places dasn chaque marche du tableau.
	 * @param marketTab La liste des marches dont on veut supprimmer les ordres.
	 */
	public static void removeAllOrder(Market[] marketTab) {
		for(Market m:marketTab){
			for(Item<Order> item:m.getOpenBids())
				m.cancelOrder(item);
			for(Item<Order> item:m.getOpenAsks())
				m.cancelOrder(item);
		}
		
	}

}
