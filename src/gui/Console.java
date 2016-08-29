package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import core.MySQLConnector;
import crud.Crud;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Toolkit;
import java.sql.Date;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import net.miginfocom.swing.MigLayout;
import javax.swing.JProgressBar;

public class Console extends JFrame {

	private static final long serialVersionUID = 1L;
	
	// Components
	private JPanel contentPane;
	private JTextArea txtQueries;
	// progress panel and progress bar
	private JPanel progressPanel;
	private JProgressBar progressBar;
	// button panel and buttons
	private JPanel buttons;
	private JButton btnClose;
	private JButton btnRun;
	private JButton btnAdd;
	
	// Class variables
	public static int operation = 2;
	private String query_delete = "DELETE FROM words WHERE wordID = ?;";
	private String query_update = "UPDATE words SET native = '?', second = '?' WHERE wordID = ?;";
	private String query_insert = "INSERT INTO words (native, second, description, added) \nVALUES('Azərbaycan dilində', 'İngilis dilində', 'No', '"
			+ new Date(new java.util.Date().getTime()) + "');";
	private String query = null;

	// Objects
	private BackgroundWorker sw = null;
	private Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	private MySQLConnector connection = new MySQLConnector();
	private Crud crud = null;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Console frame = new Console();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Console() {
		// determine query type
		if (operation == 0) query = query_delete; else if (operation == 1) query = query_update; else query = query_insert;

		setIconImage(Toolkit.getDefaultToolkit().getImage(Console.class.getResource("/icons/command_32.png")));
		setTitle("Console");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 511, 350);
		setResizable(false);
		setLocation(dim.width / 2 - getWidth() / 2, dim.height / 2 - getHeight() / 2);

		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		txtQueries = new JTextArea();
		txtQueries.setFont(new Font("Serif", Font.BOLD, 13));
		txtQueries.setText(query);
		txtQueries.setForeground(Color.LIGHT_GRAY);
		txtQueries.setBackground(Color.DARK_GRAY);
		scrollPane.setViewportView(txtQueries);

		JLabel lblRunSqlCommand = new JLabel("RUN SQL COMMAND");
		lblRunSqlCommand.setIcon(new ImageIcon(Console.class.getResource("/icons/command_32.png")));
		lblRunSqlCommand.setFont(new Font("Serif", Font.BOLD, 20));
		lblRunSqlCommand.setForeground(Color.LIGHT_GRAY);
		contentPane.add(lblRunSqlCommand, BorderLayout.NORTH);

		JPanel bottom = new JPanel();
		bottom.setBackground(Color.DARK_GRAY);
		contentPane.add(bottom, BorderLayout.SOUTH);
		bottom.setLayout(new BorderLayout(0, 0));

		progressPanel = new JPanel();
		progressPanel.setBackground(Color.DARK_GRAY);
		bottom.add(progressPanel, BorderLayout.NORTH);
		progressPanel.setLayout(new BorderLayout(0, 0));

		progressBar = new JProgressBar();
		progressPanel.add(progressBar);

		buttons = new JPanel();
		buttons.setBackground(Color.DARK_GRAY);
		bottom.add(buttons, BorderLayout.CENTER);
		buttons.setLayout(new MigLayout("", "[87px][97px]", "[44px]"));

		btnRun = new JButton("Run");
		buttons.add(btnRun, "cell 0 0,alignx left,aligny top");
		btnRun.addActionListener(e -> {
			sw = new BackgroundWorker();
			sw.execute();
		});

		btnRun.setIcon(new ImageIcon(Console.class.getResource("/icons/db_run_32.png")));
		btnRun.setForeground(Color.LIGHT_GRAY);
		btnRun.setBackground(Color.DARK_GRAY);
		
		if (operation == 2) {
			btnAdd = new JButton("Insert GUI");
			btnAdd.addActionListener(e -> {
				InsertGUI.main(null);
				dispose();
			});
			btnAdd.setIcon(new ImageIcon(Console.class.getResource("/icons/add_32.png")));
			btnAdd.setBackground(Color.DARK_GRAY);
			btnAdd.setForeground(Color.LIGHT_GRAY);
			buttons.add(btnAdd);
			setSize(712, 350);
			setLocation(dim.width / 2 - getWidth() / 2, dim.height / 2 - getHeight() / 2);
		}
		
		btnClose = new JButton("Close");
		buttons.add(btnClose, "cell 1 0,alignx left,aligny top");
		btnClose.addActionListener(e -> dispose());
		btnClose.setIcon(new ImageIcon(Console.class.getResource("/icons/cancel_32.png")));
		btnClose.setBackground(Color.DARK_GRAY);
		btnClose.setForeground(Color.LIGHT_GRAY);
		
	}

	private void executeUpdate() {
		// create a queries string and get from user
		String queries = txtQueries.getText();

		// create an indices array list and add a zero point.
		ArrayList<Integer> indices = new ArrayList<>();
		indices.add(0);

		// create a queries array list
		ArrayList<String> parsedQueries = new ArrayList<>();

		// find all indices which are end with semicolon
		for (int i = 0; i < queries.length(); i++) {
			if (String.valueOf(queries.charAt(i)).equals(";"))
				indices.add(i + 1);
		}

		// add all queries to array list
		for (int i = 1; i < indices.size(); i++) {
			parsedQueries.add(queries.substring(indices.get(i - 1), indices.get(i)).trim());
		}

		// check if query input empty or not
		if (queries.length() > 0) {
			crud = new Crud(connection.GET_MY_DATA_SOURCE());

			// check if queries ran or not
			if (crud.RUN_SQL_QUERY(parsedQueries)) {
				JOptionPane.showMessageDialog(null, "SQL Query successfully executed!", "Success!",
						JOptionPane.PLAIN_MESSAGE);
			} else
				JOptionPane.showMessageDialog(null,
						"There are some errors in MySQL, be sure that everything is okay with DB. For more information, please check the 'mywrods.log' file!",
						"SQL Query Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	class BackgroundWorker extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			progressBar.setIndeterminate(true);
			btnRun.setEnabled(false);
			if(operation == 2) btnAdd.setEnabled(false);
			btnClose.setEnabled(false);
			Thread.sleep(500);
			executeUpdate();
			return null;
		}

		@Override
		public void done() {
			btnRun.setEnabled(true);
			if(operation == 2) btnAdd.setEnabled(true);
			btnClose.setEnabled(true);
			progressBar.setIndeterminate(false);
		}
	};

}
