package com.lucky.data_visual.controller;

import com.lucky.data_visual.enums.FileType;
import com.lucky.data_visual.enums.HttpStatusCode;
import com.lucky.data_visual.interfaces.FileController;
import com.lucky.data_visual.model.JsonResult;
import com.lucky.data_visual.server.CsvFileServer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "CSV操作接口")
@RestController
@RequestMapping("/api/csv")
public class CsvController implements FileController {
    private final static Logger logger = LoggerFactory.getLogger(CsvController.class);

    private final JsonResult<List<Map<String, String>>> jsonResult; // 原始数据
    private final JsonResult<List<Map<String, Object>>> operateJsonResult; // 操作数据
    private final CsvFileServer csvFileServer;

    @Autowired
    public CsvController(@Qualifier("originalData") JsonResult<List<Map<String, String>>> jsonResult,
                         @Qualifier("operationalData") JsonResult<List<Map<String, Object>>> operateJsonResult,
                         CsvFileServer csvFileServer) {
        this.jsonResult = jsonResult;
        this.operateJsonResult = operateJsonResult;
        this.csvFileServer = csvFileServer;
    }

    @Autowired
    @Override
    public void setOperateJsonResult(JsonResult<List<Map<String, Object>>> result) {
        operateJsonResult.setStatusCode(result.getStatusCode());
        operateJsonResult.setMsgList(result.getMsgList());
        operateJsonResult.setColTypes(result.getColTypes());
        operateJsonResult.setData(result.getData());
        operateJsonResult.setFileType(FileType.CSV);
    }

    @Operation(
            summary = "读取前端选择的CSV文件，并装配Bean",
            description = "此接口用于接收前端上传的CSV文件，解析文件内容并将其装配成指定格式的Bean对象。若文件无效或格式不正确，会返回错误信息。",
            parameters = {
                    @Parameter(
                            name = "file",
                            description = "上传的CSV文件",
                            required = true,
                            content = @Content(mediaType = "multipart/form-data")
                    )
            }
    )
    @PostMapping("/upload")
    @Override
    public ResponseEntity<String> uploadData(@RequestParam("file") MultipartFile file) {
        List<Map<String, String>> csvData; // 存储CSV数据

        try {
            if ((csvData = csvFileServer.readFile(file)) != null) {
                // 将上传的数据注入到 JsonResult 供初始化操作
                jsonResult.setData(csvData);
                jsonResult.setStatusCode(HttpStatusCode.OK);

                // 将数据注入 operationalData 供后续处理
                JsonResult<List<Map<String, Object>>> result = csvFileServer.convertToNumberOrString(jsonResult);
                setOperateJsonResult(result);

                logger.info("====== 用户文件上传成功 ======");
                return ResponseEntity.ok("文件上传成功！");
            } else {
                return ResponseEntity.status(404).body("文件体为空！");
            }
        } catch (IOException e) {
            logger.error("!!!!!! 用户文件上传失败 !!!!!!", e);
            return ResponseEntity.status(500).body("文件上传失败！");
        }
    }

    @Operation(summary = "初始化表格数据")
    @PostMapping("/initData")
    @Override
    public void initData() {
        JsonResult<List<Map<String, Object>>> oddData = csvFileServer.convertToNumberOrString(jsonResult);
        csvFileServer.initData(oddData, operateJsonResult);
    }
}
