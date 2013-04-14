package market;


/**
 * Le type d'un ordre
 * 
 * Une personne demandant un bien (ici des bitcoins) pose un ordre de type BID. Et celui executant cet ordre est un vendeur qui veut vendre sans attendre.
 * 
 * Une personne offrant un bien pose un ordre de type ASK. Et celui executant cette ordre est un acheteur qui veut acheter sans attendre.
 *
 */
public enum Type {
	ASK,BID;	
}
