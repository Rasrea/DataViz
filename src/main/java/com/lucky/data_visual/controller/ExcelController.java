package com.lucky.data_visual.controller;

import com.lucky.data_visual.enums.HttpStatusCode;
import com.lucky.data_visual.interfaces.FileController;
import com.lucky.data_visual.model.JsonResult;
import com.lucky.data_visual.server.CsvFileServer;
import com.lucky.data_visual.server.ExcelFileServer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Tag(name = "Excel操作接口")
@RestController
@RequestMapping("/api/excel")
public class ExcelController implements FileController {
    private final static Logger logger = LoggerFactory.getLogger(ExcelController.class);
    private final JsonResult<Map<String, List<Map<String, String>>>> excelJsonResult; // 原始Excel数据
    private final JsonResult<List<Map<String, Object>>> operateJsonResult; // 操作数据
    private final JsonResult<List<Map<String, String>>> jsonResult; // 原始的单个表数据

    private final ExcelFileServer excelFileServer;
    private final CsvFileServer csvFileServer;

    @Autowired
    public ExcelController(@Qualifier("excelData") JsonResult<Map<String, List<Map<String, String>>>> excelJsonResult,
                           @Qualifier("operationalData") JsonResult<List<Map<String, Object>>> operateJsonResult,
                           @Qualifier("originalData") JsonResult<List<Map<String, String>>> jsonResult,
                           CsvFileServer csvFileServer,
                           ExcelFileServer excelFileServer) {
        this.excelJsonResult = excelJsonResult;
        this.operateJsonResult = operateJsonResult;
        this.jsonResult = jsonResult;
        this.csvFileServer = csvFileServer;
        this.excelFileServer = excelFileServer;
    }

    @Autowired
    @Override
    public void setOperateJsonResult(JsonResult<List<Map<String, Object>>> result) {
        operateJsonResult.setStatusCode(result.getStatusCode());
        operateJsonResult.setMsgList(result.getMsgList());
        operateJsonResult.setColTypes(result.getColTypes());
        operateJsonResult.setData(result.getData());
    }

    @Operation(
            summary = "读取前端选择的Excel文件，并装配Bean",
            description = "此接口用于接收前端上传的Excel文件，解析文件内容并将其装配成指定格式的Bean对象。若文件无效或格式不正确，会返回错误信息。",
            parameters = {
                    @Parameter(
                            name = "file",
                            description = "上传的Excel文件",
                            required = true,
                            content = @Content(mediaType = "multipart/form-data")
                    )
            }
    )
    @PostMapping("/upload")
    @Override
    public ResponseEntity<String> uploadData(MultipartFile file) {
        try {
            Map<String, List<Map<String, String>>> excelData; // 存储Excel数据
            if ((excelData = excelFileServer.readFile(file)) != null) {
                List<String> sheetNames = excelFileServer.getSheetNames();

                // 将上传的数据注入 excelJsonResult
                excelJsonResult.setData(excelData);
                excelJsonResult.setStatusCode(HttpStatusCode.OK);
                excelJsonResult.addMsg("Excel文件上传成功");

                // 将第一个表格数据注入 jsonResult 用来初始化
                jsonResult.setData(excelJsonResult.getData().get(sheetNames.get(0)));
                jsonResult.setStatusCode(HttpStatusCode.OK);

                // 将第一个表格注入操作JSON中
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

    @Override
    public void initData() {
        // 直接调用 CsvController 中的initData方法
    }

    @Operation(summary = "获取Excel中各个工作表的名称供前端读取")
    @GetMapping("/sheetNames")
    public List<String> getSheetNames() {
        return excelFileServer.getSheetNames();
    }

    @Operation(
            summary = "获取指定表格数据",
            description = "该接口接收一个表名参数，获取该表的所有数据，供前端读取单个表格。",
            parameters = {
                    @Parameter(
                            name = "sheetName",
                            description = "指定的表明",
                            required = true,
                            schema = @Schema(type = "string")
                    )
            }
    )
    @GetMapping("/data")
    public JsonResult<List<Map<String, Object>>> getSheetData(@RequestParam String sheetName) {
        // 将原始数据替换成新数据
        jsonResult.setData(excelJsonResult.getData().get(sheetName));
        jsonResult.addMsg(sheetName + "数据表读取成功");
        jsonResult.setStatusCode(HttpStatusCode.OK);

        // 将操作数据替换成新数据
        JsonResult<List<Map<String, Object>>> result = csvFileServer.convertToNumberOrString(jsonResult);
        setOperateJsonResult(result);

        return operateJsonResult;
    }
}
