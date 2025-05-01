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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class DataMaker {

    private static final Random rand = new Random(1234L);
    private static final int dVal = 20;

    // 캐시된 ID 리스트들
    private static List<Integer> cachedUserIds = new ArrayList<>();
    private static List<Integer> cachedPaymentIds = new ArrayList<>();
    private static List<Integer> cachedBranchIds = new ArrayList<>();
    private static List<Integer> cachedCarIds = new ArrayList<>();
    
    // PK용 인덱스
    private static int userId = 0;
    private static int paymentsId = 0;
    private static int carsId = 0;
    private static int branchId = 0;
    private static int userscarsId = 0;
    
    // Supplier interface
    private static final Map<String, Supplier<Object>> valueGenerator = Map.ofEntries(
    	    Map.entry("users.email", () -> makeEmail()),
    	    Map.entry("users.password", () -> List.of("M", "F").get(rand.nextInt(2))),
    	    Map.entry("users.age", () -> dVal + rand.nextInt(dVal*3)),
    	    Map.entry("users.score", () -> dVal + rand.nextInt(-1*dVal,dVal)),

    	    Map.entry("payments.name", () -> List.of("CreditCard", "Cash").get(rand.nextInt(2))),

    	    Map.entry("cars.model", () -> List.of("Avante", "Grandeur", "Genesis GV80", "Tesla Model Y").get(rand.nextInt(4))),
    	    Map.entry("cars.age", () -> rand.nextInt(1, 15)),
    	    Map.entry("cars.carnum", () -> makeCarNum()),
    	    
    	    Map.entry("branches.phone", () -> makePhone()),
    	    
    	    Map.entry("userscars.status", () -> List.of("예약완료", "대여중", "반납완료").get(rand.nextInt(3)))
    	    
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
    
    private static final String makeCarNum() {
    	String chars = "가나다라마바사아자차카타파하";

    	StringBuilder sb = new StringBuilder();
    	sb.append(String.valueOf(rand.nextInt(1,100)))
    		.append(chars.charAt(rand.nextInt(chars.length())))
    		.append(String.valueOf(rand.nextInt(9999)));
    	
    	return sb.toString();
    }
    
    private static final String makePhone() {
        StringBuilder sb = new StringBuilder();
        sb.append("010-")
          .append(String.format("%04d", rand.nextInt(10000)))
          .append("-")
          .append(String.format("%04d", rand.nextInt(10000)));
        return sb.toString();
    }
    
    

    public static void main(String[] args) {
        insertDummyData("Users", 10);
        insertDummyData("Payments", 10);
        insertDummyData("Branches", 5);
        insertDummyData("Cars", 15);
//        insertDummyData("UsersCars", 20);
        
        insertDummyReservation(10);
        
    }
    
    private static String getReservStatus(LocalDate startDate, int rentDays) {
    	LocalDate now = LocalDate.now();
        LocalDate endDate = startDate.plusDays(rentDays);
        
        if (!now.isBefore(startDate) && !now.isAfter(endDate)) {
            return "대여중";
        } else if (now.isAfter(endDate)) {
            return "반납완료";
        } else if (now.isBefore(startDate)) {
            return "예약완료";
        }
    	
		return "Unknown";
    }
    
    private static LocalDate getRandomDate(LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end);
        long randomDays = rand.nextLong(days + 1);
        return start.plusDays(randomDays);
    }
    public static void insertDummyReservation(int count) {
    	// UsersCars
    	// 날짜 구간을 n개 뽑음 -> 가격, 상태, 주문일시는 여기 종속됨
    	// 해당 날짜에서 대여 가능한 차량 목록 조회
    	// 사용자 및 결제는 아무거나
    	
        StringBuilder queries = new StringBuilder();
		String tableName = "userscars";

    	for (int i=0; i<count; ++i) {
            LocalDate startBound = LocalDate.of(2025, 4, 1); // 범위 시작
            LocalDate endBound = LocalDate.of(2025, 5, 30); // 범위 끝

            LocalDate startDate = getRandomDate(startBound, endBound);
            int rentDays = rand.nextInt(14);	// 14일로 최대 대여 기간 하드코딩
            LocalDate endDate = startDate.plusDays(rentDays);
            int reservDayBefore = rand.nextInt(10);
            LocalDate reservDate = startDate.minusDays(reservDayBefore);
            
            System.out.println(reservDate + " " + startDate + " " + endDate);
            
            int price = rentDays * 20000; // 하루당 렌트비 20000, 하드코딩
            String status = getReservStatus(startDate, rentDays);
        	
        	try {
        		var conn = DBManager.getInstance().getConnection();
        		
//        		String sql = """
//						SELECT carid
//						FROM userscars
//						WHERE enddate < ?
//						   OR startdate > ?
//						group by carid
//        				""";
        		String sql = """
        			    SELECT carid
        			    FROM userscars
        			    WHERE NOT (enddate < ? OR startdate > ?)
        			    GROUP BY carid
        			""";
        		// 겹치는 차 id -> 예약못하는 차
        		
        		PreparedStatement pstmt = conn.prepareStatement(sql);
        		pstmt.setDate(1, Date.valueOf(startDate));
        		pstmt.setDate(2, Date.valueOf(endDate));
//        		pstmt.setString(1, startDate.toString());
//        		pstmt.setString(2, endDate.toString());
        		
        		String carsql = """
        				select carid
        				from cars
        				""";
        		PreparedStatement carpstmt = conn.prepareStatement(carsql);
        		ResultSet carrs = carpstmt.executeQuery();
        		List<Integer> carIds = new ArrayList<>();

        		while (carrs.next()) {
        		    carIds.add(carrs.getInt("carid"));
        		}
        		
        		carrs.close();
        		carpstmt.close();
        		
        		ResultSet rs = pstmt.executeQuery();
        		while (rs.next()) {
        			int carId = rs.getInt("carid");
        			carIds.remove(Integer.valueOf(carId));

        			System.out.println("put carid " + carId);
        		}
        		
        		if (carIds.size() <= 0) {
        			--i;
        			continue;
        		}
        		
        		int id = carIds.get(rand.nextInt(carIds.size()));
        		
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
        		String insertSQL = "INSERT INTO userscars" + " (" + columnList + ") VALUES (" + placeholders + ")";
        		
        		System.out.println(insertSQL);
        		

                String tempSql = insertSQL;
                Map<String, Object> fixedColumnValues = Map.of(
                	    "carid", id,
                	    "startdate", startDate,
                	    "enddate", endDate,
                	    "price", price,
                	    "status", status,
                	    "createdat", reservDate
                	);
                

        		PreparedStatement pstmt2 = conn.prepareStatement(insertSQL);
                for (int j = 0; j < columnNames.size(); j++) {
                	Object value;
                	
                    String columnName = columnNames.get(j).toLowerCase();
//                    System.out.println(columnName + "     j: " + j);
                    if (fixedColumnValues.containsKey(columnName)) {
                        value = fixedColumnValues.get(columnName);
//                        System.out.println("####" + columnName + "   " + value + "    " + j);
                        pstmt2.setObject(j + 1, value);
                    }
                	else {
                        value = generateDummyValue(columnTypes.get(j), columnNames.get(j), tableName);
                        pstmt2.setObject(j + 1, value);
                	}

                    String strVal = (value == null) ? "NULL" :
                        (value instanceof String || value instanceof java.sql.Date) ? "'" + value.toString() + "'" :
                        value.toString();
                    tempSql = tempSql.replaceFirst("\\?", strVal);
//                    System.out.println("tempSql " + tempSql + "   j " + j);
                }
                System.out.println("##SQL: " + tempSql);
                queries.append(tempSql).append(";\n");
                pstmt2.executeUpdate();
        	}
        	 catch (Exception e) {
        		e.printStackTrace();
        	}
        	
    	}
        // 결과 파일 덮어쓰기
        try {
			Files.write(Paths.get(getFilePath(tableName)), queries.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}


    }
    
    public static String getFilePath(String tableName) {
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath + "/output/" + tableName + ".txt";
    	return filePath;
    }

    public static void insertDummyData(String tableName, int count) {

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
            Files.write(Paths.get(getFilePath(tableName)), queries.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object generateDummyValue(String typeName, String columnName, String tableName) throws Exception {
//    	System.out.println("columnName : " + columnName + ", tableName : " + tableName);
        switch (columnName.toLowerCase()) {
            case "userid":
            	if ("users".equals(tableName.toLowerCase())) return ++userId;
                return getRandomIdFrom("Users", "userId", cachedUserIds);
            case "paymentsid":
            	if ("payments".equals(tableName.toLowerCase())) return ++paymentsId;
                return getRandomIdFrom("Payments", "paymentsId", cachedPaymentIds);
            case "branchid":
            	if ("branches".equals(tableName.toLowerCase())) return ++branchId;
                return getRandomIdFrom("Branches", "branchId", cachedBranchIds);
            case "carid":
            	if ("cars".equals(tableName.toLowerCase())) return ++carsId;
                return getRandomIdFrom("Cars", "carId", cachedCarIds);
            case "ucno":
            	if ("userscars".equals(tableName.toLowerCase())) return ++userscarsId;
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
