package forecast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import com.xeiam.xchart.SwingWrapper;

import utilities.Decimal;

import market.Trade;
import market.Type;

public class TestIndicator {
	
	
	
	static public void test() throws IOException{
		FileInputStream fstream = new FileInputStream("tradehist_btce48h_3003.txt");
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		TimeSerie ts= new TimeSerie(1000,600);
		br.readLine();
		br.readLine();
		br.readLine();
		br.readLine();
		TSPoint p;
		MACD macd= new MACD();
		MoneyFlow mfi= new MoneyFlow();
		while((line=br.readLine()) != null){
			String [] tab=line.split(",");
			p=ts.feed( new Trade(Long.parseLong(tab[0]), new Decimal(tab[1]),new Decimal(tab[2]), tab[3],Type.valueOf(tab[4])));
			if (p!=null){
				macd.feed(p);
				mfi.feed(p);
			}

		}
		br.close();
		in.close();
		 

	    new SwingWrapper(ts.getChart()).displayChart();
	    new SwingWrapper(mfi.getChart()).displayChart();
	}

}
