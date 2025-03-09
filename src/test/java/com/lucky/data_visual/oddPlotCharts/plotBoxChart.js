/**
 * 绘制箱线图，包含离群点
 * @param colData 数据序列
 * @param titleText 标题标签
 * @param subText 概述
 * @param colName 列名
 * @param elementById 图像位置
 */
function plotBoxChart(colData, titleText, subText, colName, elementById) {
    // 计算四分位数和上下须
    const data = colData.sort((a, b) => a - b)
    const boxData = calculateQuartiles(data);
    const outliers = calculateOutliers(colData, boxData[0], boxData[4]);

    const option = {
        title: {
            text: titleText,
            subText: subText,
            left: 'center'
        },
        tooltip: {},
        xAxis: {
            type: 'category',
            data: [colName],
            axisLabel: {
                color: '#333',
                fontWeight: 'bold',  // 设置标签字体加粗
                fontSize: 14
            }
        },
        yAxis: {
            type: 'value',
            name: 'Value',
            min: data[0],
            nameTextStyle: {
                color: '#333',
                fontWeight: 'bold',
                fontSize: 14
            }
        },
        series: [{
            name: '箱线图',
            type: 'boxplot',
            data: [boxData],
            itemStyle: {
                color: '#66CC99'
            },
            tooltip: {
                formatter: function (param) {
                    return '下须: ' + param.data[1].toFixed(2) + '<br>' +
                        'Q1: ' + param.data[2].toFixed(2) + '<br>' +
                        '中位数(Q2): ' + param.data[3].toFixed(2) + '<br>' +
                        'Q3: ' + param.data[4].toFixed(2) + '<br>' +
                        '上须: ' + param.data[5].toFixed(2);
                }
            },
            markLine: {
                data: [{
                    name: '中位数',
                    yAxis: boxData[2],
                    lineStyle: {
                        color: '#ffa600'
                    }
                }]
            }
        },
            {
                name: '离群点',
                type: 'scatter',
                data: outliers.map(value => ['数据集', value]),
                itemStyle: {
                    color: 'transparent',
                    borderColor: '#ff0000',
                    borderWidth: 2
                },
                tooltip: {
                    trigger: 'item'
                }
            }]
    };

    // ECharts 配置
    const chart = echarts.init(document.getElementById(elementById));
    chart.setOption(option);
}