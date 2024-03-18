package net.sparkminds.librarymanagement.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelToDatabase {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/db_library";

    private static final String USERNAME = "root";

    private static final String PASSWORD = "Donguyendat@99";

    public void readExcelFile(String filePath, int sheetNum, String tableName) throws IOException {
        try (
                FileInputStream file = new FileInputStream(filePath);
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)
        ) {
            XSSFSheet sheet = workbook.getSheetAt(sheetNum);
            Iterator<Row> rowIterator = sheet.iterator();

            // Read the header (first row)
            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            // Read data rows
            while (rowIterator.hasNext()) {
                Row dataRow = rowIterator.next();
                String insertQuery = buildSelectQuery(tableName, headers);
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                for (int i = dataRow.getFirstCellNum() + 1; i < dataRow.getLastCellNum(); i++) {
                    Cell cell = dataRow.getCell(i);
                    if (cell != null && i < headers.size()) {
                        setValueTypeForCell(cell, i, preparedStatement);
                    }
                }

                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String buildSelectQuery(String tableName, List<String> headers) {
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO ");
        queryBuilder.append(tableName).append(" (");

        // Add field
        for (int i = 1; i < headers.size(); ++i) {
            queryBuilder.append(headers.get(i)).append(", ");
        }

        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());        // Remove the last comma and space
        queryBuilder.append(") VALUES (");

        // Add value
        for (int i = 1; i < headers.size(); ++i) {
            queryBuilder.append("?, ");
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());        // Remove the last comma and space
        queryBuilder.append(");");

        return queryBuilder.toString();
    }

    private static void setValueTypeForCell(Cell cell, int colIdx, PreparedStatement statement) throws SQLException {
        switch (cell.getCellType()) {
            case STRING:
                statement.setString(colIdx, cell.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    statement.setDate(colIdx, new java.sql.Date(cell.getDateCellValue().getTime()));
                } else {
                    statement.setDouble(colIdx, cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                statement.setBoolean(colIdx, cell.getBooleanCellValue());
                break;
            case BLANK:
                statement.setString(colIdx, "");
                break;
            default:
                statement.setString(colIdx, null);
        }
    }
}
