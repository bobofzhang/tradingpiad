package strategies;

import java.util.ArrayList;

import market.EndOfRun;
import market.Market;
import market.Wallet;

/**
 * Represente un agent trader generique.
 * Prends en parametre une strategie
 *
 */
public class Agent{
	public Strategy strat;
	public Market m;
	public Wallet w;
	public ArrayList<MyObserver> obsList;  // La liste des observeurs : classe externe anlaysant l'execution de la strategie de l'agent
	
	/**
	 * @param strat La strategie
	 * @param m Le marche
	 * @param w Le protefeuille lié au amrche
	 */
	public Agent(Strategy strat,  Market m,Wallet w){
		this.strat=strat;
		this.m=m;
		this.w=w;
		obsList=new ArrayList<MyObserver>();
	}
	
	/**
	 * Demarrer le trading en executant la strategie en parametre
	 */
	public void execute(){
		boolean fini=false;
		while(!fini){
			System.out.println(w);
			try{
				strat.execute(m);
			}
			catch(EndOfRun e){
				fini=true;
			}
			this.updateAllObserver();
		}
	}
	

	public Market[] getMarkets() {
		return new Market[]{m};
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

}
