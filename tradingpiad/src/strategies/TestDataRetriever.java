package strategies;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import utilities.Op;

import market.Currency;
import market.Market;
import market.Wallet;

public class TestDataRetriever implements AgentObserver{
	
	private String filename;
	private Wallet prevWallet;
	long last_time;
	private FileWriter fstream;
	private BufferedWriter out;
	long startTime;

	public TestDataRetriever(String filename, long timeDelta){
		try {
			fstream = new FileWriter(filename);
			out = new BufferedWriter(fstream);

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void notify(Wallet w, Market[] markets){
		Wallet new_wallet =w.clone(); // Deep copy of the current wallet
		
		
		// We also add the quantities of BTC,USD,etc.. inside the market
		StringBuffer buf= new StringBuffer(100);
		for( Market m :markets){
			new_wallet.setAmount(m.cur1, m.getInMarketCur1());
			new_wallet.setAmount(m.cur2, m.getInMarketCur2());
			
			// Save in the file the current value of cur1 (e.g. BTC)
			buf.append(m.cur1.toString()+">"+m.cur2.toString()+"="+m.getTrades().getLast().price.toString()+";");
		}
		buf.deleteCharAt(buf.length()-1);
		buf.append("\n");
		out.write(buf.toString());
		
		
		buf= new StringBuffer(100);
		for(Currency c: Currency.values())
			buf.append(c+":"+new_wallet.wallet[c.ordinal()]+",");
		buf.deleteCharAt(buf.length()-1);
		out.write(buf.toString());
		
		
		if (prevWallet !=null){
			
		}
		prevWallet=new_wallet;
		
	}

}
