package tradercomponent.time;

import market.Market;

public class SimpleTimeManager implements TimeManager{
	
	private long sellLimitTime;
	private long buyLimitTime;
	private Market m;
	
	public SimpleTimeManager(Market m,long i1 , int i2 ){
		buyLimitTime = m.getStartTime() + ((m.getEndTime() - m.getStartTime())*i1 )/ 100;
		sellLimitTime = m.getStartTime() + ((m.getEndTime() - m.getStartTime()) * i2) / 100;
		this.m=m;
	}

	@Override
	public boolean shouldBuy() {
		return m.getCurrentTime()<buyLimitTime;
	}

	@Override
	public boolean shouldSell() {
		return m.getCurrentTime()<buyLimitTime;
	}

	@Override
	public boolean shouldSellNow() {
		return !shouldSell();
	}
	
}
