package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import core.Config;
import core.MySQLConnector;
import core.WordChecker;
import crud.Crud;

import java.awt.Color;
import java.awt.Dimension;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Toolkit;

public class InsertGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtSecond;
	private JTextField txtNative;
	private JTextField txtDescr;

	private Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	private Crud CRUD = null;
	private MySQLConnector connection = new MySQLConnector();
	private Config conf = new Config();

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InsertGUI frame = new InsertGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public InsertGUI() {
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(InsertGUI.class.getResource("/icons/add_64.png")));
		setBackground(Color.DARK_GRAY);
		setTitle("Add new word");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 302);
		setLocation(dim.width / 2 - getWidth() / 2, dim.height / 2 - getHeight() / 2);

		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow]", "[][][][][][][][]"));

		JLabel lblAddNewWord = new JLabel("Add new word");
		lblAddNewWord.setIcon(new ImageIcon(InsertGUI.class.getResource("/icons/add_32.png")));
		lblAddNewWord.setForeground(Color.LIGHT_GRAY);
		lblAddNewWord.setFont(new Font("Serif", Font.BOLD, 20));
		contentPane.add(lblAddNewWord, "cell 0 0");

		JLabel lblSecond = new JLabel("Word in " + conf.GET_CONFIG("second_lang"));
		lblSecond.setFont(new Font("Serif", Font.BOLD, 13));
		lblSecond.setForeground(Color.LIGHT_GRAY);
		contentPane.add(lblSecond, "cell 0 1");

		txtSecond = new JTextField();
		txtSecond.setForeground(Color.LIGHT_GRAY);
		txtSecond.setBackground(Color.DARK_GRAY);
		txtSecond.setFont(new Font("Serif", Font.BOLD, 12));
		contentPane.add(txtSecond, "cell 0 2,growx");
		txtSecond.setColumns(10);

		JLabel lblNative = new JLabel("Word in " + conf.GET_CONFIG("native_lang"));
		lblNative.setForeground(Color.LIGHT_GRAY);
		lblNative.setFont(new Font("Serif", Font.BOLD, 13));
		contentPane.add(lblNative, "cell 0 3");

		txtNative = new JTextField();
		txtNative.setBackground(Color.DARK_GRAY);
		txtNative.setForeground(Color.LIGHT_GRAY);
		txtNative.setFont(new Font("Serif", Font.BOLD, 12));
		txtNative.setColumns(10);
		contentPane.add(txtNative, "cell 0 4,growx");

		JLabel lblDescr = new JLabel("Description");
		lblDescr.setForeground(Color.LIGHT_GRAY);
		lblDescr.setFont(new Font("Serif", Font.BOLD, 13));
		contentPane.add(lblDescr, "cell 0 5");

		txtDescr = new JTextField("No");
		txtDescr.setForeground(Color.LIGHT_GRAY);
		txtDescr.setBackground(Color.DARK_GRAY);
		txtDescr.setFont(new Font("Serif", Font.BOLD, 12));
		txtDescr.setColumns(10);
		contentPane.add(txtDescr, "cell 0 6,growx");

		JButton btnNewButton = new JButton("Submit");
		btnNewButton.setForeground(Color.LIGHT_GRAY);
		btnNewButton.setFont(new Font("Serif", Font.BOLD, 12));
		btnNewButton.setBackground(Color.DARK_GRAY);
		btnNewButton.addActionListener(e -> {

			String nativeW = txtNative.getText();
			String secondW = txtSecond.getText();
			String description = txtDescr.getText();

			if (WordChecker.IS_EVERYTHING_OKAY(nativeW, 1) && WordChecker.IS_EVERYTHING_OKAY(secondW, 1)
					&& WordChecker.IS_EVERYTHING_OKAY(description, 2)) {
				CRUD = new Crud(connection.GET_MY_DATA_SOURCE());
				if (CRUD.ADD_DATA(nativeW, secondW, description)) {
					JOptionPane.showMessageDialog(null, "Word successfuly added to the DB!", "Success!",
							JOptionPane.PLAIN_MESSAGE);
					dispose();
				}
				else
					JOptionPane.showMessageDialog(null,
							"There are some errors in MySQL, be sure that everything is okay with DB. For more information, please check the 'mywrods.log' file!",
							"SQL Query Error", JOptionPane.ERROR_MESSAGE);
			} else
				JOptionPane.showMessageDialog(null, "Please, be sure that word length can't be longer than 50!",
						"Word checker", JOptionPane.ERROR_MESSAGE);

		});
		btnNewButton.setIcon(new ImageIcon(InsertGUI.class.getResource("/icons/add_32.png")));
		contentPane.add(btnNewButton, "flowx,cell 0 7");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setForeground(Color.LIGHT_GRAY);
		btnCancel.setFont(new Font("Serif", Font.BOLD, 12));
		btnCancel.setBackground(Color.DARK_GRAY);
		btnCancel.addActionListener(e -> dispose());
		btnCancel.setIcon(new ImageIcon(InsertGUI.class.getResource("/icons/cancel_32.png")));
		contentPane.add(btnCancel, "cell 0 7");
	}

}
