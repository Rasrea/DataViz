package com.lucky.data_visual.model;

import com.lucky.data_visual.enums.FileType;
import com.lucky.data_visual.enums.HttpStatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Schema(description = "Json数据返回类")
@Component
public class JsonResult<T> {
    @Schema(description = "状态码", example = "1")
    private HttpStatusCode statusCode = HttpStatusCode.NOT_FOUND;

    @Schema(description = "提示信息", example = "操作完成！")
    private List<String> msgList = new ArrayList<>();

    @Schema(description = "数据列的类型")
    private Map<String, Map<String, Object>> colTypes = null;

    @Schema(description = "数据类型", example = "CSV")
    private FileType fileType = FileType.CSV;

    @Schema(description = "返回信息（如User）")
    private T data = null;

    public JsonResult() {}

    // Getter and Setter
    public List<String> getMsgList() {
        return msgList;
    }

    public void addMsg(String msg) {
        msgList.add(msg);
    }

    public void setMsgList(List<String> msgList) {
        this.msgList = msgList;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, Map<String, Object>> getColTypes() {
        return colTypes;
    }

    public void setColTypes(Map<String, Map<String, Object>> colTypes) {
        this.colTypes = colTypes;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
}
