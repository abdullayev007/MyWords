package crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Crud {

	private MysqlDataSource datasource = null;
	private Date date = null;
	private Logger logger = Logger.getLogger(Crud.class.getName());

	public Crud(MysqlDataSource datasource) {
		this.datasource = datasource;
		this.date = new Date();
	}

	public boolean ADD_DATA(String NATIVE, String SECOND, String DESCRIPTION) {
		try {
			Connection con = datasource.getConnection();
			PreparedStatement pstmt = con
					.prepareStatement("INSERT INTO words (native, second, description, added) VALUES(?, ?, ?, ?)");
			pstmt.setString(1, NATIVE);
			pstmt.setString(2, SECOND);
			pstmt.setString(3, DESCRIPTION);
			pstmt.setDate(4, new java.sql.Date(date.getTime()));

			pstmt.executeUpdate();
			return true;

		} catch (SQLException e) {
			logger.error(e);
			return false;
		}
	}

	public boolean RUN_SQL_QUERY(ArrayList<String> QUERIES) {
		try {
			Connection con = datasource.getConnection();
			Statement stmt = con.createStatement();

			for (int i = 0; i < QUERIES.size(); i++) {
				stmt.addBatch(QUERIES.get(i));
				;
			}

			stmt.executeBatch();

			return true;

		} catch (SQLException e) {
			logger.error(e);
			return false;
		}
	}

}
