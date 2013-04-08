package market;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
/**
 * 
 * @author Kevin
 *
 * Classe pour récolter des donnees sur un marche
 */
public class DataRetriever {
	FileWriter fstream;
	BufferedWriter out;
	FileWriter fstream2;
	BufferedWriter out2;
	Market market;

	public DataRetriever(String filename, String filename2, Market market) {
		this.market = market;
		try {
			fstream = new FileWriter(filename);
			out = new BufferedWriter(fstream);
			out.write(filename + "\n");
			out.write(market.getExchangeName() + "\n");
			out.write(String.valueOf(market.getStartTime()) + "\n");
			out.write(String.valueOf(market.getEndTime()) + "\n");

			fstream2 = new FileWriter(filename2);
			out2 = new BufferedWriter(fstream2);
			out2.write(filename + "\n");
			out2.write(market.getExchangeName() + "\n");
			out2.write(String.valueOf(market.getStartTime()) + "\n");
			out2.write(String.valueOf(market.getEndTime()) + "\n");

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void retrieve() throws ExchangeError {
		boolean fini = false;
		try {
			while (!fini) {
				try {
					market.updateAll();
				} catch (Exception e) {
					System.out.println("update all raté  :o");
					System.out.println(market.getJsonTicker());
					try {
						market.waitTimeDelta();
					} catch (EndOfRun end) {
						fini = true;
					}
					continue;
				}
				out.write(String.valueOf(System.currentTimeMillis()));
				out.write("\n");
				out.write(market.getJsonTicker());
				out.write("\n");
				out.write(market.getJsonDepth());
				out.write("\n");
				out.write(market.getJsonTrades());
				out.write("\n");

				for (Trade t : market.last_trades) {
					out2.write(String.valueOf(t.date));
					out2.write(",");
					out2.write(t.price.toString());
					out2.write(",");
					out2.write(t.amount.toString());
					out2.write(",");
					out2.write(t.tid);
					out2.write(",");
					out2.write(t.trade_type.toString());

					out2.write("\n");

				}
				try {
					market.waitTimeDelta();
				} catch (EndOfRun e) {
					fini = true;
				}
			}

			out.close();
			out2.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

}
