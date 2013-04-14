package market;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import utilities.Assert;

/**
 * Classe utilisee lors de d'une simulation à partir de donnees historiques completes (json ticker+depth +trades)
 * Les implementations de Market:  MarketMtgoxHistory, MarketBtceHistory et MarketBitstamp History utilisent cette classe (cette classe est la surout pour ne pas refaire 3 fois)
 */
public class HistoryData extends VirtualData {
	private String filename; // Le fichier  ou sont enrregistrees les donnees du marche
	
	private BufferedReader br;
	private DataInputStream in;
	private String next_line;

	private String runName; // Le nom du run
	private String exchangeName; // Le nomde la bourse d'echange
	private long startTime; // Date de depart du run en time millis
	private long endTime; // Date de fin du run en time millis
	private long currentTime; // Date courante

	public HistoryData(Market market, Wallet wallet, String filename) {
		super(market, wallet);
		this.filename = filename;

		try {
			
			// Creation/ouverture du fichier
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

	/**
	 * 
	 * Passer au donnees du temps suivant
	 * @throws EndOfRun S'il n'y a pas de donnes après (fin du run)
	 */
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

			market.jsonTicker = br.readLine(); // ticker suiavnt
			Assert.checkPrecond(market.jsonTicker != null, "Fin du fichier innatendue (rien apres currentTime)");

			market.jsonDepth = br.readLine(); // depth suivante
			Assert.checkPrecond(market.jsonDepth != null, "Fin du fichier innatendue (rien apres ticker)");

			market.jsonTrades = br.readLine(); // trades suivantes
			Assert.checkPrecond(market.jsonTrades != null, "Fin du fichier innatendue(rien apres depth)");

			next_line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

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
