package rentcar;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RentCar {

	public static void main(String[] args) {
		
		// DB 연결
		DBManager db = DBManager.getInstance();
		db.connectDB();
		
		// 데이터 저장
		/*
		 * INSERT INFO users (userid, username, userpassword, userage, useremail)
		 * VALUES ('winter', 한겨울', ...)
		 * VALUES (?,?,?,?,?)
		 */
		
		// 매개변수 insert문
		String insertSQL = new StringBuilder()
				.append("INSERT INTO Users (userId, email, password, age, temp) ")
				.append("VALUES (?,?,?,?,?)")
				.toString();
		
		String insertSQL2 = "INSERT INTO Users (userId, email, password, age, temp) VALUES (2,'qwer@naver.com','zxcv1234',26,37)";
		
		// PreparedStatement 얻기 및 값 지정
		try {
			PreparedStatement insert_pstmt = db.prepareStatement(insertSQL);
			insert_pstmt.setInt(1, 3);
			insert_pstmt.setString(2, "asdf@google.com");
			insert_pstmt.setString(3, "qwer1234");
			insert_pstmt.setInt(4, 25);
			insert_pstmt.setInt(5, 36);
			
			PreparedStatement insert_pstmt2 = db.prepareStatement(insertSQL2);
			
			int rows = insert_pstmt2.executeUpdate();
			
//			int rows = insert_pstmt.executeUpdate();
			System.out.println("저장된 행 수 : " + rows);
			
			// 닫기
			insert_pstmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				// 연결 끊기
				db.close();
			} catch (SQLException e) {}
			
		}
		
		
	}
	

}
