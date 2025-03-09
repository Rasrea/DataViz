package com.lucky.data_visual.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SingleColDataProcessor {
    // 获取某一列数据
    Map<String, List<Object>> getColData(List<Map<String, Object>> data, String colName);

    // 删除某一列，返回删除后的表格数据
    List<Map<String, Object>> dropColData(List<Map<String, Object>> data, String colName);

    // 将该列数据转为数值型或字符型，返回该列可能的数据类型
    Object convertColValue(String value, Set<String> colTypes);

    // 获取该列的当前数据类型和可选数据类型
    Map<String, Map<String, Object>> getCurAndOptColTypes(Map<String, Map<String, Object>> colTypes, String colName);
}
