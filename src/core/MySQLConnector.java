package core;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class MySQLConnector {

	private MysqlDataSource datasource = null;
	private String url = null;
	private String user = null;
	private String pass = null;

	public MySQLConnector() {
		url = "jdbc:mysql://localhost:3306/mywords";
		user = "root";
		pass = "root";

		datasource = new MysqlDataSource();
	}

	public MysqlDataSource GET_MY_DATA_SOURCE() {

		datasource.setURL(url);
		datasource.setUser(user);
		datasource.setPassword(pass);

		return datasource;
	}

}
