package market;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import utilities.Assert;

public class HistoryData extends VirtualData {
	private String filename;
	private BufferedReader br;
	private DataInputStream in;

	private String next_line;

	private String runName;
	private String exchangeName;
	private long startTime;
	private long endTime;
	private long currentTime;
	private int i=0;

	public HistoryData(Market market, Wallet wallet, String filename) {
		super(market, wallet);
		this.filename = filename;

		try {
			FileInputStream fstream = new FileInputStream(this.filename);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));

			runName = br.readLine(); // Ligne 1: Le nom du run
			exchangeName = br.readLine(); // Ligne 2: Le nom de la bourse d'echange
			startTime = Long.parseLong(br.readLine()); // Ligne 3: L'heure du debut du run en timestamp (celui qu'on recupere avec System.currentTimeMillis() )
			endTime = Long.parseLong(br.readLine()); // Ligne 4: L'heure du debut du run en timestamp (celui qu'on recupere avec System.currentTimeMillis() )

			next_line = br.readLine();
			nextData();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void nextData() throws EndOfRun {
		if (next_line == null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
			throw new EndOfRun();
		}

		try {
			currentTime = Long.parseLong(next_line);

			market.jsonTicker = br.readLine();
			Assert.checkPrecond(market.jsonTicker != null, "Fin du fichier innatendue (rien apres currentTime)");

			market.jsonDepth = br.readLine();
			Assert.checkPrecond(market.jsonDepth != null, "Fin du fichier innatendue (rien apres ticker)");

			market.jsonTrades = br.readLine();
			Assert.checkPrecond(market.jsonTrades != null, "Fin du fichier innatendue(rien apres depth)");

			next_line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		System.out.println("iteee="+i++);

	}


	public String getRunName() {
		return runName;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public long getEndTime() {
		return endTime;
	}

}
