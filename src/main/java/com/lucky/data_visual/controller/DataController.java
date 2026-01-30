package com.lucky.data_visual.controller;

import com.lucky.data_visual.enums.FileType;
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
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "数据集接口")
@RestController
@RequestMapping("/data")
public class DataController {
    private final static Logger logger = LoggerFactory.getLogger(DataController.class);

    private final ResourceLoader resourceLoader;
    private final CsvFileServer csvFileServer;
    private final JsonResult<List<Map<String, String>>> jsonResult;
    private final JsonResult<List<Map<String, Object>>> operateJsonResult;

    @Resource
    private SampleDataPath sampleDataPath;

    @Autowired
    public DataController(ResourceLoader resourceLoader, @Qualifier("originalData") JsonResult<List<Map<String, String>>> jsonResult, @Qualifier("operationalData") JsonResult<List<Map<String, Object>>> operateJsonResult, CsvFileServer csvFileServer) {
        this.resourceLoader = resourceLoader;
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
        operateJsonResult.setFileType(FileType.CSV);
    }

    @Operation(summary = "获取 CSV/Excel 操作数据", description = "存储操作数据")
    @GetMapping("/fetch-csv")
    public JsonResult<List<Map<String, Object>>> getTableData() {
        return operateJsonResult;
    }

    @Operation(summary = "豆瓣读书Top250", description = "存储样本数据--豆瓣读书Top250")
    @GetMapping("/douban-books")
    public JsonResult<List<Map<String, Object>>> getSampleData1() throws IOException {
        // 从 classpath 加载资源
        org.springframework.core.io.Resource resource = resourceLoader.getResource(sampleDataPath.getDoubanBookPath());

        // 读取 CSV
        JsonResult<List<Map<String, String>>> csvResult = readCsv(resource.getInputStream());

        // 类型转换 + 设置返回值
        if (csvResult != null) {
            setOperateJsonResult(csvFileServer.convertToNumberOrString(csvResult));
        }

        return operateJsonResult;
    }

    @Operation(summary = "猫眼电影榜单", description = "存储样本数据--猫眼电影榜单")
    @GetMapping("/maoyan-films")
    public JsonResult<List<Map<String, Object>>> getSampleData2() throws IOException {
        // 从 classpath 加载资源
        org.springframework.core.io.Resource resource = resourceLoader.getResource(sampleDataPath.getMaoyanFilmPath());

        // 读取 CSV
        JsonResult<List<Map<String, String>>> csvResult = readCsv(resource.getInputStream());

        // 类型转换 + 设置返回值
        if (csvResult != null) {
            setOperateJsonResult(csvFileServer.convertToNumberOrString(csvResult));
        }

        return operateJsonResult;
    }

    @Operation(summary = "Metacritic游戏数据集", description = "存储样本数据--Metacritic游戏数据集")
    @GetMapping("/metacritic-games")
    public JsonResult<List<Map<String, Object>>> getSampleData3() throws IOException {
        // 从 classpath 加载资源
        org.springframework.core.io.Resource resource = resourceLoader.getResource(sampleDataPath.getMetacriticGamePath());

        // 读取 CSV
        JsonResult<List<Map<String, String>>> csvResult = readCsv(resource.getInputStream());

        // 类型转换 + 设置返回值
        if (csvResult != null) {
            setOperateJsonResult(csvFileServer.convertToNumberOrString(csvResult));
        }

        return operateJsonResult;
    }

    @Operation(summary = "randomData", description = "存储样本数据--randomData")
    @GetMapping("/random-data")
    public JsonResult<List<Map<String, Object>>> getSampleData4() throws IOException {
        // 从 classpath 加载资源
        org.springframework.core.io.Resource resource = resourceLoader.getResource(sampleDataPath.getRandomDataPath());

        // 读取 CSV
        JsonResult<List<Map<String, String>>> csvResult = readCsv(resource.getInputStream());

        // 类型转换 + 设置返回值
        if (csvResult != null) {
            setOperateJsonResult(csvFileServer.convertToNumberOrString(csvResult));
        }

        return operateJsonResult;
    }

    /**
     * 读取样本数据集
     *
     * @param inputStream 数据集地址
     * @return 返回JsonResult
     */
    public JsonResult<List<Map<String, String>>> readCsv(InputStream inputStream) {
        List<Map<String, String>> csvData = new ArrayList<>();

        try (InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8); CSVReader reader = new CSVReader(isr)) {
            // 1. 读取第一行作为表头
            String[] headers = reader.readNext();
            if (headers == null) {
                logger.warn("====== CSV文件没有数据 ======");
                return null;
            }

            // 2. 逐行读取数据
            String[] lines;
            while ((lines = reader.readNext()) != null) {
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.length && i < lines.length; i++) {
                    row.put(headers[i], lines[i]);
                }
                csvData.add(row);
            }

            // 3. 封装返回结果
            jsonResult.setStatusCode(HttpStatusCode.OK);
            jsonResult.addMsg("样本数据读取成功");
            jsonResult.setData(csvData);

            logger.info("====== 样本数据读取成功 ======");
            return jsonResult;

        } catch (IOException | CsvValidationException e) {
            logger.error("CSV文件读取失败", e);
            throw new RuntimeException("CSV文件读取失败", e);
        }
    }
}
