window.onload = function () {
    const chartServer = new ChartServer();

    const multiData = {
        labels: ['1月', '2月', '3月', '4月'],
        values: [820, 932, 901, 934]
    };

    const colData = ['1月', '1月', '2月', '3月', '4月', '1月', '2月', '3月', '4月'];
    const haveNullData = chartServer.countNullValues(colData);
    // console.log(haveNullData);
    const topKWordData = chartServer.topKWordFrequency(colData, 3);
    // console.log(topKWordData);

    // 绘制前 K 个类别的饼图
    fetch('../plotCharts/configs/PieChart.json')
        .then(response => response.json())
        .then(pieChartElements => {
            // 绘制折线图
            const chart = new Chart('pieChart', topKWordData, pieChartElements, new SingleColumnStrategy());
            chart.plot();
        })
        .catch(error => console.error('配置文件有误:', error));

    // 绘制条形图
    fetch('../plotCharts/configs/BarChart.json')
        .then(response => response.json())
        .then(barChartElements => {
            const chart = new Chart('barChart', multiData, barChartElements, new DoubleColumnStrategy());
            chart.plot();
        })
        .catch(error => console.error('配置文件有误:', error));

    // 绘制词云图
    // fetch('../plotCharts/configs/WordCloudChart.json')
    //     .then(response => response.json())
    //     .then(wordCloudChartElements => {
    //         wordCloudChartElements.series[0].textStyle.color = function () { // 随机生成颜色
    //             return "rgb(" + [Math.round(Math.random() * 160), Math.round(Math.random() * 160), Math.round(Math.random() * 160)].join(",") + ")";
    //         };
    //         const chart = new Chart('wordCloudChart', topKWordData, wordCloudChartElements, new SingleColumnStrategy());
    //         chart.plot();
    //     })
    //     .catch(error => console.error('配置文件有误:', error));

    // 绘制箱线图
    // fetch('../plotCharts/configs/BoxChart.json')
    //     .then(response => response.json())
    //     .then(boxChartElements => {
    //         const randomIntegers = Array.from({length: 100}, () => Math.floor(Math.random() * 101));
    //         const sortNumbers = randomIntegers.sort((a, b) => a - b) // 排序
    //         const boxData = chartServer.calculateQuartiles(sortNumbers);
    //         const outliers = chartServer.calculateOutliers(colData, boxData[0], boxData[4]);
    //
    //         boxChartElements.xAxis.data = ['随机数据集'];
    //         boxChartElements.series[0].data = [boxData];
    //         boxChartElements.series[0].formatter = {
    //             function(param) {
    //                 return '下须: ' + param.data[1].toFixed(2) + '<br>' +
    //                     'Q1: ' + param.data[2].toFixed(2) + '<br>' +
    //                     '中位数(Q2): ' + param.data[3].toFixed(2) + '<br>' +
    //                     'Q3: ' + param.data[4].toFixed(2) + '<br>' +
    //                     '上须: ' + param.data[5].toFixed(2);
    //             }
    //         };
    //         boxChartElements.series[0].markLine.data[0].yAxis = boxData[2];
    //         boxChartElements.series[1].data = outliers.map(value => ['数据集', value]);
    //         const boxChart = new Chart('lineChart', null, boxChartElements, new NullColumnStrategy());
    //         boxChart.plot();
    //
    //     })
    //     .catch(error => console.error('配置文件有误:', error));

    // 绘制散点图
    // fetch('../plotCharts/configs/ScatterChart.json')
    //     .then(response => response.json())
    //     .then(scatterChartElements => {
    //         const scatterChart = new Chart('lineChart', multiData, scatterChartElements, new DoubleColumnStrategy());
    //         scatterChart.plot();
    //     })
    //     .catch(error => console.error('配置文件有误:', error));
}

