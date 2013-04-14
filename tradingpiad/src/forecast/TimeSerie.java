package forecast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.StyleManager.LegendPosition;

import market.Trade;
import utilities.CircularArray;
import utilities.Decimal;
import utilities.Op;

/**
 * Classe representant une serie temporelle financiere(comme celle qu'on peut voir sur https://mtgox.com/ )
 * Chaque point de la serie est caracterise par un prix d'ouverture; de fermeture, le volume, etc...
 *
 */
public class TimeSerie {
	// Champs definissant une TimeSerie
	private CircularArray<TSPoint> array;
	private long timeInterval;// en seconde !
	private long startDate; // en seconde !
	
	// Champs pour les calculs
	long endDatumDate;
	TSPoint curDatum;
	
	private TimeSerie(int capacity, long startDate,long timeInterval){
		array= new CircularArray<TSPoint>(TSPoint.class, capacity);
		this.timeInterval=timeInterval;
		this.startDate=startDate;
		endDatumDate=startDate;
	}
	public TimeSerie(int capacity, long timeInterval){
		this(capacity, -1,timeInterval);
	}
	
	public int size(){
		return array.size();
	}
	
	public TSPoint get(int i){
		return array.get(i);
	}
	
	/**
	 * Ajouter un echange dans la TimeSerie
	 * @param t 
	 * @return Le TSdatum ajoute (null si aucun n'est ajoute)
	 */
	public TSPoint feed(Trade t){
		
		if(startDate<0)
			startDate=endDatumDate=t.date;
			
		
		TSPoint createdDatum=null;
		if( t.date>=endDatumDate ){
			createdDatum=curDatum;
			
			if (createdDatum != null)
				array.add(curDatum);
			
			curDatum=new TSPoint(BigDecimal.ZERO, BigDecimal.ZERO, new Decimal("10000000"),t.price,BigDecimal.ZERO);
			endDatumDate+=timeInterval;
			
		}
		
		curDatum.volume=Op.add(curDatum.volume, t.amount);
		curDatum.high=Op.max(curDatum.high, t.price);
		curDatum.low=Op.min(curDatum.low, t.price);
		curDatum.close=t.price;
		return createdDatum;

	}
	
	/**
	 * @return Le derniere element
	 */
	public TSPoint getLast(){
		return array.getLast();
	}
	
	public Chart getChart(){
		List<Number> prices = new ArrayList<Number>();
		List<Date> dates = new ArrayList<Date>();
		
		for (int i = 0; i < array.size(); i++) {
			prices.add(array.get(i).getClose().doubleValue());
			dates.add(new Date((startDate + i * timeInterval)*1000));
		}

		Chart chart = new ChartBuilder().chartType(ChartType.Area).width(800).height(600).title("AreaChart01").xAxisTitle("X").yAxisTitle("Y").build();

		// Customize Chart
		chart.getStyleManager().setChartTitleVisible(false);
		chart.getStyleManager().setLegendPosition(LegendPosition.InsideNW);
		chart.getStyleManager().setDatePattern("HH:mm");
		chart.getStyleManager().setLocale(Locale.FRANCE);

		// Series
		chart.addDateSeries("Price over time ", dates, prices);
		
		
		return chart;
	}
}
