package market;

import java.util.Comparator;


/**
 * Comparateur d'ordre abstrait (ordre du cahier des ordres(Order) ou echange dans l'historique(Trade)) dans l'ordre croissant des prix.
 * Il permet notamment de classer le cahier des ordres virtuel (lorsqu'on simule une strategie) cote ASK (offres de Bitcoins)
 *  Ainsi une offre plus competitive (moins chere)  est avant un offre moins competitive (plus chere).
 *
 */
public class CompareAsk implements Comparator<AbstractOrder> {

	@Override
	public int compare(AbstractOrder o1, AbstractOrder o2) {
		return o1.compareTo(o2);// Un ordre est plus petit qu'un autre si son prix est plus petit que celui de l'autre
	}

}
