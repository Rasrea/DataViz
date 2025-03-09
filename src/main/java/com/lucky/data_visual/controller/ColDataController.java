package com.lucky.data_visual.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.data_visual.enums.HttpStatusCode;
import com.lucky.data_visual.enums.TableOperation;
import com.lucky.data_visual.model.JsonResult;
import com.lucky.data_visual.model.PythonScriptsPath;
import com.lucky.data_visual.server.PythonProcessorServer;
import com.lucky.data_visual.server.SingleColDataServer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "单列数据操作接口")
@RestController
@RequestMapping("/api/col")
public class ColDataController {
    private final static Logger logger = LoggerFactory.getLogger(ColDataController.class);

    private final JsonResult<List<Map<String, Object>>> operateJsonResult; // 操作数据
    private final JsonResult<Map<String, List<Object>>> colJsonResult; // 列数据

    private final SingleColDataServer colDataServer = new SingleColDataServer();

    @Resource
    private PythonScriptsPath pythonScriptsPath;

    @Value("${server.dataPath}")
    private String serverDataPath;

    @Autowired
    public ColDataController(@Qualifier("operationalData") JsonResult<List<Map<String, Object>>> operateJsonResult,
                         @Qualifier("colData") JsonResult<Map<String, List<Object>>> colJsonResult) {
        this.operateJsonResult = operateJsonResult;
        this.colJsonResult = colJsonResult;
    }

    @Operation(
            summary = "操作指定列的数据",
            description = "该接口接收一个列名参数，获取该列的所有数据，并返回该列的数据及其类型信息。",
            parameters = {
                    @Parameter(
                            name = "column",
                            description = "指定要操作的列名",
                            required = true,
                            schema = @Schema(type = "string")
                    )
            }
    )
    @GetMapping("/operation")
    public JsonResult<Map<String, List<Object>>> operateColData(@RequestParam String column) {
        // 检查 operateJsonResult 是否为 null
        if (operateJsonResult == null || operateJsonResult.getData() == null) {
            logger.error("operateJsonResult 数据为空");
            colJsonResult.setStatusCode(HttpStatusCode.BAD_REQUEST);
            colJsonResult.addMsg("操作数据为空");
            return colJsonResult;
        }

        // 获取该列数据
        Map<String, List<Object>> colData = colDataServer.getColData(operateJsonResult.getData(), column);

        // 获取该列的当前类型和可选类型
        Map<String, Map<String, Object>> colTypes = colDataServer.getCurAndOptColTypes(operateJsonResult.getColTypes(), column);

        // 将该列信息装配到Bean
        colJsonResult.setStatusCode(HttpStatusCode.OK);
        colJsonResult.addMsg(column + "列数据获取成功");
        colJsonResult.setColTypes(colTypes);
        colJsonResult.setData(colData);
        return colJsonResult;
    }

    @Operation(
            summary = "修改指定列的数据",
            description = "该接口接收一个表单，获取表单的数据，然后调用Python脚本，按照表单处理数据列，并重新注入operateJsonResult。",
            parameters = {
                    @Parameter(
                            name = "tableData",
                            description = "表单信息包括若干项",
                            required = true,
                            schema = @Schema(type = "Map")
                    )
            }
    )
    @PostMapping("/alterColData")
    public ResponseEntity<String> alterColData(@RequestBody Map<String, Object> tableData) throws JsonProcessingException {
        // 读取表格的操作步骤
        Map<String, Object> submitElementMap = new HashMap<>();
        for (TableOperation tableOperation : TableOperation.values()) {
            String operation = tableOperation.getOperation();
            submitElementMap.put(operation, "isDropNa".equals(operation) ? Boolean.parseBoolean((String) tableData.get(operation)) : tableData.get(operation));
        }

        // 将 表格操作数据 转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(submitElementMap);
        jsonString = jsonString.replace("\"", "\\\"");  // 转义双引号

        // 调用python脚本实现表格需求
        String[] cmd = {
                pythonScriptsPath.getPythonPath(), // Python解释器路径
                pythonScriptsPath.getDataVizPath(),
                "read_form_data",
                "\"" + jsonString + "\"",  // 包裹 JSON 字符串
                serverDataPath
        };

        try {
            String COL_NAME = TableOperation.COL_NAME.getOperation();
            String COL_TYPE = TableOperation.COL_TYPE.getOperation();

            List<Map<String, Object>> newCsvData = PythonProcessorServer.Run(cmd);
            // 将新的表格信息注入 operateJsonResult
            operateJsonResult.setData(newCsvData);
            operateJsonResult.addMsg(tableData.get(COL_NAME) + "数据列修复成功");
            // 更新当前列的数据类型
            operateJsonResult.getColTypes().get("currentColType").put((String) tableData.get(COL_NAME), tableData.get(COL_TYPE));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok("数据提交成功");
    }

}
