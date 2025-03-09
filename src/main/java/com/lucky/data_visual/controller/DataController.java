package com.lucky.data_visual.controller;

import com.lucky.data_visual.enums.HttpStatusCode;
import com.lucky.data_visual.model.JsonResult;
import com.lucky.data_visual.model.SampleDataPath;
import com.lucky.data_visual.server.CsvFileServer;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "数据集接口")
@RestController
@RequestMapping("/data")
public class DataController {
    private final static Logger logger = LoggerFactory.getLogger(DataController.class);

    private final CsvFileServer csvFileServer;
    private final JsonResult<List<Map<String, String>>> jsonResult;
    private final JsonResult<List<Map<String, Object>>> operateJsonResult;

    @Resource
    private SampleDataPath sampleDataPath;

    @Autowired
    public DataController(@Qualifier("originalData") JsonResult<List<Map<String, String>>> jsonResult,
                          @Qualifier("operationalData") JsonResult<List<Map<String, Object>>> operateJsonResult,
                          CsvFileServer csvFileServer) {
        this.jsonResult = jsonResult;
        this.operateJsonResult = operateJsonResult;
        this.csvFileServer = csvFileServer;
    }

    @Autowired
    public void setOperateJsonResult(JsonResult<List<Map<String, Object>>> result) {
        operateJsonResult.setStatusCode(result.getStatusCode());
        operateJsonResult.setMsgList(result.getMsgList());
        operateJsonResult.setColTypes(result.getColTypes());
        operateJsonResult.setData(result.getData());
    }

    @Operation(summary = "获取CSV操作数据", description = "存储操作数据")
    @GetMapping("/fetch-csv")
    public JsonResult<List<Map<String, Object>>> getTableData() {
        if (operateJsonResult.getStatusCode() != HttpStatusCode.OK) logger.error("!!!!!! 操作数据上传错误 !!!!!!");
        return operateJsonResult;
    }

    @Operation(summary = "豆瓣读书Top250", description = "存储样本数据--豆瓣读书Top250")
    @GetMapping("/douban-books")
    public JsonResult<List<Map<String, Object>>> getSampleData1() {
        JsonResult<List<Map<String, String>>> sampleData1 = readCsv(sampleDataPath.getDoubanBookPath());
        if (sampleData1 != null && sampleData1.getStatusCode() != HttpStatusCode.OK) {
            logger.error("!!!!!! 豆瓣读书Top250上传失败 !!!!!!");
        }
        if (sampleData1 != null) {
            setOperateJsonResult(csvFileServer.convertToNumberOrString(sampleData1));
        }
        return operateJsonResult;
    }

    @Operation(summary = "猫眼电影榜单", description = "存储样本数据--猫眼电影榜单")
    @GetMapping("/maoyan-films")
    public JsonResult<List<Map<String, Object>>> getSampleData2() {
        JsonResult<List<Map<String, String>>> sampleData2 = readCsv(sampleDataPath.getMaoyanFilmPath());
        if (sampleData2 != null && sampleData2.getStatusCode() != HttpStatusCode.OK)
            logger.error("!!!!!! 猫眼电影榜单上传失败 !!!!!!");
        if (sampleData2 != null) {
            setOperateJsonResult(csvFileServer.convertToNumberOrString(sampleData2));
        }
        return operateJsonResult;
    }

    @Operation(summary = "Metacritic游戏数据集", description = "存储样本数据--Metacritic游戏数据集")
    @GetMapping("/metacritic-games")
    public JsonResult<List<Map<String, Object>>> getSampleData3() {
        JsonResult<List<Map<String, String>>> sampleData3 = readCsv(sampleDataPath.getMetacriticGamePath());
        if (sampleData3 != null && sampleData3.getStatusCode() != HttpStatusCode.OK)
            logger.error("!!!!!! Metacritic游戏上传失败 !!!!!!");
        if (sampleData3 != null) {
            setOperateJsonResult(csvFileServer.convertToNumberOrString(sampleData3));
        }
        return operateJsonResult;
    }

    @Operation(summary = "randomData", description = "存储样本数据--randomData")
    @GetMapping("/random-data")
    public JsonResult<List<Map<String, Object>>> getSampleData4() {
        JsonResult<List<Map<String, String>>> sampleData4= readCsv(sampleDataPath.getRandomDataPath());
        if (sampleData4 != null && sampleData4.getStatusCode() != HttpStatusCode.OK)
            logger.error("!!!!!! randomData上传失败 !!!!!!");
        if (sampleData4 != null) {
            setOperateJsonResult(csvFileServer.convertToNumberOrString(sampleData4));
        }
        return operateJsonResult;
    }

    /**
     * 读取样本数据集
     * @param filePath 数据集地址
     * @return 返回JsonResult
     */
    public JsonResult<List<Map<String, String>>> readCsv(String filePath) {
        List<Map<String, String>> csvData = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
             // 读取第一行，作为列名
            String[] headers = reader.readNext();
            if (headers == null) {
                logger.warn("====== CSV文件没有数据 ======");
                return null;
            }

            // 逐行读取CSV数据
            String[] lines;
            while ((lines = reader.readNext()) != null) {
                // 将每一行转为Map，列名为键，数据值作为值
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i], lines[i]);
                }
                csvData.add(row);
            }

            // 将上传的数据注入到 JsonResult 中
            jsonResult.setStatusCode(HttpStatusCode.OK);
            jsonResult.addMsg("样本数据读取成功");
            jsonResult.setData(csvData);
            logger.info("====== 样本数据读取成功 ======");
            return jsonResult;
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
