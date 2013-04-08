package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import market.ExchangeError;
import market.Trade;
import market.Trades;

public class Util {

	public static <E> void reverse(E[] tab) {
		for (int i = 0; i < tab.length / 2; i++) {
			E tmp = tab[i];
			tab[i] = tab[tab.length - 1 - i];
			tab[tab.length - 1 - i] = tmp;
		}
	}

	public static String getData(URL url) throws ExchangeError{
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22");
			// Set standard HTTP/1.1 no-cache headers.
			conn.addRequestProperty("Cache-Control", "private, no-store, no-cache, must-revalidate,max-age=0");
			conn.setReadTimeout(200*1000);

			
			InputStream is = conn.getInputStream();
			return convertStreamToString(is);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ExchangeError(e1.getMessage());
		}
	}
	/**
	 * Methode pour filtrer que les echanges jamais vu dans le cas ou on peut pas le faire avec l'API (btc-e et bitstamp)
	 */
	public static Trade[] filterRecentTrade(Trade[] tmp_last_trades, Trades hist) {
		if (tmp_last_trades.length == 0)
			return tmp_last_trades;
		else {
			int nb_new_trades = 0;// On va compter le nombre de vrais nouveaux
			// trades
			if (hist.size() > 0) {
				// tant que les echanges recus sont plus r�cents que le dernier
				// recu
				while (nb_new_trades<tmp_last_trades.length && !tmp_last_trades[nb_new_trades].tid.equals(hist.getLast().tid)) {
					nb_new_trades++;
				}
			} else
				// si l'historique est vide, ca veut dire que tous les trades
				// sont nouveaux
				nb_new_trades = tmp_last_trades.length;

			// L� on les ajoute � l'envers car btce inverse l'ordre
			Trade[] last_trades = new Trade[nb_new_trades];
			for (int j = 0; j < last_trades.length; j++)
				last_trades[j] = tmp_last_trades[nb_new_trades - 1 - j];

			return last_trades;
		}
	}
	
	public static String convertStreamToString(InputStream is) {
	    Scanner s = new java.util.Scanner(is,"UTF-8").useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
}
