package utilities;

import java.math.BigDecimal;
import java.util.ArrayList;

import forecast.TimeSerie;


/**
 * Permet d'analyser un historique et faire des predictions sur l'evolutiondes prix
 * Elle fonctionne simplement en comptant le nombre de montee et de descente
 *
 */
public class Forecast {

	private TimeSerie trades_history ; 
	private ArrayList<BigDecimal> rt ; 
	private final int limit_number = 10 ; 
	private BigDecimal calcul ; 
	
	private int nb_negatif = 0 ;
	private int nb_positive = 0 ;
	
	
	/**
	 * @param tradesHistory L'historique qu'on veut analyser
	 */
	public Forecast(TimeSerie tradesHistory){
		this.trades_history = tradesHistory;
		rt = new ArrayList<BigDecimal>();
	}
	
	
	/**
	 * Calcul de variables intermediaires
	 * @param window_size Taille de la fenetre ou 'lon regarde dan le passe
	 */
	public void calcul_rt( int window_size ){
		
	
		//System.out.println("start_index : " + start_index + "  window_size :"  + window_size );
		//System.out.print(" THIS IS CALCUL ");
		for (int i = Math.max(trades_history.size()-window_size,1 ) ;i<trades_history.size(); i++){			
			
			
			
			calcul = Op.sub(Op.div(trades_history.get(i).getClose(),trades_history.get(i-1).getClose()),BigDecimal.ONE);
			//System.out.print(" " + calcul  );
			//System.out.println();
			
			rt.add(calcul);
			if (calcul.compareTo(BigDecimal.ZERO)>0){
				nb_positive ++; 
			}else{
				setNb_negatif(getNb_negatif() + 1) ; 
			}
		
		}
	}
/*
	public boolean is_forecast_positif(int startIndex , int windowSize){
		calcul_rt( windowSize);
		
		System.out.println("is_forecast_positif"+(double)((double)nb_positive/(double)limit_number));
		
		if((double)((double)nb_positive/(double)windowSize)>0.6){
			return true ;
		}
		return false ; 
	}
	
	public boolean is_forecast_negatif(int startIndex , int windowSize){
		calcul_rt( windowSize);
		System.out.println("is_forecast-negatif"+(double)((double)nb_positive/(double)limit_number));
		if((double)((double)nb_negatif/(double)windowSize)>0.6 ){
			return true ;
		}
		return false ; 
	}
	*/
	
	/**
	 * @return true Si une montee des prix est predite false sinon
	 */
	public boolean is_forecast_positif(){
		
		//System.out.println("is_forecast_positif"+(double)((double)nb_positive/(double)limit_number));
		
		if((double)((double)nb_positive/(double)limit_number)>0.7 ){
			return true ;
		}
		return false ; 
	}

	/**
	 * @return true Si une descente des prix est predite, false sinon
	 */
	public boolean is_forecast_negatif(){
		
		//System.out.println("is_forecast-negatif"+(double)((double)nb_positive/(double)limit_number));
		
		if((double)((double)nb_negatif/(double)limit_number)>0.7){
			return true ;
		}
		return false ; 
	}
	
	
	public int getNb_negatif() {
		return nb_negatif;
	}

	public void setNb_negatif(int nb_negatif) {
		this.nb_negatif = nb_negatif;
	}
	
}
