const chart = new Chart();

// 获取相关列名
function fetchColName() {
    return new URLSearchParams(window.location.search).get('column');
}

// 创建相关图表
function createCharts(data, colName) {
    const currentColType = data["colTypes"]["currentColType"][colName];

    // 将 列名 传入后端
    fetch(`http://localhost:8080/api/operation/readColData?colName=${colName}`)
        .then(isRead => {
            if (!isRead.ok) {
                console.error('读取数据失败');
            }

            // 创建统计空值个数的饼图
            fetch('plotCharts/configs/PieChart.json')
                .then(response => response.json())
                .then(roseChartElements => {
                    fetch(`http://localhost:8080/api/operation/emptyAndNonEmptyData`)
                        .then(response => response.json())
                        .then(data => {
                            console.log(data);


                            roseChartElements.series[0].radius = ["0%", "55%"]; // 修改饼图大小
                            roseChartElements.title.text = `${colName} 数据空值统计`;
                            roseChartElements.title.subtext = `空值: ${data[0].value}个, 非空值: ${data[1].value}个`;
                            roseChartElements.legend.show = false; // 隐藏图例
                            roseChartElements.series[0].roseType = 'radius'; // 玫瑰图
                            roseChartElements.series[0].data = data;
                            chart.setContainerId('chart1');
                            chart.plotWithConfig(roseChartElements);
                        });
                })
                .catch(error => console.error('配置文件有误(PieChart):', error));

            // 创建统计前 K 项频数的玫瑰图
            fetch('plotCharts/configs/PieChart.json')
                .then(response => response.json())
                .then(pieChartElements => {
                    fetch(`http://localhost:8080/api/operation/topKFrequency?K=5`)
                        .then(response => response.json())
                        .then(data => {
                            pieChartElements.series[0].radius = ["0%", "65%"]; // 修改饼图大小
                            pieChartElements.title.text = `${colName} Top5类别分析`;
                            pieChartElements.title.subtext = '';
                            pieChartElements.series[0].data = data;
                            chart.setContainerId('chart4');
                            chart.plotWithConfig(pieChartElements);
                        });
                })
                .catch(error => console.error('配置文件有误(PieChart):', error));

            if (currentColType === "Number") {
                // 绘制散点图
                fetch('plotCharts/configs/ScatterChart.json')
                    .then(response => response.json())
                    .then(scatterChartElements => {
                        fetch(`http://localhost:8080/api/operation/singleLabelsAndValues`)
                            .then(response => response.json())
                            .then(data => {
                                scatterChartElements.title.text = `${colName} 散点分析`;
                                scatterChartElements.title.subtext = '';
                                scatterChartElements.xAxis.name = 'Index';
                                scatterChartElements.yAxis.name = `${colName}`;
                                scatterChartElements.legend.show = false;
                                scatterChartElements.tooltip.formatter = `数据点：<br/>Index: {b0}<br/>${colName}: {c0}`;
                                scatterChartElements.series[0].data = data;
                                chart.setContainerId('chart3');
                                chart.plotWithConfig(scatterChartElements);
                            });
                    });

                // 绘制箱线图
                fetch('plotCharts/configs/BoxChart.json')
                    .then(response => response.json())
                    .then(boxChartElements => {
                        fetch(`http://localhost:8080/api/operation/quartilesAndWhisker?isGetOutliers=true`)
                            .then(response => response.json())
                            .then(data => {
                                console.log(data);

                                // 箱线图
                                const boxData = data['quartilesAndWhisker'];
                                boxChartElements.xAxis.data = [`${colName}`];
                                boxChartElements.series[0].data = [boxData];
                                boxChartElements.series[0].formatter = {
                                    function(param) {
                                        return '下须: ' + param.data[1].toFixed(2) + '<br>' +
                                            'Q1: ' + param.data[2].toFixed(2) + '<br>' +
                                            '中位数(Q2): ' + param.data[3].toFixed(2) + '<br>' +
                                            'Q3: ' + param.data[4].toFixed(2) + '<br>' +
                                            '上须: ' + param.data[5].toFixed(2);
                                    }
                                };
                                boxChartElements.title.text = `${colName} 箱线分析`;
                                boxChartElements.series[0].markLine.data[0].yAxis = boxData[2];

                                // 离群点
                                const outliers = data['outliers'];
                                boxChartElements.series[1].data = outliers.map(value => ['数据集', value]);
                                chart.setContainerId('chart2');
                                chart.plotWithConfig(boxChartElements);
                            });
                    });
            } else if (currentColType === "String" || currentColType === "Date") {
                // 绘制词云图
                fetch('plotCharts/configs/WordCloudChart.json')
                    .then(response => response.json())
                    .then(wordCloudChartElements => {
                        fetch(`http://localhost:8080/api/operation/topKFrequency?K=1000`)
                            .then(response => response.json())
                            .then(data => {
                                wordCloudChartElements.series[0].textStyle.color = function () { // 随机生成颜色
                                    return "rgb(" + [Math.round(Math.random() * 160), Math.round(Math.random() * 160), Math.round(Math.random() * 160)].join(",") + ")";
                                };
                                wordCloudChartElements.title.text = `${colName} 词云分析`;
                                wordCloudChartElements.title.subtext = '';
                                wordCloudChartElements.series[0].data = data;
                                chart.setContainerId('chart2');
                                chart.plotWithConfig(wordCloudChartElements);
                            });
                    })
                    .catch(error => console.error('配置文件有误:', error));

                // 绘制条形图
                fetch('plotCharts/configs/BarChart.json')
                    .then(response => response.json())
                    .then(barChartElements => {
                        fetch(`http://localhost:8080/api/operation/topKFrequency?K=10`)
                            .then(response => response.json())
                            .then(data => {
                                barChartElements.title.text = `${colName} 条形分析`;
                                barChartElements.title.subtext = '';
                                barChartElements.xAxis.name = `${colName}`;
                                barChartElements.xAxis.data = data.map(item => item.name);
                                barChartElements.yAxis.name = 'Value';
                                barChartElements.series[0].data = data;

                                console.log(barChartElements);

                                chart.setContainerId('chart3');
                                chart.plotWithConfig(barChartElements);
                            })
                    })
            } else {
                displayChartText('chart2Text', '图二：<br>Number类型绘制箱线图;<br>String类型绘制词云图;<br>Date类型同String;<br>错误列不显示;');
                displayChartText('chart3Text', '图三：<br>Number类型绘制散点图;<br>String类型绘制条形图;<br>Date类型同String;<br>错误列不显示;');
            }

        });


}

