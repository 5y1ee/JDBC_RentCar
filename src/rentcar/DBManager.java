package rentcar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
	
	// Const
	private static final String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";
	private static final String ORACLE_ADDRESS = "jdbc:oracle:thin:@localhost:1521/xe";
	private static final String ORACLE_USER = "testuser";
	private static final String ORACLE_PASSWORD = "test1234";

	// Field
//	protected Connection conn = null;
	protected static DBManager instance = new DBManager();
	
	// Constructor
	protected DBManager() {
		try {
			Class.forName(ORACLE_DRIVER);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Driver load failed", e);
		}
	}
	
	// Method
	public static DBManager getInstance() {
		return instance;
	}
	
	public Connection getConnection() {
		try {
			return DriverManager.getConnection(ORACLE_ADDRESS, ORACLE_USER, ORACLE_PASSWORD);
		} catch (SQLException e) {
			throw new RuntimeException("Connection failed", e);
		}
	}
	
	public void returnConnection(Connection conn) {
		try {
			if (conn != null && !conn.isClosed())
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
//	public Connection getConnection() {
//		connectDB();
//		return conn;
//	}
	
//	protected void connectDB() {
//		if (conn != null) {
//			System.out.println("Already Connected.");
//			return;
//		}
//		
//		try {
//			// JDBC Driver 등록
//			Class.forName(ORACLE_DRIVER);
//			
//			// 연결하기
//			conn = connect();
//			
//			System.out.println("Connection Success.");
//			
//		} catch (Exception e) {
//			System.out.println("Connection Fail.");
//			e.printStackTrace();
//		}
//		
//	}
	
//	protected Connection connect() throws SQLException {
//		return DriverManager.getConnection(
//				ORACLE_ADDRESS,
//				ORACLE_USER,
//				ORACLE_PASSWORD
//				);
//	}
	
}
