package rentcar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Stack;

public class DBManager_CP extends DBManager {
	
	// Static block
    static {
    	try {
			instance = new DBManager_CP();
		} catch (SQLException e) {
			throw new RuntimeException("Connection pool init failed", e);
		}
    }
	
	// Field
	private static DBManager_CP instance;
    private static Stack<Connection> pool = new Stack<>();
    private static final int MAX = 10;
    	
	// Constructor
	private DBManager_CP() throws SQLException {
		for (int i=0; i<MAX; ++i) {
			pool.push(super.getConnection());
		}
		System.out.println("Connection pool created.");
	}
	
	// Method
	public static DBManager_CP getInstance() {
		return instance;
	}
	
	@Override
	public Connection getConnection() {
        if (!pool.isEmpty()) {
        	return pool.pop();
        }
        throw new RuntimeException("No available connections");
    }
	
	@Override
	public synchronized void returnConnection(Connection conn) {
		if (pool.size() < MAX) {
			pool.push(conn);
		} else {
			super.returnConnection(conn);
		}
	}
	
    
//	public void connectDB() {
//		if (pool.size() == 0) {
//			System.out.println("Connection Pool is Empty.");
//			return;
//		}
//		
//		try {
//			// JDBC Driver 등록
//			Class.forName("oracle.jdbc.OracleDriver");
//			
//			// 연결하기
//			var conn = getConnection();
//			
//			System.out.println("Connection Success.");
//			
//		} catch (Exception e) {
//			System.out.println("Connection Fail.");
//			e.printStackTrace();
//		}
//		
//	}
	
}
