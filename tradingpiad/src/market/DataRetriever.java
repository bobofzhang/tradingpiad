package market;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import market.mtgox.MarketMtgoxVirtual;
import market.bitstamp.MarketBitstampVirtual;
import market.btce.MarketBtceVirtual;
import utilities.Assert;
import utilities.Decimal;
import utilities.Op;
/**
 * 
 * @author Kevin
 *
 * Classe permetant de recolter des donnes du marche en live (si on veut recolter des donnes sur X jours, le programme doit tourner X jours) .
 * La recolte se fait dans 2 fichier: un complet avec les informations completes (historique+ticker+cahier des ordres) et un avec juste l'historique des echanges
 * Il fonctionne sur n'importe quelle bourse d'echange de l'application. 
 */
public class DataRetriever {
	FileWriter fstream;
	BufferedWriter out;
	
	FileWriter fstream2;
	BufferedWriter out2;
	
	/**
	 * Le marche (triplet monnaie 1 , monnaie 2, bourse d'echange) sur lequel va s'effectuer la recolte
	 */
	Market market; 

	/**
	 * @param filename Le nom du fichier ou on sauvegarde les donnees completes de la recolte. Ces donnes sont specifiques a chaque bourses d'echanges. Pour plus tard exploiter ce fichier,  il faudra utiliser une des implementations de market suivantes MarketMtgoxHistory, MarketBtceHistory  ou MarketBitstampHistory  
	 * @param filename2 Le nom du fichier ou on sauvegarde juste l'historique des echanges, pour exploiter ce fichier lors d'une simulation, il faudra utiliser la classe MarketPast.
	 * @param market Le marche dont on souhaite recolter des donnees. Cela peut etre soit un MarketMtGoxVirtual, soit un MarketBtceVirtual, soit un MarketBitstampVirtual.
	 */
	public DataRetriever(String filename, String filename2, Market market) {
		Assert.checkPrecond(market instanceof MarketMtgoxVirtual  || market instanceof MarketBtceVirtual || market instanceof MarketBitstampVirtual , "market doit etre soit un MarketMtGoxVirtual, soit un MarketBtceVirtual, soit un MarketBitstampVirtual.");
		this.market = market;
		
		try {
			// Ouverture fichier 1
			fstream = new FileWriter(filename);
			out = new BufferedWriter(fstream);
			
			// Ecriture informations generales au debut du fichier 1
			out.write(filename + "\n");
			out.write(market.getExchangeName() + "\n");
			out.write(String.valueOf(market.getStartTime()) + "\n");
			out.write(String.valueOf(market.getEndTime()) + "\n");

			
			//Ouverture fichier 2
			fstream2 = new FileWriter(filename2);
			out2 = new BufferedWriter(fstream2);	
			
			// Ecriture informations generales au debut du fichier 2
			out2.write(filename + "\n");
			out2.write(market.getExchangeName() + "\n");
			out2.write(market.cur1+"\n");
			out2.write(market.cur2+"\n");
			out2.write(market.getPricePrecision()+"\n");
			out2.write(market.getAmountPrecision()+"\n");
			out2.write(Math.round(Math.log10(market.getPricePrecision().doubleValue()))+"\n");
			out2.write(Math.round(Math.log10(market.getAmountPrecision().doubleValue()))+"\n");
			out2.write(Op.sub(Decimal.ONE,market.subFee(Decimal.ONE))+"\n");
			out2.write(String.valueOf(market.getStartTime()) + "\n");
			out2.write(String.valueOf(market.getEndTime()) + "\n");

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Lancer la recolte de donnees
	 */
	public void retrieve() {
		boolean fini = false;
		try {
			while (!fini) {
				try {
					market.updateAll(); // Mise a jour des donnees
				} catch (Exception e) {
					System.out.println("update all raté  :o");
					System.out.println(market.getJsonTicker());
					try {
						market.waitTimeDelta(); // --> si l'update echoue (erreur venant de la bourse d'echange) , on re -attends dans l'espoir que ca fonctionne la prochaine fois
					} catch (EndOfRun end) {
						fini = true;
					}
					continue;
				}
				
				// Ecriture dans le fichier 1: le temps couyrant + les 3 json (ticker ,depth et trades)
				out.write(String.valueOf(System.currentTimeMillis()));
				out.write("\n");
				out.write(market.getJsonTicker());
				out.write("\n");
				out.write(market.getJsonDepth());
				out.write("\n");
				out.write(market.getJsonTrades());
				out.write("\n");
				
				
				// Ecriture dans le fichier 2 : l'historique des echanges :date, prix, quantite, trade id et type
				for (Trade t : market.last_trades) { // Parcours des echanges recents non ajoutes
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
					market.waitTimeDelta(); // On attends
				} catch (EndOfRun e) {// Si on a depasser le temps final
					fini = true;
				}
			}
			
			
			// Fermeture fichier
			out.close();
			out2.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

}
