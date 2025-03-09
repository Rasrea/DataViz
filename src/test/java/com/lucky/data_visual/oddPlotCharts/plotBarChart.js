function plotBarChart(xData, yData, titleText, subtext, xLabel, yLabel, elementById) {
    // 检查 yData 是否为数组
    if (!Array.isArray(yData)) {
        console.error("yData 不是一个数组:", yData);
        return;
    }

    const option = {
        title: {
            text: titleText,
            subtext: subtext,
            left: 'center'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {type: 'shadow'} // 设置鼠标指针类型
        },
        grid: {
            left: '3%', // 设置网格左边距为容器宽度的 3%
            right: '4%', // 设置网格右边距为容器宽度的 4%
            bottom: '3%', // 设置网格底边距为容器高度的 3%
            containLabel: true // 确保网格区域包含坐标轴的标签
        },
        xAxis: {
            type: 'value', // X轴是数值轴
            // name: xLabel,
            // nameTextStyle: {
            //     fontWeight: 'bold',
            //     fontSize: 14
            // }
        },
        yAxis: {
            type: 'category', // Y轴是类目轴
            data: yData, // 类别标签
            name: yLabel,
            nameTextStyle: {
                fontWeight: 'bold',
                fontSize: 14
            },
        },
        series: [{
            name: '数量',
            type: 'bar', // 条形图
            data: xData, // 每个类别的数值
            itemStyle: {
                color: '#3398DB', // 设置条形图的颜色
                borderRadius: [5, 5, 5, 5], // 设置条形的圆角
                shadowBlur: 10, // 阴影模糊程度
                shadowOffsetX: 0, // 阴影水平偏移
                shadowOffsetY: 6, // 阴影垂直偏移
                shadowColor: 'rgba(0, 0, 0, 0.1)' // 阴影颜色
            }
        }]
    };
    // 初始化图表
    const chart = echarts.init(document.getElementById(elementById));
    // 使用配置项设置图表
    chart.setOption(option);
}