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


/**
 * Un objet Market est caracterise par un triplet (monnaie 1, monnaie 2, bourse d'echange,).
 * Il donne accès  aux informations du marché et permet d'interagir avec lui.
 * C'est en quelque sorte une interface entre l'agent trader et l'environnement de trading.
 * C'est un objet abstrait dont l'implementation change selon:
 * - que les interractions sont virtuelles ou réelles (version reelle pas implemente mais possible)
 * (pas encore implementee mais possible en derivant les classes MarketMtgoxLive pour mtgox,
 * marketBtceLive pour btce, etc...).
 * - la bourse d'echange
 * - dans le cas virtuelle: si la simulation tourne en live ou si elle tourne sur un fichier
 */
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
		ts = new TimeSerie(500, 7200);
	}
	
	public Market(Currency cur1, Currency cur2,String exchangeName){
		Assert.nullCheck(cur1, cur2);
		this.cur1=cur1;
		this.cur2=cur2;
		this.exchangeName=exchangeName;
		trades=new Trades(500);
		ts = new TimeSerie(500, 600);
		
	}
	
	/**
	 * @return Le ticker obtenu lors de l'update la plus recente
	 */
	public Ticker getTicker(){
		return ticker;
	}
	
	/**
	 * @return La depth (=le cahier des ordres) obtenu lors de l'update la plus recente
	 */
	public  Depth getDepth(){
		return depth;
	}
	
	
	/**
	 * @return Les (pour l'instant 500) derniers echanges obtenu lors de la derniere update
	 */
	public  Trades getTrades(){
		return trades;
	}
	
	/**
	 * Actualise les informations du marché
	 * @throws ExchangeError Si l'update a echoué
	 */
	public void updateAll() throws ExchangeError{
		// On cree un pool de 3 thread
	    ExecutorService executor = Executors.newFixedThreadPool(3); 
	    
	    // Code a executer pour le 1er thread (thread "speciaux" retournant des valeurs)
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
	    
	 // Code a executer pour le 2e thread
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
	    
	 // Code a executer pour le 3e thread
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
	    	// On attends la fin du 1er thread et on recupere son resultat
	    	ExchangeError ticker_ex=ticker_future.get();
			if (ticker_ex != null)
				throw ticker_ex; // On lance l'erreur une erreur s'il y en a une
	    	
			// On attends la fin du 2e thread et on recupere son resultat
			ExchangeError trades_ex=trades_future.get();
	    	if (trades_ex != null)
				throw trades_ex; 
	    	
	    	// On attends la fin du 3e thread et on recupere son resultat
	    	ExchangeError depth_ex=depth_future.get();
	    	if (depth_ex != null)
				throw depth_ex;  
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	    check();// On verifie si des ordres on ete executes
	}
	

	/**
	 * @return Le json du ticker (! Pas implemente dans MarketPast)
	 */
	public String getJsonTicker(){
		return jsonTicker;
	}
	
	/**
	 * @return Le json du cahier des ordres (! Pas implemente dans MarketPast)
	 */
	public String getJsonDepth(){
		return jsonDepth;
	}
	
	
	/**
	 * @return Le json des trades (! Pas implemente dans MarketPast)
	 */
	public String getJsonTrades(){
		return jsonTrades;
	}
	
	
	/**
	 * Actualisation du ticker
	 * @throws ExchangeError
	 */
	public abstract void updateTicker() throws ExchangeError;
	
	/**
	 *  Actualisation du cahier des ordres
	 * @throws ExchangeError
	 */
	public abstract void updateDepth() throws ExchangeError;
	
	/**
	 * Actualisation des derniers echanges
	 * @throws ExchangeError
	 */
	public abstract void updateTrades() throws ExchangeError;
	
	
	public abstract void nextTimeDelta() throws EndOfRun;
	public abstract void sleep();
	
	/**
	 * Fait passer le temps de timeDelta millisecondes
	 * @throws EndOfRun Si a l'issu de l'attente, on est au dela de la date de fin du run
	 */
	public void waitTimeDelta() throws EndOfRun{
		nextTimeDelta();
		sleep();
	}
	
	/**
	 * @return Date de depart du run en time millis
	 */
	public abstract long getStartTime();
	
	/**
	 * @return Date courant en time millis
	 */
	public abstract long getCurrentTime();
	
	
	/**
	 * @return Date de fin du run
	 */
	public abstract long getEndTime();
	
	/**
	 * @return La liste des asks vurtuels de l'agent trader
	 */
	public abstract LinkedList<Order> getOpenAsks();
	
	/**
	 * @return La liste des bids virtuels de l'agent trader
	 */
	public abstract LinkedList<Order> getOpenBids();
	
	
	/**
	 * @return La liste des bids executes
	 */
	public abstract LinkedList<Order> getExecutedBids();
	
	
	/**
	 * @param o Un ordre, doit etre de type ASK
	 */
	public abstract void addAsk(Order o);
	
	/**
	 * @param o Un ordre, doit etre de type BID
	 */
	public abstract void addBid(Order o);
	
	
	/**
	 * 
	 * Supprimme un ordre du marche (!! On doit bien utiliser cette methode et pas supprimmer els elements des listes directement )
	 * @param item Item contenant l'ordre a supprimmer (obtenu via getOpenBids() ou getOpenAsks() )
	 */
	public abstract void cancelOrder(Item<Order> item);
	
	
	/**
	 * @return Recuperer le portefeuille associe a la bourse echange auquel appartient le marché (plusieurs marche peuvent partager un meme portefeuille)
	 */
	public abstract Wallet getWallet();
	
	
	/**
	 * @return La quantite de cur1 investi dans le marche
	 */
	public abstract BigDecimal getInMarketCur1();
	
	
	/**
	 * @return La quantite de cur2 investi dans la marche
	 */
	public abstract BigDecimal getInMarketCur2();
	
	
	
	/**
	 * @return La quantite de cur1 +dans le wallet plus celle dans le marche
	 */
	public BigDecimal getTotalCur1Amount(){
		return Op.add(this.getInMarketCur1(),getWallet().getAmount(cur1));
	}
	
	/**
	 * @return La quantite de cur2 dans le wallet plus celle dans le marche
	 */
	public BigDecimal getTotalCur2Amount(){
		return Op.add(this.getInMarketCur2(),getWallet().getAmount(cur2));
	}
	
	/**
	 * Analyse les echange recents non analyses (fera rien dans le cas reel)
	 */
	public abstract void check();
	
	/**
	 * Methode pour arrondir les prix. La maniere d'arrondir un pris varie selon les bourses d'echanges (selon le nombre de chiffres apprès la virgule autorise)
	 * @param price Un prix qu'on veut arrondir
	 * @return Un arrondi du prix selon la precision des prix de ce marche (peut varier d'un marche a l'autre)
	 */
	public abstract BigDecimal roundPrice(BigDecimal price);
	
	/**
	 * Methode pour arrondir les quantites 
	 * @param amount Une quantite qu'on veut arrondir
	 * @return Un arrondi de la quantite selon la precision des quantites de ce marche (peut varier d'un marche a l'autre)
	 */
	public abstract BigDecimal roundAmount(BigDecimal amount);
	
	
	/**
	 * @return La precision des prix sur ce marche
	 */
	public abstract BigDecimal getPricePrecision();
	
	
	/**
	 * @return La precision des quantites sur ce marche
	 */
	public abstract BigDecimal getAmountPrecision();
	
	/**
	 * @param amount Une quantite d'une currency quelconque
	 * @return Cette quantite moins les frais de transaction
	 */
	public abstract BigDecimal subFee(BigDecimal amount);
	
	/**
	 * @param ask Le prix ou on vend
	 * @param bid Le prix ou on achete
	 * @return Le profit si on achete a prix bid et on revend au prix ask
	 */
	public BigDecimal getProfit(BigDecimal ask, BigDecimal bid){
		return Op.sub(subFee(subFee(ask)),bid);
	}

	/**
	 * @return Le nom de la bourse d'echange (par exemple Mtgox)
	 */
	public String getExchangeName(){
		return exchangeName;
	}
	
	/**
	 * @return une time serie tracant l'evolutiondes prix du marche
	 */
	public TimeSerie getTs() {
		return ts;
	}

	/**
	 * Changer de timeSerie
	 * @param ts Nouvelle time serie
	 */
	public void setTs(TimeSerie ts) {
		this.ts = ts;
	}
	
}
