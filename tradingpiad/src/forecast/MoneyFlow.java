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

public class MoneyFlow implements Indicator{
	
	private CircularArray<BigDecimal> arr;
	private BigDecimal posMF;
	private BigDecimal negMF;
	private BigDecimal prevTypicalPrice;
	private BigDecimal mfi;
	
	private BigDecimal overBought,overSell;
	
	
	public MoneyFlow(){
		arr= new  CircularArray<BigDecimal>(BigDecimal.class,500);
		posMF=Decimal.ONE;
		negMF= Decimal.ONE;
		prevTypicalPrice=Decimal.ZERO;
		overBought=new Decimal("80");
		overSell=new Decimal("20");
	}
	
	@Override
	public void feed(TSPoint d){
		BigDecimal typicalPrice= Op.div(Op.add(Op.add(d.getClose(),d.getHigh()),d.getLow()),new Decimal("3"));
		BigDecimal moneyFlow=Op.mult(typicalPrice, d.getVolume());
		if( typicalPrice.compareTo(prevTypicalPrice)>0)
			posMF=Op.add(posMF, moneyFlow);
		else if(typicalPrice.compareTo(prevTypicalPrice)<0)
			negMF=Op.add(negMF, moneyFlow);
		
		prevTypicalPrice=typicalPrice;
		
		mfi=Op.sub(new Decimal("100"), Op.div(new Decimal("100"), Op.add(Decimal.ONE, Op.div(posMF, negMF))));
		arr.add(mfi);
		
		
	}
	
	public BigDecimal getValue(){
		return mfi;
	}

	@Override
	public boolean shouldBuy() {
		return mfi.compareTo(overSell)<0;
	}

	@Override
	public boolean shouldSell() {
		return mfi.compareTo(overBought)>0;
	}
	
	public Chart getChart(){
		List<Number> mfis = new ArrayList<Number>();
		List<Number> x = new ArrayList<Number>();
		
		for (int i = 0; i < arr.size(); i++) {
			mfis.add(arr.get(i).doubleValue());
			x.add(i);
		}

		Chart chart = new ChartBuilder().chartType(ChartType.Bar).width(800).height(600).title("MFI").xAxisTitle("X").yAxisTitle("Y").build();

		// Series
		chart.addSeries("MFI ", x, mfis);
		
		
		return chart;
	}

}
