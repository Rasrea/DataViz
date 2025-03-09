/**
 * 绘制简易的词云图
 * @param colData 数据列
 * @param titleText 标题
 * @param elementById 图表位置
 */
function plotWordCloudChart(colData, titleText, elementById) {
    // 检查 colData 是否为数组
    if (!Array.isArray(colData)) {
        console.error("colData 不是一个数组:", colData);
        return;
    }

    // 存储词频
    const wordCount = {}

    // 遍历数据统计词频
    colData.forEach(word => {
        // 将词转为小写，避免影响统计
        // word = word.toLowerCase();

        // 如果词已经存在则加一，否则初始化为一
        wordCount[word] = wordCount[word] ? wordCount[word] + 1 : 1;
    })

    // 格式化数据为词云所需格式
    const wordCloudData = Object.keys(wordCount).map(word => ({
        name: word,
        value: wordCount[word]
    }));

    const option = {
        title: {
            text: titleText,
            left: "center"
        },
        tooltip: {
            show: true
        },
        series: [{
            type: "wordCloud",
            gridSize: 10    , // 网格大小
            sizeRange: [12, 60], // 词云字形的大小范围
            rotationRange: [-90, 90], // 词云词语的旋转角度范围
            rotationStep: 45, // 旋转步长
            shape: "circle", // 词云图形，其他选项包括'cardioid', 'diamond', 'triangle'等
            width: "100%",
            height: "100%",
            drawOutOfBound: false, // 是否绘制出界的文字
            textStyle: {
                fontFamily: "sans-serif",
                fontWeight: "bold",
                color: function () { // 随机生成颜色
                    return "rgb(" + [Math.round(Math.random() * 255), Math.round(Math.random() * 255), Math.round(Math.random() * 255)].join(",") + ")";
                }
            },
            data: wordCloudData
        }]
    };
    // 初始化ECharts
    const chart = echarts.init(document.getElementById(elementById));
    chart.setOption(option);
}