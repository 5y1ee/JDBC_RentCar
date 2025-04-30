//package rentcar;
//
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.sql.DatabaseMetaData;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Random;
//
//public class DataMaker {
//    private static final Random rand = new Random();
//    
//	public static void main(String[] args) {
//		
//        insertDummyData("Users", 10);
////        insertDummyData("Cars", 10);
////        insertDummyData("Payments", 10);
//        insertDummyData("Branches", 10);
//		
//		return;
//	}
//	
//    public static void insertDummyData(String tableName, int count) {
//        String projectPath = System.getProperty("user.dir");
//        String filePath = projectPath + "/output/" + tableName + ".txt";
//        
//        try {
//        	var conn = DBManager.getInstance().getConnection();
//            DatabaseMetaData meta = conn.getMetaData();
//            
//            ResultSet columns = meta.getColumns(null, null, tableName.toUpperCase(), null);
//            List<String> columnNames = new ArrayList<>();
//            List<String> columnTypes = new ArrayList<>();
//
//            while (columns.next()) {
//                columnNames.add(columns.getString("COLUMN_NAME"));
//                columnTypes.add(columns.getString("TYPE_NAME"));
//            }
//
//            String placeholders = String.join(", ", Collections.nCopies(columnNames.size(), "?"));
//            String columnList = String.join(", ", columnNames);
//            String sql = "INSERT INTO " + tableName + " (" + columnList + ") VALUES (" + placeholders + ")";
//
//            StringBuilder queries = new StringBuilder();
//            
//            for (int i = 0; i < count; i++) {
//                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//                    String tempSql = sql;
//                    for (int j = 0; j < columnNames.size(); j++) {
//                        Object value = generateDummyValue(columnTypes.get(j), columnNames.get(j));
//                        pstmt.setObject(j + 1, value);
//                        String strVal = (value == null) ? "NULL" :
//                            (value instanceof String || value instanceof java.sql.Date) ? "'" + value.toString() + "'" :
//                            value.toString();
//
//                        tempSql = tempSql.replaceFirst("\\?", strVal);  // 새 문자열에 누적 대입
//                    }
//                    System.out.println("SQL: " + tempSql);
//                    queries.append(tempSql+";\n");
//                    pstmt.executeUpdate();
//                }
//            }
//
//            System.out.println("Inserted " + count + " rows into " + tableName);
//            
//            Files.write(Paths.get(filePath), queries.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//	
//    private static Object generateDummyValue(String typeName, String columnName) {
//        switch (typeName.toUpperCase()) {
//            case "VARCHAR2":
//            case "CHAR":
//                return columnName + rand.nextInt(1000);
//            case "NUMBER":
//                return rand.nextInt(1000);
//            case "DATE":
//                return new java.sql.Date(System.currentTimeMillis());
//            default:
//                return null;
//        }
//    }
//    
//    
//    void tempFunc() {
//		/*
//		DBManager db = DBManager.getInstance();
//		String user = "TESTUSER";
//		
//		try {
//			var metaData = db.getMetaData();
//			
//			// 테이블 복록 조회
//			ResultSet tables = metaData.getTables(null, user, "%", new String[] { "TABLE" });
//            while (tables.next()) {
//                String tableName = tables.getString("TABLE_NAME");
//                System.out.println("📄 Table: " + tableName);
//
//                // 각 테이블의 컬럼 정보 조회
//                ResultSet columns = metaData.getColumns(null, user, tableName, "%");
//                while (columns.next()) {
//                    String columnName = columns.getString("COLUMN_NAME");
//                    String typeName = columns.getString("TYPE_NAME");
//                    int size = columns.getInt("COLUMN_SIZE");
//                    System.out.println("   🔸 Column: " + columnName + " (" + typeName + ", size=" + size + ")");
//                }
//                System.out.println();
//
//            }	
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		
//		*/
//    }
//    
//
//
//}


