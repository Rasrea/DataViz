/**
 * 绘制数据列中空值分布饼图
 * @param colData 数据列
 * @param titleText 标题
 * @param elementById 图表位置
 */
function plotNullValuePieChart(colData, titleText, elementById) {
    // 检查 colData 是否为数组
    if (!Array.isArray(colData)) {
        console.error("colData 不是一个数组:", colData);
        return;
    }

    // 统计空值和非空值的个数
    const {totalCount, missingCount, nonMissingCount} = countNullValues(colData);

    // 配置饼图
    const option = {
        title: {
            text: titleText,
            subtext: `空值: ${missingCount}个, 非空值: ${nonMissingCount}个`,
            left: 'center',
            top: '8%'  // 调整标题距离图像的距离
        },
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        series: [
            {
                name: '数据',
                type: 'pie',
                radius: '50%',
                data: [
                    {value: missingCount, name: '空值'},
                    {value: nonMissingCount, name: '非空值'}
                ],
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };

    // 初始化 ECharts
    const chart = echarts.init(document.getElementById(elementById));
    chart.setOption(option);
}

/**
 * 绘制统计前K项词频的饼图
 * @param colData 数据列
 * @param titleText 标题
 * @param K 只保留 K + 1 项
 * @param elementById 图表位置
 */
function plotTopKPieChart(colData, titleText, K, elementById) {
    // 检查 colData 是否为数组
    if (!Array.isArray(colData)) {
        console.error('colData 不是一个数组:', colData);
        return;
    }

    // 统计词频，并转为对应的饼图格式
    const wordCount = TopKWordFrequency(colData, K);
    const pieData = Object.keys(wordCount).map(word => ({
        name: word,
        value: wordCount[word]
    }));

    const option = {
        title: {
            text: titleText,
            left: 'center'
        },
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
            orient: 'vertical',
            left: 'right',
            data: pieData.map(item => item.name),
            textStyle: {
                color: '#555',
                fontWeight: 'bold'
            }
        },
        series: [
            {
                name: '类别',
                type: 'pie',
                radius: ['30%', '70%'], // 设置内外半径，形成环形图
                avoidLabelOverlap: false,
                label: {
                    show: true,
                    position: 'outside', // 标签显示在外部
                    formatter: '{b}: {c} ({d}%)',
                },
                labelLine: {
                    show: true
                },
                data: pieData
            }
        ]
    };

    // 初始化图表
    const chart = echarts.init(document.getElementById(elementById));
    chart.setOption(option);
}
