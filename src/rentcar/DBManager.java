package rentcar;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBManager {
	
	private static DBManager dbManager = new DBManager();
	private static Connection conn = null;
	
	private DBManager() {
		
	}
	
	public static DBManager getInstance() {
		return dbManager;
	}
	
	public static void connectDB() {
		if (conn != null) {
			System.out.println("Already Connected.");
			return;
		}
		
		try {
			// JDBC Driver 등록
			Class.forName("oracle.jdbc.OracleDriver");
			
			// 연결하기
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521/xe",
					"testuser",
					"test1234"
					);
			
			System.out.println("Connection Success.");
			
		} catch (Exception e) {
			System.out.println("Connection Fail.");
			e.printStackTrace();
		}
//		finally {
//			if (conn != null) {
//				try {
//					// 연결 끊기
//					conn.close();
//					System.out.println("End Connection.");
//				} catch (SQLException e) {}
//			}
//		}
		
	}
	
	public static PreparedStatement prepareStatement(String sql) throws SQLException {
		if (conn == null) connectDB();
		System.out.println(conn);
		return conn.prepareStatement(sql);
	}
	
	public static void close() throws SQLException {
		if (conn == null) connectDB();
		conn.close();
	}
	
	public static DatabaseMetaData getMetaData() throws SQLException {
		if (conn == null) connectDB();
		return conn.getMetaData();
	}
	
	
}
