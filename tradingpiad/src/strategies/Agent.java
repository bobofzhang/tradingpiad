package strategies;

import java.util.ArrayList;
import java.util.Observable;

import market.EndOfRun;
import market.ExchangeError;
import market.Market;
import market.Wallet;

public class Agent{
	public Strategy strat;
	public Market m;
	public Wallet w;
	public ArrayList<MyObserver> obsList;
	
	public Agent(Strategy strat,  Market m,Wallet w){
		this.strat=strat;
		this.m=m;
		this.w=w;
		obsList=new ArrayList<MyObserver>();
	}
	
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
			System.out.println("End TimeDelta");
			System.out.println("\n");
			this.updateAllObserver();
		}
	}
	
	public void init() throws ExchangeError{
	}

	public Market[] getMarkets() {
		return new Market[]{m};
	}

	public Wallet getWallet() {
		return w;
	}
	
	public void addObserver(MyObserver a){
		obsList.add(a);
	}
	
	public void updateAllObserver(){
		for(MyObserver a:obsList)
			a.update(this);
	}

}
