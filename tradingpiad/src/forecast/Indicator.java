package forecast;

public interface Indicator {
	public boolean shouldBuy();
	public boolean shouldSell();
	public void feed(TSDatum d);
}
