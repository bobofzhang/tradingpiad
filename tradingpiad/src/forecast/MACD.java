package forecast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager.ChartType;

import utilities.CircularArray;
import utilities.Decimal;
import utilities.Op;

/**
 * http://en.wikipedia.org/wiki/MACD
 * (pas utilisee dans une strategie je l'ai fait juste pour tester des trucs)
 */
public class MACD implements Indicator {
	private EMA ema1;
	private EMA ema2;
	private EMA ema3;
	private BigDecimal cur;
	private int ite, nbPeriod;
	private boolean shouldBuy, shouldSell;
	private boolean sup0;
	private CircularArray<Double> array;
	
	
	/**
	 * 
	 * @param n1 Recommandation : 9
	 * @param n2 Recommandation : 16
	 * @param n3 Recommandation : 6
	 */
	public MACD(int n1, int n2, int n3) {
		ema1 = new EMA(n1);
		ema2 = new EMA(n2);
		ema3 = new EMA(n3);
		ite = 0;
		nbPeriod = Math.max(Math.max(n1, n2), n3);
		shouldBuy = shouldSell = false;
		array=new CircularArray<Double>(Double.class, 500);
	}
	
	public MACD() {
		this(12,26,9);
	}
	
	@Override
	public void feed(TSPoint d) {
		BigDecimal p = d.getClose();
		BigDecimal ema1_2 = Op.sub(ema1.getValue(p), ema2.getValue(p));
		cur =Op.sub(ema1_2,ema3.getValue(ema1_2));
		array.add(Double.valueOf(cur.doubleValue()));
		if (ite >= nbPeriod && !cur.equals(Decimal.ZERO)) {
			if (Op.sup0(cur) && !sup0) {
				shouldBuy = true;
				sup0=true;
			} else if (Op.inf0(cur) && sup0) {
				shouldSell = true;
				sup0=false;
			} else {
				shouldBuy = shouldSell = false;
			}
		}
		ite++;
	}

	@Override
	public boolean shouldBuy() {
		return shouldBuy;
	}

	@Override
	public boolean shouldSell() {
		return shouldSell;
	}
	
	public Chart getChart(){
		List<Number> prices = new ArrayList<Number>();
		List<Number> x = new ArrayList<Number>();
		
		for (int i = 0; i < array.size(); i++) {
			prices.add(array.get(i).doubleValue());
			x.add(i);
		}

		Chart chart = new ChartBuilder().chartType(ChartType.Bar).width(800).height(600).title("MACD").xAxisTitle("X").yAxisTitle("Y").build();

		// Series
		chart.addSeries("MACD ", x, prices);
		
		
		return chart;
	}
}
