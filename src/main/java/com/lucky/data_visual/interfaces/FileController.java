package com.lucky.data_visual.interfaces;

import com.lucky.data_visual.model.JsonResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileController {
    // 设定操作JSON数据
    void setOperateJsonResult(JsonResult<List<Map<String, Object>>> result);

    // 接收上传的文件数据
    ResponseEntity<String> uploadData(@RequestParam("file") MultipartFile file);

    // 初始化表格数据
    void initData();
}
