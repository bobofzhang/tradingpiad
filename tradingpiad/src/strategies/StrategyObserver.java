package strategies;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.StyleManager.LegendPosition;

import utilities.Decimal;
import utilities.Op;

import market.Currency;
import market.Market;
import market.Wallet;

/**
 * Permet de memoriser et analyser le comprortement d'une stategie au corus de son execution.
 * Cette classe permet :
 * - De retenir un historique du wallet au cours du temps.
 * - D'avoir la moyenne et la variance des profits par rapport a cet historique
 * - Avoir le madrawdown (perte maximale) toujorus apr rapport a cet historique du wallet
 *
 */
public class StrategyObserver implements MyObserver {

	long lastTime, timeDelta;
	private final Currency refCurrency;
	private List<Double> listValue;

	/**
	 * @param timeDelta Tous les combiens , on "photographie" l'etat du portfeuille au cours de l'execution de la strategie 
	 * @param refCurrency Dans quelle monnaie on plot le resultat
	 */
	public StrategyObserver(long timeDelta, Currency refCurrency) {
		this.refCurrency = refCurrency;
		this.timeDelta = timeDelta;
		lastTime = 0;
		listValue = new ArrayList<Double>(500);

	}

	@Override
	public void update(Object o) {
		Agent a = (Agent) o;
		Market[] markets = a.getMarkets();
		Wallet w = a.getWallet();

		// Get the list of all the currency used by the trader agent
		ArrayList<Currency> listCurrencies = new ArrayList<Currency>(markets.length * 2);
		for (Market m : markets) {
			if (!listCurrencies.contains(m.cur1))
				listCurrencies.add(m.cur1);
			if (!listCurrencies.contains(m.cur2))
				listCurrencies.add(m.cur2);
		}

		// Create a corresponding array wich mapping a currency to an amount
		BigDecimal[] tab = new BigDecimal[listCurrencies.size()];

		for (Currency c : listCurrencies)
			tab[listCurrencies.indexOf(c)] = w.getAmount(c);

		// We also add the quantities of BTC,USD,etc.. inside the market
		for (Market m : markets) {
			int i1 = listCurrencies.indexOf(m.cur1);
			int i2 = listCurrencies.indexOf(m.cur2);

			tab[i1] = Op.add(tab[i1], m.getInMarketCur1());
			tab[i2] = Op.add(tab[i2], m.getInMarketCur2());

		}
		

		for (Market m : markets) {
			int i1 = listCurrencies.indexOf(m.cur1);
			int i2 = listCurrencies.indexOf(m.cur2);

			if (m.cur1.equals(refCurrency) && Op.sup0(tab[i2])) {
				tab[i1] = Op.add(tab[i1], Op.div(tab[i2], m.ticker.sell));
				tab[i2] = Decimal.ZERO;
			}

			if (m.cur2.equals(refCurrency) && Op.sup0(tab[i1])) {
				tab[i2] = Op.add(tab[i2], Op.mult(tab[i1], m.ticker.buy));
				tab[i1] = Decimal.ZERO;
			}

		}

		BigDecimal total = tab[listCurrencies.indexOf(refCurrency)];
		
		if(lastTime==0){
			lastTime=markets[0].getCurrentTime();
			listValue.add(new Double(total.doubleValue()));
		}
		else if (markets[0].getCurrentTime() > lastTime + timeDelta) {
			listValue.add(new Double(total.doubleValue()));
			lastTime+=timeDelta;
		}

	}
	
	/**
	 * @return liste des valeurs du portfeuille au corus du temps
	 */
	public List<Double> getWalletValueList(){
		return listValue;
	}
	
	/**
	 * @return Liste des valeurs de l'evolution du portfeuille au cours du temps (si superieur 0, il y a augmentation sinon il y a descente )
	 */
	public double[] getWalletEvolution(){
		double[] evTab= new double[listValue.size()-1];
		
		for(int i=1;i<listValue.size();i++){
			evTab[i]=listValue.get(i).doubleValue()/listValue.get(i-1)-1;
		}
		
		return evTab;
	}
	
	/**
	 * @return L'ecart type de l'evolution
	 */
	public double standardDeviation(){
		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		double[] inputArray=getWalletEvolution();
		for( int i = 0; i < inputArray.length; i++) {
		        stats.addValue(inputArray[i]);
		}

		return stats.getStandardDeviation();
	}
	
	/**
	 * @return La moyenne de l'evolution
	 */
	public double mean(){
		DescriptiveStatistics stats = new DescriptiveStatistics();
		
		double[] inputArray=getWalletEvolution();
		for( int i = 0; i < inputArray.length; i++) {
		        stats.addValue(inputArray[i]);
		}
		
		return stats.getMean();
	}
	
	/**
	 * Donne la perte maximale si on aurait commencer et terminer à trader aux pires moments
	 * @return Le maxdrawdown
	 */
	public double maxDrawDown(){
		double max=0;
		double diff=0;
		double maxdd=0;
		for(double d:listValue){
			max=Math.max(max, d);
			diff=max-d;
			maxdd=Math.max(maxdd,diff);
		}
		return maxdd;
	}
	
	public Chart getChart(){
		List<Number> prices = new ArrayList<Number>();
		List<Date> dates = new ArrayList<Date>();
		
		for (int i = 0; i < listValue.size(); i++) {
			prices.add(listValue.get(i).doubleValue());
			dates.add(new Date(lastTime-(listValue.size()-i-1)*timeDelta));
		}

		Chart chart = new ChartBuilder().chartType(ChartType.Area).width(800).height(600).title("AreaChart01").xAxisTitle("X").yAxisTitle("Y").build();

		// Customize Chart
		chart.getStyleManager().setChartTitleVisible(false);
		chart.getStyleManager().setLegendPosition(LegendPosition.InsideNW);
		chart.getStyleManager().setDatePattern("dd/MM");
		chart.getStyleManager().setLocale(Locale.FRANCE);

		// Series
		chart.addDateSeries("Wallet amount estimation in "+refCurrency, dates, prices);
		
		return chart;
	}

}
