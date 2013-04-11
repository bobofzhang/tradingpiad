package ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import market.Currency;
import market.ExchangeError;
import market.Market;
import market.Wallet;
import market.bitstamp.MarketBitstampVirtual;
import market.btce.MarketBtceHistory;
import market.mtgox.MarketMtgoxHistory;
import net.miginfocom.swing.MigLayout;
import strategies.Agent;
import strategies.Strategy;
import strategies.marketmaking.ForecastStrategy;
import strategies.marketmaking.MarketMaking;
import utilities.Decimal;
import forecast.TestIndicator;
import javax.swing.JTabbedPane;


public class Gui {

	private JFrame frame;
	private JTextField amount;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws ExchangeError, NoSuchAlgorithmException, KeyManagementException, IOException {
		
		
		
		// Pour que ca fonctionne a l'univ !
		System.setProperty("java.net.useSystemProxies", "true");
		
		// Pour resoudre le probleme des certificats
		X509TrustManager trm = new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {

            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[] { trm }, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        
        
        System.setProperty("http.agent", "");
		
		Wallet wal=new Wallet();
		wal.setAmount(Currency.USD, new BigDecimal(1000.0));
		wal.setAmount(Currency.BTC, new BigDecimal(10.0));
		Market m =new MarketBitstampVirtual(Currency.BTC, Currency.USD,wal,60000,System.currentTimeMillis()+600000);
		
		
		
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	
	public static void runStrat() throws ExchangeError{
		Wallet wal=new Wallet();
		wal.setAmount(Currency.USD, new BigDecimal(1000.0));
		//Market m =new MarketMtgoxVirtual(Currency.BTC, Currency.USD,wal,30000,System.currentTimeMillis()+48*60*60000);
		
		Market m =new MarketBtceHistory(Currency.BTC, Currency.USD,wal,"btce48h_3003.txt");
		
		
		//DataRetriever d= new DataRetriever("mtgox48h_3003.txt","tradehist_mtgox48h_3003.txt",m);
		//d.retrieve();
		
		
		Strategy mmaking= new MarketMaking();
		Agent a=new Agent(mmaking,m,wal);
		a.init();
		a.execute();
		

	}
	
	public static void runStrat(BigDecimal amount , Market market , Currency c1, Currency c2 , Strategy stra) throws ExchangeError{
		//Wallet wal=new Wallet();
		//wal.setAmount(Currency.USD,  amount);
		//Market m =new MarketMtgoxVirtual(Currency.BTC, Currency.USD,wal,30000,System.currentTimeMillis()+48*60*60000);
		
		//Market m =new MarketBtceHistory(Currency.BTC, Currency.USD,wal,"btce48h_3003.txt");
		
		
		//DataRetriever d= new DataRetriever("mtgox48h_3003.txt","tradehist_mtgox48h_3003.txt",m);
		//d.retrieve();
		
	//	Agent a=new Agent(stra,market,wal);
		//a.init();
		//a.execute();
		

	}
	
	

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[left][206.00,grow]", "[][][][][][][][]"));

		JLabel lblNewLabel = new JLabel("Chose a Market");
		frame.getContentPane().add(lblNewLabel, "cell 0 0");

		final JComboBox market = new JComboBox();
		market.setModel(new DefaultComboBoxModel(new String[] {"MtGox", "BTCE", "BitStamp"}));
		frame.getContentPane().add(market, "cell 1 0,growx");

		JLabel lblChoseACurrency = new JLabel("Chose a currency 1");
		frame.getContentPane().add(lblChoseACurrency, "cell 0 1");

		final JComboBox currency1 = new JComboBox();
		currency1.setModel(new DefaultComboBoxModel(Currency.values()));
		frame.getContentPane().add(currency1, "cell 1 1,growx");
		
		JLabel lblChooseCurrency = new JLabel("Choose currency 2");
		frame.getContentPane().add(lblChooseCurrency, "cell 0 2,alignx left");
		
		final JComboBox currency2 = new JComboBox();
		currency2.setModel(new DefaultComboBoxModel(Currency.values()));
		frame.getContentPane().add(currency2, "cell 1 2,growx");
		
		JLabel label = new JLabel("");
		frame.getContentPane().add(label, "flowx,cell 0 4,alignx trailing");
		
		textField = new JTextField();
		frame.getContentPane().add(textField, "cell 1 4,growx");
		textField.setColumns(10);

		
		JLabel lblChooseAStrategy = new JLabel("Choose a Strategy");
		frame.getContentPane().add(lblChooseAStrategy, "cell 0 5,alignx left");
		
		final JComboBox strategy = new JComboBox();
		strategy.setModel(new DefaultComboBoxModel(new String[] {"Market Making", "Forecast"}));


		
		
		JLabel lblHowMuchDo = new JLabel("How much do you want to invest ? ");
		frame.getContentPane().add(lblHowMuchDo, "cell 0 3,alignx left");

		amount = new JTextField();
		frame.getContentPane().add(amount, "cell 1 3,growx");
		amount.setColumns(10);
		
		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Strategy stra = null;
				Market mar = null ; 
				Currency c1,c2;
				BigDecimal am = new BigDecimal("0");
				String s; 
			
				Wallet wal=new Wallet();
				wal.setAmount(Currency.USD,  am);
				
				//retreiving the 
				s  = amount.getText();
				am = new Decimal(s);

				
				 c1 = (Currency) currency1.getSelectedItem();
				 c2 = (Currency) currency2.getSelectedItem();
				
				 
				if (strategy.getSelectedItem().toString() == "Market Making"){

					stra = new MarketMaking();
				
				}else if ( strategy.getSelectedItem().toString() == "Forecast"){
					stra = new ForecastStrategy(new BigDecimal(1000));
				}
				
				
				if(market.getSelectedItem().toString()=="MtGox"){
					System.out.println(market.getSelectedItem().toString() );
							try {
								mar = new MarketMtgoxHistory(c1, c2,wal,"btce48h_3003.txt");
							} catch (ExchangeError e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	
						
				}
				if (market.getSelectedItem().toString() == "BTCE"){
					
					System.out.println(market.getSelectedItem().toString() );
					
					try {
						mar = new MarketBtceHistory(c1, c2,wal,"btce48h_3003.txt");
					} catch (ExchangeError e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				
					Agent a=new Agent(stra,mar,wal);

					try {
						a.init();
					} catch (ExchangeError e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					a.execute();
			}	
		});
		
		frame.getContentPane().add(strategy, "cell 1 5,growx");
		frame.getContentPane().add(btnRun, "cell 1 6,alignx center");
		
		JSeparator separator = new JSeparator();
		frame.getContentPane().add(separator, "cell 0 7,growx");

		JSeparator separator_1 = new JSeparator();
		frame.getContentPane().add(separator_1, "flowx,cell 1 7,growx");
		
		JLabel lblHowMuchDo_1 = new JLabel("How much do you want to start with ? ");
		frame.getContentPane().add(lblHowMuchDo_1, "cell 0 4");

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNew = new JMenuItem("New");
		mnFile.add(mntmNew);

		JMenuItem mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);

		JMenuItem mntmReset = new JMenuItem("Reset");
		mnFile.add(mntmReset);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		mnFile.add(mntmQuit);
	}

}