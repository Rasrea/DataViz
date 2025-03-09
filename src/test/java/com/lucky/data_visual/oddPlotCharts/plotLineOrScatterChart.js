/**
 * 绘制类别折线图：横坐标是序数，纵坐标是值
 * @param plotType line: 折线, scatter: 散点
 * @param xData X轴数据
 * @param yData Y轴数据
 * @param titleText 标题标签
 * @param subtext 概要
 * @param xLabel 横坐标标签
 * @param yLabel 纵坐标标签
 * @param elementById 图表位置
 */
function plotLineOrScatterChart(plotType, xData, yData, titleText, subtext, xLabel, yLabel, elementById) {
    // 检查 yData 是否为数组
    if (!Array.isArray(yData)) {
        console.error("yData 不是一个数组:", yData);
        return;
    }

    // 若xData为空，使用数据序号作为横坐标
    if (xData === '') {
        xData = Array.from({length: yData.length}, (_, i) => i + 1); // 生成 1, 2, 3, ... 序列
    }

    // 配置项
    const option = {
        title: {
            text: titleText,
            subtext: subtext,
            left: 'center',
        },
        tooltip: {
            trigger: 'axis',
        },
        xAxis: {
            type: 'category',  // 横坐标类型为类目型
            data: xData,
            name: xLabel,
            nameTextStyle: {
                color: '#333',
                fontWeight: 'bold',
                fontSize: 14
            },
        },
        yAxis: {
            type: 'value',  // 纵坐标类型为数值型
            name: yLabel,
            min: 'dataMin',
            max: 'dataMax',
            nameTextStyle: {
                color: '#333',
                fontWeight: 'bold',
                fontSize: 14
            }
        },
        series: [{
            name: yLabel,
            type: plotType,
            data: yData,
        }]
    };

    // 初始化图表
    const chart = echarts.init(document.getElementById(elementById));
    // 使用配置项设置图表
    chart.setOption(option);
}
