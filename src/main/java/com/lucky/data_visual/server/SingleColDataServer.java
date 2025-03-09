package com.lucky.data_visual.server;

import com.lucky.data_visual.interfaces.SingleColDataProcessor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SingleColDataServer implements SingleColDataProcessor {

    @Override
    public Map<String, List<Object>> getColData(List<Map<String, Object>> data, String colName) {
        Map<String, List<Object>> colData = new HashMap<>();
        for (Map<String, Object> row : data) {
            Object value = row.get(colName);
            colData.putIfAbsent(colName, new ArrayList<>());
            colData.get(colName).add(value);
        }
        return colData;
    }

    @Override
    public List<Map<String, Object>> dropColData(List<Map<String, Object>> data, String colName) {
        // 创建一个新的数据序列，避免修改原始data
        List<Map<String, Object>> newData = new ArrayList<>();
        for (Map<String, Object> row : data) {
            Map<String, Object> newRow = new LinkedHashMap<>(row);
            newRow.remove(colName);
            newData.add(newRow);
        }
        return newData;
    }

    @Override
    public Object convertColValue(String value, Set<String> colTypes) {
        try {
            // 将值转为整型
            Integer intValue = Integer.parseInt(value);
            colTypes.add("Number");
            return intValue;
        } catch (NumberFormatException e) {
            try {
                // 将值转为浮点型
                Double doubleValue = Double.parseDouble(value);
                colTypes.add("Number");
                return doubleValue;
            } catch (NumberFormatException ex) {
                // 保持原值
                if (value.isEmpty()) {
                    colTypes.add("Nan");
                } else {
                    colTypes.add("String");
                    colTypes.add("Date");
                }
                return value;
            }
        }
    }

    @Override
    public Map<String, Map<String, Object>> getCurAndOptColTypes(Map<String, Map<String, Object>> colTypeMap, String colName) {
        Map<String, Map<String, Object>> colTypes = new HashMap<>();
        String[] typeKeys = {"currentColType", "optionalColTypes"};

        for (String typeKey : typeKeys) {
            Object typeValue = colTypeMap.get(typeKey).get(colName);
            Map<String, Object> typeMap = new HashMap<>();
            typeMap.put(colName, typeValue);
            colTypes.put(typeKey, typeMap);
        }
        return colTypes;
    }

}
