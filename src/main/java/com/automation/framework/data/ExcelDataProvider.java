package com.automation.framework.data;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ExcelDataProvider {
    
    private static final Logger logger = LogManager.getLogger(ExcelDataProvider.class);
    private Workbook workbook;
    private Sheet sheet;
    private String filePath;
    
    public ExcelDataProvider(String filePath) {
        this.filePath = filePath;
        loadWorkbook();
    }
    
    private void loadWorkbook() {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filePath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("Unsupported file format. Use .xlsx or .xls");
            }
            logger.info("Excel workbook loaded successfully: " + filePath);
        } catch (IOException e) {
            logger.error("Failed to load Excel workbook: " + e.getMessage());
            throw new RuntimeException("Cannot load Excel file: " + filePath, e);
        }
    }
    
    public void selectSheet(String sheetName) {
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new RuntimeException("Sheet not found: " + sheetName);
        }
        logger.info("Selected sheet: " + sheetName);
    }
    
    public void selectSheet(int sheetIndex) {
        sheet = workbook.getSheetAt(sheetIndex);
        if (sheet == null) {
            throw new RuntimeException("Sheet not found at index: " + sheetIndex);
        }
        logger.info("Selected sheet at index: " + sheetIndex);
    }
    
    public String getCellData(int rowNum, int colNum) {
        try {
            Row row = sheet.getRow(rowNum);
            if (row == null) return "";
            
            Cell cell = row.getCell(colNum);
            if (cell == null) return "";
            
            return getCellValueAsString(cell);
        } catch (Exception e) {
            logger.error("Error reading cell data at row: " + rowNum + ", col: " + colNum);
            return "";
        }
    }
    
    public String getCellData(int rowNum, String columnName) {
        int colNum = getColumnIndex(columnName);
        return getCellData(rowNum, colNum);
    }
    
    private int getColumnIndex(String columnName) {
        Row headerRow = sheet.getRow(0);
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && getCellValueAsString(cell).equals(columnName)) {
                return i;
            }
        }
        throw new RuntimeException("Column not found: " + columnName);
    }
    
    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    public Object[][] getTestData(String sheetName) {
        selectSheet(sheetName);
        return getTestData();
    }
    
    public Object[][] getTestData() {
        if (sheet == null) {
            throw new RuntimeException("No sheet selected. Call selectSheet() first.");
        }
        
        int rowCount = sheet.getLastRowNum();
        int colCount = sheet.getRow(0).getLastCellNum();
        
        Object[][] data = new Object[rowCount][colCount];
        
        for (int i = 1; i <= rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                data[i-1][j] = getCellData(i, j);
            }
        }
        
        logger.info("Retrieved test data: " + rowCount + " rows, " + colCount + " columns");
        return data;
    }
    
    public Map<String, String> getRowDataAsMap(int rowNum) {
        Map<String, String> rowData = new HashMap<>();
        Row headerRow = sheet.getRow(0);
        Row dataRow = sheet.getRow(rowNum);
        
        if (dataRow != null) {
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String columnName = getCellValueAsString(headerRow.getCell(i));
                String cellValue = getCellData(rowNum, i);
                rowData.put(columnName, cellValue);
            }
        }
        
        return rowData;
    }
    
    public List<Map<String, String>> getAllDataAsMapList() {
        List<Map<String, String>> dataList = new ArrayList<>();
        int rowCount = sheet.getLastRowNum();
        
        for (int i = 1; i <= rowCount; i++) {
            dataList.add(getRowDataAsMap(i));
        }
        
        logger.info("Retrieved " + dataList.size() + " data rows as map list");
        return dataList;
    }
    
    public void setCellData(int rowNum, int colNum, String data) {
        try {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }
            
            Cell cell = row.getCell(colNum);
            if (cell == null) {
                cell = row.createCell(colNum);
            }
            
            cell.setCellValue(data);
            saveWorkbook();
            logger.info("Cell data updated at row: " + rowNum + ", col: " + colNum);
        } catch (Exception e) {
            logger.error("Error setting cell data: " + e.getMessage());
        }
    }
    
    private void saveWorkbook() {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        } catch (IOException e) {
            logger.error("Failed to save workbook: " + e.getMessage());
        }
    }
    
    public void closeWorkbook() {
        try {
            if (workbook != null) {
                workbook.close();
                logger.info("Excel workbook closed successfully");
            }
        } catch (IOException e) {
            logger.error("Error closing workbook: " + e.getMessage());
        }
    }
    
    public int getRowCount() {
        return sheet.getLastRowNum();
    }
    
    public int getColumnCount() {
        return sheet.getRow(0).getLastCellNum();
    }
}