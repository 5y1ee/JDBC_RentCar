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
import java.util.Random;

public class DataMaker {
    private static final Random rand = new Random();

    
	public static void main(String[] args) {
		
        insertDummyData("Users", 10);  // Users 테이블에 10개 더미 삽입
        
		
		return;
		
		
		/*
		DBManager db = DBManager.getInstance();
		String user = "TESTUSER";
		
		try {
			var metaData = db.getMetaData();
			
			// 테이블 복록 조회
			ResultSet tables = metaData.getTables(null, user, "%", new String[] { "TABLE" });
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("📄 Table: " + tableName);

                // 각 테이블의 컬럼 정보 조회
                ResultSet columns = metaData.getColumns(null, user, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String typeName = columns.getString("TYPE_NAME");
                    int size = columns.getInt("COLUMN_SIZE");
                    System.out.println("   🔸 Column: " + columnName + " (" + typeName + ", size=" + size + ")");
                }
                System.out.println();

            }	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		*/
		
		
	}
	


    
    public static void insertDummyData(String tableName, int count) {
        try {
            DatabaseMetaData meta = DBManager.getMetaData();

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
                try (PreparedStatement pstmt = DBManager.prepareStatement(sql)) {
                    String tempSql = sql;
                    for (int j = 0; j < columnNames.size(); j++) {
                        Object value = generateDummyValue(columnTypes.get(j));
                        pstmt.setObject(j + 1, value);
                        String strVal = (value == null) ? "NULL" :
                            (value instanceof String || value instanceof java.sql.Date) ? "'" + value.toString() + "'" :
                            value.toString();

                        tempSql = tempSql.replaceFirst("\\?", strVal);  // 새 문자열에 누적 대입
                    }
                    System.out.println("SQL: " + tempSql);
                    queries.append(tempSql+";\n");
                    pstmt.executeUpdate();
                }
            }

            System.out.println("Inserted " + count + " rows into " + tableName);
            
            Files.write(Paths.get("D:\\query-log.txt"), queries.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    private static Object generateDummyValue(String typeName) {
        switch (typeName.toUpperCase()) {
            case "VARCHAR2":
            case "CHAR":
                return "str" + rand.nextInt(1000);
            case "NUMBER":
                return rand.nextInt(1000);
            case "DATE":
                return new java.sql.Date(System.currentTimeMillis());
            default:
                return null;
        }
    }
    


}
