package com.lucky.data_visual.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Schema(description = "Python脚本路径类")
@Component
@ConfigurationProperties(prefix = "python-scripts-path")
public class PythonScriptsPath {

    @Schema(description = "解释器路径")
    private String pythonPath;

    @Schema(description = "数据预处理脚本路径")
    private String dataVizPath;

    // Getter and Setter
    public String getPythonPath() {
        return pythonPath;
    }

    public void setPythonPath(String pythonPath) {
        this.pythonPath = pythonPath;
    }

    public String getDataVizPath() {
        return dataVizPath;
    }

    public void setDataVizPath(String dataVizPath) {
        this.dataVizPath = dataVizPath;
    }

}
