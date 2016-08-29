package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.AWTException;
import java.awt.BorderLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import core.Config;
import core.MySQLConnector;
import crud.DataLoader;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.SystemTray;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;

public class Main {

	// Components
	private JFrame frmMyWords;
	private JFrame frmAbout;
	private JFrame frmLanguage;
	private JTable table;
	
	// Class variables
	private boolean first_run = true;
	
	// Objects
	private MySQLConnector connection = new MySQLConnector();
	private Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	private Logger logger = Logger.getLogger(Main.class.getName());
	private Config config = new Config();
	private DefaultTableModel table_model;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmMyWords.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		initialize();
	}

	private void initialize() {
		frmMyWords = new JFrame();
		frmMyWords.getContentPane().setBackground(Color.DARK_GRAY);
		frmMyWords.setTitle("My Words");
		frmMyWords.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/icons/app.png")));
		frmMyWords.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frmMyWords.setSize(1024, 768);
		frmMyWords.setLocation(dim.width / 2 - frmMyWords.getWidth() / 2, dim.height / 2 - frmMyWords.getHeight() / 2);
		frmMyWords.getContentPane().setLayout(new BorderLayout(0, 0));

		if (first_run == true) {
			INIT_SYSTEM_TRAY();
			first_run = false;
		}
		if (table_model == null)
			INIT_TABLE_MODEL();

		JPanel left = new JPanel();
		left.setBackground(Color.DARK_GRAY);
		frmMyWords.getContentPane().add(left, BorderLayout.WEST);
		left.setLayout(new MigLayout("", "[162px]", "[64px][76px][76px][76px][][]"));

		JLabel lblBrand = new JLabel("MyWords");
		lblBrand.setIcon(new ImageIcon(Main.class.getResource("/icons/app.png")));
		lblBrand.setFont(new Font("Serif", Font.BOLD, 20));
		lblBrand.setForeground(Color.LIGHT_GRAY);
		left.add(lblBrand, "cell 0 0,grow");

		JButton btnAdd = new JButton("Insert");
		btnAdd.addActionListener(e -> {
			Console.operation = 2;
			Console.main(null);
		});
		btnAdd.setFont(new Font("Serif", Font.BOLD, 14));
		btnAdd.setForeground(Color.LIGHT_GRAY);
		btnAdd.setBackground(Color.DARK_GRAY);
		btnAdd.setHorizontalAlignment(SwingConstants.LEFT);
		btnAdd.setIcon(new ImageIcon(Main.class.getResource("/icons/add_64.png")));
		left.add(btnAdd, "cell 0 1,growx,aligny top");

		JButton btnModify = new JButton("Update");
		btnModify.addActionListener(e -> {
			Console.operation = 1;
			Console.main(null);
		});
		btnModify.setFont(new Font("Serif", Font.BOLD, 14));
		btnModify.setForeground(Color.LIGHT_GRAY);
		btnModify.setBackground(Color.DARK_GRAY);
		btnModify.setHorizontalAlignment(SwingConstants.LEFT);
		btnModify.setIcon(new ImageIcon(Main.class.getResource("/icons/edit_64.png")));
		left.add(btnModify, "cell 0 2,growx,aligny top");

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(e -> {
			Console.operation = 0;
			Console.main(null);
		});
		btnDelete.setFont(new Font("Serif", Font.BOLD, 14));
		btnDelete.setForeground(Color.LIGHT_GRAY);
		btnDelete.setBackground(Color.DARK_GRAY);
		btnDelete.setHorizontalAlignment(SwingConstants.LEFT);
		btnDelete.setIcon(new ImageIcon(Main.class.getResource("/icons/delete_64.png")));
		left.add(btnDelete, "cell 0 3,growx,aligny top");

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setBackground(Color.DARK_GRAY);
		frmMyWords.getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		table.setModel(table_model);
		table.setSelectionBackground(Color.LIGHT_GRAY);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(true);
		table.setForeground(Color.LIGHT_GRAY);
		table.setBackground(Color.DARK_GRAY);
		table.setFont(new Font("Serif", Font.BOLD, 13));
		scrollPane.setViewportView(table);

		JLabel lblCount = new JLabel("There are " + table.getModel().getRowCount() + " words in the DB");
		lblCount.setForeground(Color.LIGHT_GRAY);
		lblCount.setFont(new Font("Serif", Font.BOLD, 12));
		left.add(lblCount, "cell 0 5");

		JPanel bottom = new JPanel();
		bottom.setBackground(Color.DARK_GRAY);
		frmMyWords.getContentPane().add(bottom, BorderLayout.SOUTH);

		JButton btnMinimize = new JButton("Minimize");
		btnMinimize.addActionListener(e -> frmMyWords.setVisible(false));
		btnMinimize.setFont(new Font("Serif", Font.BOLD, 14));
		btnMinimize.setForeground(Color.LIGHT_GRAY);
		btnMinimize.setBackground(Color.DARK_GRAY);
		btnMinimize.setIcon(new ImageIcon(Main.class.getResource("/icons/minimize_32.png")));
		bottom.add(btnMinimize);

		JMenuBar menuBar = new JMenuBar();
		frmMyWords.setJMenuBar(menuBar);

		JMenu mnRefresh = new JMenu("Refresh");
		mnRefresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// load new data from db
				INIT_TABLE_MODEL();
				// set new data to table
				table.setModel(table_model);
				// unselect menu
				mnRefresh.setSelected(false);
				// set new count data to label
				lblCount.setText("There are " + table.getModel().getRowCount() + " words in the DB!");
			}
		});
		mnRefresh.setIcon(new ImageIcon(Main.class.getResource("/icons/refresh_16.png")));
		menuBar.add(mnRefresh);

		JMenu mnOptions = new JMenu("Options");
		mnOptions.setIcon(new ImageIcon(Main.class.getResource("/icons/options_16.png")));
		menuBar.add(mnOptions);

		JMenuItem mntmSetNative = new JMenuItem("Set native & second languages");
		mntmSetNative.addActionListener(e -> LANGUAGE());
		mntmSetNative.setIcon(new ImageIcon(Main.class.getResource("/icons/language_16.png")));
		mnOptions.add(mntmSetNative);

		JMenuItem mntmAboutApplication = new JMenuItem("About application");
		mntmAboutApplication.addActionListener(e -> ABOUT());
		mntmAboutApplication.setIcon(new ImageIcon(Main.class.getResource("/icons/about_16.png")));
		mnOptions.add(mntmAboutApplication);

		JMenuItem mntmCloseApplication = new JMenuItem("Close app");
		mntmCloseApplication.addActionListener(e -> System.exit(0));
		mntmCloseApplication.setIcon(new ImageIcon(Main.class.getResource("/icons/exit_16.png")));
		mnOptions.add(mntmCloseApplication);
	}

	public void INIT_TABLE_MODEL() {
		// create a data loader object
		DataLoader load = new DataLoader(connection.GET_MY_DATA_SOURCE());
		// create an empty table model
		DefaultTableModel empty_model = new DefaultTableModel(new Object[] { "Nothing" }, 0);
		// load from db and set it new data to model
		DefaultTableModel data = load.GET_DEFAULT_TABLE_MODEL("");
		// if data is not null set data else set empty model, which is modified.
		table_model = (data != null) ? data : empty_model;
	}

	public void INIT_SYSTEM_TRAY() {
		SystemTray tray = null;

		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();

			TrayIcon icon = new TrayIcon(
					Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/icons/app_16.png")),
					"Click to open application");
			icon.setImageAutoSize(true);
			icon.addActionListener(e -> frmMyWords.setVisible(true));

			try {
				tray.add(icon);
			} catch (AWTException e) {
				logger.error(e);
			}

		}
	}

	private void LANGUAGE() {
		frmLanguage = new JFrame("Language selector");
		frmLanguage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmLanguage.setBounds(100, 100, 451, 165);
		frmLanguage.setUndecorated(true);
		frmLanguage.setResizable(false);
		frmLanguage.setVisible(true);
		frmLanguage
				.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/language_16.png")));
		frmLanguage.setLocation(dim.width / 2 - frmLanguage.getSize().width / 2,
				dim.height / 2 - frmLanguage.getSize().height / 2);

		JPanel contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new LineBorder(Color.LIGHT_GRAY));
		frmLanguage.setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow]", "[][][][][]"));

		JLabel lblSelectNativeLanguage = new JLabel("Native language");
		lblSelectNativeLanguage.setForeground(Color.LIGHT_GRAY);
		lblSelectNativeLanguage.setFont(new Font("Serif", Font.BOLD, 12));
		contentPane.add(lblSelectNativeLanguage, "cell 0 0");

		JTextField txtNative = new JTextField(config.GET_CONFIG("native_lang"));
		txtNative.setFont(new Font("Serif", Font.BOLD, 12));
		txtNative.setBackground(Color.DARK_GRAY);
		txtNative.setForeground(Color.LIGHT_GRAY);
		contentPane.add(txtNative, "cell 0 1,growx");
		txtNative.setColumns(10);

		JLabel lblSelectSecondLanguage = new JLabel("Second language");
		lblSelectSecondLanguage.setForeground(Color.LIGHT_GRAY);
		lblSelectSecondLanguage.setFont(new Font("Serif", Font.BOLD, 12));
		contentPane.add(lblSelectSecondLanguage, "cell 0 2");

		JTextField txtSecond = new JTextField(config.GET_CONFIG("second_lang"));
		txtSecond.setFont(new Font("Serif", Font.BOLD, 12));
		txtSecond.setForeground(Color.LIGHT_GRAY);
		txtSecond.setColumns(10);
		txtSecond.setBackground(Color.DARK_GRAY);
		contentPane.add(txtSecond, "cell 0 3,growx");

		JButton btnSet = new JButton("Set");
		btnSet.addActionListener(e -> {
			String nativeText = txtNative.getText();
			String secondText = txtSecond.getText();

			if (nativeText != null && secondText != null && !nativeText.equals("") && !secondText.equals("")) {
				
				if (config.SET_CONFIG("native_lang", nativeText) && config.SET_CONFIG("second_lang", secondText)) {
					JOptionPane.showMessageDialog(null, "Settings saved!", "Success!", JOptionPane.PLAIN_MESSAGE);
					frmLanguage.dispose();
				} else
					JOptionPane.showMessageDialog(null,
							"Something bad happened! Please, look at the 'mywords.log' file! ", "Error",
							JOptionPane.ERROR_MESSAGE);
				
			} else 
					JOptionPane.showMessageDialog(null,
						"Please, write something! You can't save it blank!", "Error",
						JOptionPane.ERROR_MESSAGE);

		});
		btnSet.setIcon(new ImageIcon(Main.class.getResource("/icons/submit_16.png")));
		btnSet.setFont(new Font("Serif", Font.BOLD, 12));
		btnSet.setForeground(Color.LIGHT_GRAY);
		btnSet.setBackground(Color.DARK_GRAY);
		contentPane.add(btnSet, "cell 0 4");

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setIcon(new ImageIcon(Main.class.getResource("/icons/exit_16.png")));
		btnCancel.addActionListener(e -> frmLanguage.dispose());
		btnCancel.setFont(new Font("Serif", Font.BOLD, 12));
		btnCancel.setForeground(Color.LIGHT_GRAY);
		btnCancel.setBackground(Color.DARK_GRAY);
		contentPane.add(btnCancel, "cell 0 4 1");
	}

	private void ABOUT() {
		frmAbout = new JFrame();
		frmAbout.setUndecorated(true);
		frmAbout.setResizable(false);
		frmAbout.setVisible(true);
		frmAbout.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmAbout.setBounds(100, 100, 435, 141);
		frmAbout.setAlwaysOnTop(true);
		frmAbout.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/about_16.png")));
		frmAbout.setLocation(dim.width / 2 - frmAbout.getSize().width / 2,
				dim.height / 2 - frmAbout.getSize().height / 2);

		JPanel contentPane = new JPanel();
		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				frmAbout.dispose();
			}
		});
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new LineBorder(Color.LIGHT_GRAY));
		frmAbout.setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel image = new JLabel("");
		image.setIcon(new ImageIcon(getClass().getResource("/icons/java_128.png")));
		image.setBounds(10, 10, 141, 120);
		contentPane.add(image);

		JLabel lblAppName = new JLabel("MyWords V1.0");
		lblAppName.setForeground(Color.LIGHT_GRAY);
		lblAppName.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblAppName.setBounds(175, 28, 250, 25);
		contentPane.add(lblAppName);

		JLabel lblNameDeveloper = new JLabel("Kamal Abdullayev");
		lblNameDeveloper.setForeground(Color.LIGHT_GRAY);
		lblNameDeveloper.setFont(new Font("SansSerif", Font.BOLD, 12));
		lblNameDeveloper.setBounds(176, 59, 249, 16);
		contentPane.add(lblNameDeveloper);

		JLabel lblContactMe = new JLabel("If you have questions, contact me!");
		lblContactMe.setForeground(Color.LIGHT_GRAY);
		lblContactMe.setBounds(176, 80, 260, 16);
		contentPane.add(lblContactMe);

		JLabel lblEmail = new JLabel("abdullayev007@gmail.com");
		lblEmail.setForeground(Color.LIGHT_GRAY);
		lblEmail.setBounds(176, 102, 249, 16);
		contentPane.add(lblEmail);
	}

}
