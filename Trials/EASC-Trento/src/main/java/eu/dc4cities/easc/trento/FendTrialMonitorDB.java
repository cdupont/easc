package eu.dc4cities.easc.trento;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FendTrialMonitorDB {	
	private static FendTrialMonitorDB instance = null;
	private Connection connection;
	
	private static final String TABLE_EXAM="exams";	
	private static final String COLUMN_BETIME="bendEnd";
	private static final String COLUMN_BDONE="bendDone";
	private static final String COLUMN_ID="id";
	private static final String COLUMN_BSTIME="bendStart";
	private static final String COLUMN_FSTIME="fendStart";
	private static final String COLUMN_FDONE="fendDone";
	private static final String COLUMN_RQSTDATE="rqstdate";
	private static final String TABLE_DWRQST="dwrqst";
	
	private FendTrialMonitorDB () {
		this.connection = getConnection();
	}	

	public static synchronized FendTrialMonitorDB getInstance() {
		if (instance == null) {
			instance = new FendTrialMonitorDB();
		}
		return instance;
	}
	
	public int getFendRqsts(){
		int done=0;
		connection = getConnection();		
		try {
			Statement statement = connection.createStatement();
			String selectQuery = "SELECT count(*) AS count FROM "+TABLE_EXAM+" WHERE DATE("+COLUMN_FSTIME+") = CURRENT_DATE";			
			ResultSet resultSet = statement.executeQuery(selectQuery);			
			if (resultSet.next()) {				
				done = resultSet.getInt("count");
			}

			selectQuery = "SELECT count(*) AS count FROM "+TABLE_DWRQST+" WHERE DATE("+COLUMN_RQSTDATE+") = CURRENT_DATE";			
			resultSet = statement.executeQuery(selectQuery);			
			if (resultSet.next()) {				
				done += resultSet.getInt("count");
			}

			statement.close();
			resultSet.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}						
		return done;
	}
	
	public int getFendRqsts(long now, long interval){		
		int  done = 0;
		connection = getConnection();		
		try {
			Statement statement = connection.createStatement();
			String selectQuery = "SELECT count(UNIX_TIMESTAMP("+COLUMN_FSTIME+")) AS count FROM "+TABLE_EXAM+" "
					+ "WHERE "
					+ "UNIX_TIMESTAMP("+COLUMN_FSTIME+") >= "+(now-interval)+" "							
					+ "AND UNIX_TIMESTAMP("+COLUMN_FSTIME+") <= "+now+";";
			ResultSet resultSet = statement.executeQuery(selectQuery);
			if (resultSet.next()) {				
				done = resultSet.getInt("count");
			}

			selectQuery = "SELECT count(UNIX_TIMESTAMP("+COLUMN_RQSTDATE+")) AS count FROM "+TABLE_DWRQST+" "
					+ "WHERE "
					+ "UNIX_TIMESTAMP("+COLUMN_RQSTDATE+") >= "+(now-interval)+" "
					+ "AND UNIX_TIMESTAMP("+COLUMN_RQSTDATE+") <= "+now+";";
			resultSet = statement.executeQuery(selectQuery);
			if (resultSet.next()) {
				done += resultSet.getInt("count");
			}

			statement.close();
			resultSet.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}			
		return done;
	}
	
	private Connection getConnection() {
		final String DB_HOST = "10.0.0.7";
		final String DB_PORT = "3306";
//		final String DB_HOST = "127.0.0.1";
//		final String DB_PORT = "8889";
		final String DB_NAME = "d4c";
		final String DB_USER = "root";
		final String DB_PWD = "root";
		try {
			if( (connection == null) || (!connection.isValid(1)) ) {
			Class.forName("com.mysql.jdbc.Driver");				
				connection = DriverManager.getConnection("jdbc:mysql://"+
						DB_HOST +
						":" + DB_PORT +
						"/" + DB_NAME +
						"?user=" + DB_USER +
						"&password=" + DB_PWD
						);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();             
		} catch (SQLException e) {
			e.printStackTrace();
		}
	return connection;
	}
}
