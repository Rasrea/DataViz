package com.lucky.data_visual.server;

import com.lucky.data_visual.enums.HttpStatusCode;
import com.lucky.data_visual.interfaces.FileProcessor;
import com.lucky.data_visual.model.JsonResult;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class CsvFileServer implements FileProcessor {
    private final static Logger logger = LoggerFactory.getLogger(CsvFileServer.class);
    private final SingleColDataServer colDataServer;

    @Autowired
    public CsvFileServer(SingleColDataServer colDataServer) {
        this.colDataServer = colDataServer;
    }

    @Override
    public List<Map<String, String>> readFile(MultipartFile file) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {
            // 读取表头
            String[] headers = csvReader.readNext(); // 获取第一行作为表头
            if (headers == null || headers.length == 0) {
                return null;
            }

            // 逐行读取数据
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < values.length; i++) {
                    row.put(headers[i], values[i]);
                }
                data.add(row);  // 每读取一行就添加数据
            }
            return data;
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initData(JsonResult<List<Map<String, Object>>> originalData, JsonResult<List<Map<String, Object>>> operationalData) {
        operationalData.setStatusCode(originalData.getStatusCode());
        operationalData.setMsgList(originalData.getMsgList());
        operationalData.setColTypes(originalData.getColTypes());
        operationalData.setData(originalData.getData());

        operationalData.addMsg("数据初始化成功");
    }

    @Override
    public String getFileType() {
        return "CSV";
    }

    // 将数据列转为整型或浮点型
    public JsonResult<List<Map<String, Object>>> convertToNumberOrString(JsonResult<List<Map<String, String>>> jsonResult) {
        List<Map<String, String>> csvData = new ArrayList<>(jsonResult.getData());
        List<Map<String, Object>> convertedData = new ArrayList<>();
        Map<String, Set<String>> colTypesMap = new LinkedHashMap<>();

        for (Map<String, String> row : csvData) {
            Map<String, Object> convertedRow = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : row.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // 初始化列类型集合
                colTypesMap.putIfAbsent(key, new HashSet<>());

                // 调用单列转换函数
                Object convertedValue = colDataServer.convertColValue(value, colTypesMap.get(key));
                convertedRow.put(key, convertedValue);
            }
            convertedData.add(convertedRow);
        }

        // 统一数据类型
        Map<String, String> unifyColMap = unifyColTypes(colTypesMap);

        // 将修改的数据转回原JSON
        JsonResult<List<Map<String, Object>>> newJsonResult = new JsonResult<>();
        Map<String, Map<String, Object>> colTypes = new HashMap<>();
        colTypes.put("optionalColTypes", new LinkedHashMap<>(colTypesMap));
        colTypes.put("currentColType", new LinkedHashMap<>(unifyColMap));

        newJsonResult.setStatusCode(HttpStatusCode.OK);
        newJsonResult.addMsg("数据列类型转换成功");
        newJsonResult.setData(convertedData);
        newJsonResult.setColTypes(colTypes);
        logger.info("====== 数据列类型转换成功 ======");
        return newJsonResult;
    }

    /**
     * 根据类型的等级，返回所有列的所属类型
     *
     * @param colTypesMap 每列可能拥有多个类型
     * @return 每个列只包含一种类型
     */
    private static Map<String, String> unifyColTypes(Map<String, Set<String>> colTypesMap) {
        Map<String, String> unifyColMap = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> entry : colTypesMap.entrySet()) {
            String key = entry.getKey();
            Set<String> colTypes = entry.getValue();

            if (colTypes.contains("Nan")) {
                unifyColMap.put(key, "Nan");
            } else if (colTypes.contains("String")) {
                unifyColMap.put(key, "String");
            } else if (colTypes.contains("Number")) {
                unifyColMap.put(key, "Number");
            } else {
                unifyColMap.put(key, "Unknown");
            }
        }
        return unifyColMap;
    }
}
