package strategies;

import market.EndOfRun;
import market.ExchangeError;
import market.Market;
import market.Wallet;

public class Agent {
	public Strategy strat;
	public Market m;
	public Wallet w;
	
	public Agent(Strategy strat,  Market m,Wallet w){
		this.strat=strat;
		this.m=m;
		this.w=w;
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
		}
	}
	
	public void init() throws ExchangeError{
	}

}
