package com.lucky.data_visual.server;

import com.lucky.data_visual.interfaces.FileProcessor;
import com.lucky.data_visual.model.JsonResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class ExcelFileServer implements FileProcessor {
    private final List<String> sheetNames = new ArrayList<>();

    @Override
    public Map<String, List<Map<String, String>>> readFile(MultipartFile file) throws IOException {
        List<Map<String, String>> resultList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            sheetNames.clear();
            // 获取所有工作表
            int sheetCount = workbook.getNumberOfSheets();

            // 遍历每个工作表
            for (int i = 0; i < sheetCount; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName(); // 获取工作表名称
                sheetNames.add(sheetName);
                List<Map<String, String>> sheetData = parseSheet(sheet, sheetName);
                resultList.addAll(sheetData);
            }
        }

        return splitExcelToTables(resultList); // 将工作表根据表名拆分成多个表格
    }

    // 解析一个工作表
    private List<Map<String, String>> parseSheet(Sheet sheet, String sheetName) {
        List<Map<String, String>> sheetData = new ArrayList<>();
        Row headerRow = sheet.getRow(0);  // 假设第一行是标题行

        // 获取标题行的单元格
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(cell.getStringCellValue());
        }

        // 遍历工作表的每一行，跳过标题行
        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 使用getLastRowNum()
            Row row = sheet.getRow(i);
            if (row == null) continue;  // 跳过空行
            Map<String, String> rowData = new LinkedHashMap<>();

            // 添加一个字段，区分不同表格
            rowData.put("sheetName", sheetName);

            // 遍历每一列并获取数据
            for (int j = 0; j < headerRow.getPhysicalNumberOfCells(); j++) { // 读取每一列
                Cell cell = row.getCell(j);
                String cellValue = getCellValue(cell);
                rowData.put(headers.get(j), cellValue);
            }

            sheetData.add(rowData);
        }

        return sheetData;
    }

    // 获取单元格内容的通用方法，空单元格返回空字符串
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return ""; // 如果单元格为空，返回空字符串
        }

        return switch (cell.getCellType()) {
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                yield String.valueOf(cell.getNumericCellValue());
            }
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> ""; // 其他类型返回空字符串
        };
    }

    // 将统一的表格根据表名分离成多个
    private Map<String, List<Map<String, String>>> splitExcelToTables(List<Map<String, String>> result) {
        List<Map<String, String>> excelData = new ArrayList<>(result);
        Map<String, List<Map<String, String>>> tablesMap = new HashMap<>(); // 存储各表的数据
        for (Map<String, String> row : excelData) {
            String sheetName = row.get("sheetName");
            tablesMap.putIfAbsent(sheetName, new ArrayList<>());
            row.remove("sheetName"); // 删除键值对中的列名键
            tablesMap.get(sheetName).add(row);
        }
        return tablesMap;
    }

    @Override
    public void initData(JsonResult<List<Map<String, Object>>> originalData, JsonResult<List<Map<String, Object>>> operationalData) {
        // 直接调用 CsvFileServer 中的initData方法
    }

    @Override
    public String getFileType() {
        return "Excel";
    }

    // Getter
    public List<String> getSheetNames() {
        return sheetNames;
    }
}
