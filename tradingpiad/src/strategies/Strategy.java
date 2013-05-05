package strategies;

import java.util.ArrayList;

import market.EndOfRun;
import market.Market;

public interface Strategy {
	public void execute( Market[] marketList) throws EndOfRun;
}
