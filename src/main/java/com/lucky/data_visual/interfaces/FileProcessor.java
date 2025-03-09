package com.lucky.data_visual.interfaces;

import com.lucky.data_visual.model.JsonResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FileProcessor {
    // 读取文件内容
    Object readFile(MultipartFile file) throws IOException;

    // 初始化文件数据
    void initData(JsonResult<List<Map<String, Object>>> originalData, JsonResult<List<Map<String, Object>>> operationalData);

    // 返回文件类型
    String getFileType();

}
