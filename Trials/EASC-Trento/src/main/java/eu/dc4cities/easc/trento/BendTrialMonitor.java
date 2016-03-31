package eu.dc4cities.easc.trento;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BendTrialMonitor {	
	private static BendTrialMonitor instance = null;
	private Connection connection;
	
	private static final String TABLE_EXAM="exams";	
	private static final String COLUMN_BETIME="bendEnd";
	private static final String COLUMN_BDONE="bendDone";
	private static final String COLUMN_ID="id";
	private static final String COLUMN_BSTIME="bendStart";
	private static final String COLUMN_FSTIME="fendStart";
	private static final String COLUMN_FDONE="fendDone";
	
	
	private BendTrialMonitor () {
		this.connection = getConnection();
	}	

	public static synchronized BendTrialMonitor getInstance() {
		if (instance == null) {
			instance = new BendTrialMonitor();
		}
		return instance;
	}
	
	public int getBendDone(){
		int done=0;
		connection = getConnection();		
		try {
			Statement statement = connection.createStatement();
			String selectQuery = "SELECT count(*) AS count FROM "+TABLE_EXAM+" WHERE "+COLUMN_BDONE+" AND DATE("+COLUMN_BETIME+") = CURRENT_DATE";			
			ResultSet resultSet = statement.executeQuery(selectQuery);			
			if (resultSet.next()) {				
				done = resultSet.getInt("count");
			}
			statement.close();
			resultSet.close();		
		} catch (SQLException e) {
			e.printStackTrace();
		}						
		return done;
	}
	
	public int getBendDone(long now, long interval){		
		int  done = 0;
		connection = getConnection();		
		try {
			Statement statement = connection.createStatement();
			String selectQuery = "SELECT count(UNIX_TIMESTAMP("+COLUMN_BETIME+")) AS count FROM "+TABLE_EXAM+" "
					+ "WHERE "+COLUMN_BDONE+" "
					+ "AND UNIX_TIMESTAMP("+COLUMN_BETIME+") >= "+(now-interval)+" "							
					+ "AND UNIX_TIMESTAMP("+COLUMN_BETIME+") <= "+now+";";
			ResultSet resultSet = statement.executeQuery(selectQuery);
			if (resultSet.next()) {				
				done = resultSet.getInt("count");
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

	public int getBendTodo() {
		int  todo = 0;
		connection = getConnection();
		try {
			Statement statement = connection.createStatement();			
			String selectQuery = "SELECT count("+COLUMN_ID+") as count FROM exams "
					+ "WHERE "+COLUMN_FDONE+" AND " 
					+ "DATE("+COLUMN_FSTIME+") = CURRENT_DATE";
			ResultSet resultSet = statement.executeQuery(selectQuery);
			if (resultSet.next()) {
				todo = resultSet.getInt("count");
			}
			statement.close();
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return todo;
	}
}