package rentcar;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class DataMaker {

    private static final Random rand = new Random();
    private static final int dVal = 20;

    // 캐시된 ID 리스트들
    private static List<Integer> cachedUserIds = new ArrayList<>();
    private static List<Integer> cachedPaymentIds = new ArrayList<>();
    private static List<Integer> cachedBranchIds = new ArrayList<>();
    private static List<Integer> cachedCarIds = new ArrayList<>();
    
    // Supplier interface
    private static final Map<String, Supplier<Object>> valueGenerator = Map.ofEntries(
    	    Map.entry("users.email", () -> makeEmail()),
    	    Map.entry("users.password", () -> List.of("M", "F").get(rand.nextInt(2))),
    	    Map.entry("users.age", () -> dVal + rand.nextInt(dVal*3)),
    	    Map.entry("users.score", () -> dVal + rand.nextInt(-1*dVal,dVal)),

    	    Map.entry("payments.name", () -> List.of("Y", "N").get(rand.nextInt(2)))
    	    // 대충 뒤에 알아서 추가
    	);
    
    private static final String makeEmail() {
    	String chars = "abcdefghijklmnopqrstuvwxyz";
        int len = 5 + rand.nextInt(6); // 5~10글자
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }

        List<String> domains = List.of("@gmail.com", "@naver.com", "@kakao.com", "@daum.net");
        String domain = domains.get(rand.nextInt(domains.size()));

        return sb.toString() + domain;
    }
    
    

    public static void main(String[] args) {
        insertDummyData("Users", 10);
        insertDummyData("Payments", 10);
//        insertDummyData("Branches", 5);
//        insertDummyData("Cars", 15);
//        insertDummyData("UsersCars", 20);
    }

    public static void insertDummyData(String tableName, int count) {
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath + "/output/" + tableName + ".txt";

        try {
            var conn = DBManager.getInstance().getConnection();
            DatabaseMetaData meta = conn.getMetaData();

            ResultSet columns = meta.getColumns(null, null, tableName.toUpperCase(), null);
            List<String> columnNames = new ArrayList<>();
            List<String> columnTypes = new ArrayList<>();

            while (columns.next()) {
                columnNames.add(columns.getString("COLUMN_NAME"));
                columnTypes.add(columns.getString("TYPE_NAME"));
            }

            String placeholders = String.join(", ", Collections.nCopies(columnNames.size(), "?"));
            String columnList = String.join(", ", columnNames);
            String sql = "INSERT INTO " + tableName + " (" + columnList + ") VALUES (" + placeholders + ")";

            StringBuilder queries = new StringBuilder();

            for (int i = 0; i < count; i++) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    String tempSql = sql;
                    for (int j = 0; j < columnNames.size(); j++) {
                        Object value = generateDummyValue(columnTypes.get(j), columnNames.get(j), tableName);
                        pstmt.setObject(j + 1, value);

                        String strVal = (value == null) ? "NULL" :
                            (value instanceof String || value instanceof java.sql.Date) ? "'" + value.toString() + "'" :
                            value.toString();
                        tempSql = tempSql.replaceFirst("\\?", strVal);
                    }
                    System.out.println("##SQL: " + tempSql);
                    queries.append(tempSql).append(";\n");
                    pstmt.executeUpdate();
                }
            }

            System.out.println("Inserted " + count + " rows into " + tableName);

            // 결과 파일 덮어쓰기
            Files.write(Paths.get(filePath), queries.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object generateDummyValue(String typeName, String columnName, String tableName) throws Exception {
//    	System.out.println("columnName : " + columnName + ", tableName : " + tableName);
        switch (columnName.toLowerCase()) {
            case "userid":
            	if ("users".equals(tableName.toLowerCase())) break;
                return getRandomIdFrom("Users", "userId", cachedUserIds);
            case "paymentsid":
            	if ("payments".equals(tableName.toLowerCase())) break;
                return getRandomIdFrom("Payments", "paymentsId", cachedPaymentIds);
            case "branchid":
            	if ("branches".equals(tableName.toLowerCase())) break;
                return getRandomIdFrom("Branches", "branchId", cachedBranchIds);
            case "carid":
            	if ("cars".equals(tableName.toLowerCase())) break;
                return getRandomIdFrom("Cars", "carId", cachedCarIds);
        }
        
        String key = tableName.toLowerCase() + "." + columnName.toLowerCase();
        System.out.println(key);
        if (valueGenerator.containsKey(key)) {
        	return valueGenerator.get(key).get();
        }
    	
        switch (typeName.toUpperCase()) {
            case "VARCHAR2":
            case "CHAR":
                return columnName + rand.nextInt(1000);
            case "NUMBER":
                return rand.nextInt(1000);
            case "DATE":
                return new java.sql.Date(System.currentTimeMillis());
            default:
                return null;
        }
    }

    private static int getRandomIdFrom(String tableName, String idColumn, List<Integer> cache) throws Exception {
    	
    	System.out.println(tableName + ", " + idColumn + ", " + cache.toString());
    	
        if (!cache.isEmpty()) {
            return cache.get(rand.nextInt(cache.size()));
        }

        var conn = DBManager.getInstance().getConnection();
        var stmt = conn.createStatement();
        var rs = stmt.executeQuery("SELECT " + idColumn + " FROM " + tableName);
        System.out.println("SELECT " + idColumn + " FROM " + tableName);
        while (rs.next()) {
        	System.out.println(rs.getInt(1));
            cache.add(rs.getInt(1));
        }
        rs.close();
        stmt.close();

        if (cache.isEmpty()) {
            throw new RuntimeException("No data in table: " + tableName);
        }

        return cache.get(rand.nextInt(cache.size()));
    }
}
