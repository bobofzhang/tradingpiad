package market;

/**
 * Exception lancee quand uen erreur se produit lors de la recuperation de donnees sur ue bourse d'echange 
 *
 */
@SuppressWarnings("serial")
public class ExchangeError extends Exception {

	public ExchangeError(String string) {
		super(string);// TODO Auto-generated constructor stub
	}

}
