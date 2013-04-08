package forecast;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.StyleManager.LegendPosition;

import market.Trade;
import utilities.Assert;
import utilities.CircularArray;
import utilities.Decimal;
import utilities.Op;

public class TimeSerie {
	// Champs definissant une TimeSerie
	private CircularArray<TSDatum> array;
	private long timeInterval;// en seconde !
	private long startDate; // en seconde !
	
	// Champs pour les calculs
	long endDatumDate;
	TSDatum curDatum;
	
	private TimeSerie(int capacity, long startDate,long timeInterval){
		array= new CircularArray<TSDatum>(TSDatum.class, capacity);
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
	
	public TSDatum get(int i){
		return array.get(i);
	}
	
	/**
	 * @param t 
	 * @return Le TSdatum ajoute (null si aucun n'est ajoute)
	 */
	public TSDatum feed(Trade t){
		
		if(startDate<0)
			startDate=endDatumDate=t.date;
			
		
		TSDatum createdDatum=null;
		if( t.date>=endDatumDate ){
			createdDatum=curDatum;
			
			if (createdDatum != null)
				array.add(curDatum);
			
			curDatum=new TSDatum(BigDecimal.ZERO, BigDecimal.ZERO, new Decimal("10000000"),t.price,BigDecimal.ZERO);
			endDatumDate+=timeInterval;
			
		}
		
		curDatum.volume=Op.add(curDatum.volume, t.amount);
		curDatum.high=Op.max(curDatum.high, t.price);
		curDatum.low=Op.min(curDatum.low, t.price);
		curDatum.close=t.price;
		return createdDatum;

	}
	
	public TSDatum getLast(){
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
