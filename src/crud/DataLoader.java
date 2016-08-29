package crud;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import core.Config;

public class DataLoader {

	private Logger logger = Logger.getLogger(Config.class.getName());
	private Config config = new Config();
	private MysqlDataSource datasource = null;
	private DefaultTableModel table_model = null;

	public DataLoader(MysqlDataSource DS) {
		this.datasource = DS;
	}

	public DefaultTableModel GET_DEFAULT_TABLE_MODEL(String word) {

		table_model = new DefaultTableModel() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};

		table_model.addColumn("ID");
		table_model.addColumn(config.GET_CONFIG("native_lang"));
		table_model.addColumn(config.GET_CONFIG("second_lang"));
		table_model.addColumn("Description");
		table_model.addColumn("Added");

		try {
			Connection con = datasource.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM words WHERE second LIKE '%" + word + "%' AND native LIKE '%"
					+ word + "%' ORDER BY wordID DESC");

			int count = rs.getMetaData().getColumnCount();
			Object[] row = new Object[count];

			while (rs.next()) {
				row[0] = rs.getString("wordID");
				row[1] = rs.getString("native");
				row[2] = rs.getString("second");
				row[3] = rs.getString("description");
				row[4] = rs.getDate("added");

				table_model.addRow(row);
			}

			con.close();
			stmt.close();
			rs.close();

			return table_model;
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}

	}

}
