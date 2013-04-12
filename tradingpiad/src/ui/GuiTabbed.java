package ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

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

public class GuiTabbed {

	private JFrame frame;
	private JTextField amount;
	private JTextField textField;
	private JTextField loadPath1;
	private JTextField loadPath2;


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
					GuiTabbed window = new GuiTabbed();
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
	public GuiTabbed() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, "cell 0 0,grow");
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Strategy", null, panel, null);
		panel.setLayout(new MigLayout("", "[212.00,leading][281.00,trailing]", "[][][][][]"));
		

		
		
		JLabel lblNewLabel = new JLabel("Chose a Market");
		panel.add(lblNewLabel, "cell 0 0");
		
		final JComboBox market = new JComboBox();
		market.setModel(new DefaultComboBoxModel(new String[] {"MtGox", "BTCE", "BitStamp"}));
		panel.add(market, "cell 1 0,growx");

		JLabel lblChoseACurrency = new JLabel("Chose a currency 1");
		panel.add(lblChoseACurrency, "cell 0 1");

		final JComboBox currency1 = new JComboBox();
		currency1.setModel(new DefaultComboBoxModel(Currency.values()));
		panel.add(currency1, "cell 1 1,growx");
		
		JLabel lblChooseCurrency = new JLabel("Choose currency 2");
		panel.add(lblChooseCurrency, "cell 0 2,alignx left");
		
		final JComboBox currency2 = new JComboBox();
		currency2.setModel(new DefaultComboBoxModel(Currency.values()));
		panel.add(currency2, "cell 1 2,growx");
		
		JLabel label = new JLabel("");
		panel.add(label, "flowx,cell 0 4,alignx trailing");
		
		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setText("0");
		panel.add(textField, "cell 1 4,growx");
		textField.setColumns(10);

		
		
		JLabel lblChooseAStrategy = new JLabel("Choose a Strategy");
		panel.add(lblChooseAStrategy, "cell 0 5,alignx left");
		
		final JComboBox strategy = new JComboBox();
		strategy.setModel(new DefaultComboBoxModel(new String[] {"Market Making", "Forecast"}));


		
		
		JLabel lblHowMuchDo = new JLabel("How much do you want to invest ? ");
		panel.add(lblHowMuchDo, "cell 0 3,alignx left");

		amount = new JTextField();
		amount.setHorizontalAlignment(SwingConstants.CENTER);
		amount.setText("0");
		panel.add(amount, "cell 1 3,growx");
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
		
		panel.add(strategy, "cell 1 5,growx");
		panel.add(btnRun, "cell 1 6,alignx center");
		
		JLabel lblHowMuchDo_1 = new JLabel("How much do you want to start with ? ");
		panel.add(lblHowMuchDo_1, "cell 0 4");
		
		
		/**
		 * Here i'll create and initialize the secound tab -> Data retrieve
		 */
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Data Retreive", null, panel_1, null);
		panel_1.setLayout(new MigLayout("", "[209.00][grow]", "[][][][]"));
		
		JLabel lblChooseAMethode = new JLabel("Choose a methode");
		panel_1.add(lblChooseAMethode, "cell 0 0");
		
		JRadioButton rdbtnLive = new JRadioButton("Live");
		panel_1.add(rdbtnLive, "flowx,cell 1 0");
		rdbtnLive.setMnemonic(KeyEvent.VK_B);
		rdbtnLive.setActionCommand("true");
		rdbtnLive.setSelected(true);
		
		JRadioButton rdbtnPast = new JRadioButton("Past");
		panel_1.add(rdbtnPast, "cell 1 0");
		
		
		 ButtonGroup group = new ButtonGroup();
		 group.add(rdbtnLive);
		 group.add(rdbtnPast);   
		 
	//	rdbtnLive.addActionListener(this);
	//	rdbtnPast.addActionListener(this);
		 
		
	//	public void actionPerformed(ActionEvent e) { }
		
		JLabel lblChooseAMarket = new JLabel("Choose a market");
		panel_1.add(lblChooseAMarket, "cell 0 1,alignx left");
		lblChooseAMarket.setVisible(false);
		
		JComboBox data_market = new JComboBox();
		data_market.setModel(new DefaultComboBoxModel(new String[] {"MtGox", "BTCE", "BitStamp"}));
		panel_1.add(data_market, "cell 1 1,growx");
		data_market.setVisible(false);
		
		
		JLabel lblFilename = new JLabel("FileName 1 ");
		panel_1.add(lblFilename, "cell 0 2,alignx left");
		lblFilename.setVisible(false);
		
		
		loadPath1 = new JTextField();
		panel_1.add(loadPath1, "flowx,cell 1 2,alignx left");
		loadPath1.setColumns(10);
		loadPath1.setVisible(false);
		
		JLabel lblFilename_1 = new JLabel("FileName 2");
		panel_1.add(lblFilename_1, "cell 0 3,alignx left");
		lblFilename_1.setVisible(false);
		
		loadPath2 = new JTextField();
		panel_1.add(loadPath2, "flowx,cell 1 3,alignx left");
		loadPath2.setColumns(10);
		loadPath2.setVisible(false);
		
		//Create a file chooser
		final JFileChooser fc = new JFileChooser();

		//In response to a button click:
	//	int returnVal = fc.showOpenDialog(aComponent);
		
		
		JButton btnLoadF1 = new JButton("Load");
		btnLoadF1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int status = fc.showOpenDialog(null);
				if(status == JFileChooser.APPROVE_OPTION){
					File selectedFile = fc.getSelectedFile();
					loadPath1.setText(selectedFile.getAbsolutePath());
				}
			} 
		});
		panel_1.add(btnLoadF1, "cell 1 2");
		
		JButton btnLoadF2 = new JButton("Load");
		btnLoadF2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int status = fc.showOpenDialog(null);
				if(status == JFileChooser.APPROVE_OPTION){
					File selectedFile = fc.getSelectedFile();
					loadPath2.setText(selectedFile.getAbsolutePath());
				}
			} 
		});
		panel_1.add(btnLoadF2, "cell 1 3");
		
		
		
		if(rdbtnLive.isSelected()){
			rdbtnPast.setSelected(false);
			lblChooseAMarket.setVisible(true);
			data_market.setVisible(true);
			lblFilename.setVisible(true);
			loadPath1.setVisible(true);
			lblFilename_1.setVisible(true);
			loadPath2.setVisible(true);
		}
		
		/**
		 * The following part is the Application menu
		 */
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