// 若数据不符合要求，则显示提示
function displayChartText(elementId, text) {
    const element = document.getElementById(elementId);
    element.style.display = "flex";
    element.innerHTML = text;
}

// 创建数据列修改表格
function createAlterTable(data, colName) {
    const currentColType = data["colTypes"]["currentColType"][colName];
    const optionalColTypes = [currentColType, ...data["colTypes"]["optionalColTypes"][colName].filter(option => option !== currentColType)];
    document.getElementById("form-title").innerHTML = `${colName} 信息`; // 表格标题
    const colTypeSelect = document.getElementById("colType");
    colTypeSelect.innerHTML = optionalColTypes.map(option => `<option value="${option}">${option}</option>`).join('');
    const isDropNaSelect = document.getElementById("isDropNa");
    const fillTypeSelect = document.getElementById("fillType");

    // 定义可供选择的修改项
    const optionalNumberFillType = {
        "使用均值": "mean",
        "使用中位数": "median",
        "使用众数": "mode",
        "使用上下值的均值": "upperAndLowerMean",
    };
    const optionalStringFillType = {
        "使用'Unknown'填充": "fillUnknown"
    };

    function updateFillTypeSelect() {
        fillTypeSelect.innerHTML = "";
        fillTypeSelect.disabled = isDropNaSelect.value === "true" || colTypeSelect.value === "Nan";

        if (!fillTypeSelect.disabled) {
            const fillTypes = colTypeSelect.value === "Number" ? optionalNumberFillType : optionalStringFillType;
            fillTypeSelect.innerHTML = Object.entries(fillTypes).map(([key, value]) => `<option value="${value}">${key}</option>`).join('');
        }
    }

    isDropNaSelect.addEventListener("change", updateFillTypeSelect);
    colTypeSelect.addEventListener("change", updateFillTypeSelect);
    updateFillTypeSelect();

    sentFormData('colAlterForm', colName);
}

// 将表单元素发送给后端
function sentFormData(formId, colName) {
    const form = document.getElementById(formId);

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const formData = new FormData(form);
        const formDataObject = Object.fromEntries(formData.entries());
        formDataObject["colName"] = colName;

        fetch("http://localhost:8080/api/col/alterColData", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(formDataObject),
        })
            .then(_ => {
                // window.location.reload();
                window.location.href = "check.html?refresh=true"; // 提交成功后刷新页面

            })
            .catch(error => {
                console.error("请求失败:", error);
                alert("提交失败，请重试！");
            });
    });
}

// 获取相关列的数据
function fetchColData() {
    const colName = fetchColName();

    fetch(`http://localhost:8080/api/col/operation?column=${colName}`)
        .then(response => response.json())
        .then(data => {
            createAlterTable(data, colName);
            createCharts(data, colName);
        })
        .catch(error => {
            console.error('操作失败：', error);
        });
}

window.onload = fetchColData;
