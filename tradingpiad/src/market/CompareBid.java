package market;

import java.util.Comparator;


public class CompareBid implements Comparator<AbstractOrder> {
	
	/**
	 * Comparateur d'ordre abstrait (ordre du cahier des ordres (classe Order) ou echange dans l'historique (classe Trade) dans l'ordre croissant des prix.
	 * Il permet notamment de classer le cahier des ordres virtuels (lorsqu'on simule une strategie) cote BID (demandes de Bitcoins).
	 * Ainsi une demande plus competitive (plus elevee en prix)  est avant unde mande moisn competitive (moins elevee en prix).
	 *
	 */
	@Override
	public int compare(AbstractOrder o1, AbstractOrder o2) {
		return o2.compareTo(o1);
	}

}
