// 创建图表类 Chart
class Chart {
    constructor(containerId) {
        this.containerId = containerId; // 图像位置

        this.chartConfig = null; // 图表默认配置信息
        this.strategy = null; // 绘图策略
        this.eChart = null; // ECharts 实例
        this.axisData = null; // 坐标轴数据
        this.axisLabels = null; // 坐标轴标签
        this.chartConfigAfterProcessing = null; // 处理后的图表配置
        this.chartType = null; // 图表类型
    }

    // 初始化 ECharts 实例
    initChart() {
        // this.chart = echarts.init(document.getElementById(this.containerId), 'dark'); // 深色
        this.eChart = echarts.init(document.getElementById(this.containerId));
    }

    // 设置图表的细节
    applyChartStyles() {
        this.chartConfigAfterProcessing = this.strategy.applyDataFormat(this.axisData);

        const elements = ['title', 'xAxis', 'yAxis', 'series', 'tooltip', 'legend'];
        elements.forEach(element => {
            if (this.chartConfig[element]) {
                if (element === 'series') {
                    this.chartConfigAfterProcessing.series = this.chartConfigAfterProcessing.series ? this.chartConfigAfterProcessing.series.map((seriesItem, _) => {
                        return {...seriesItem, ...this.chartConfig.series[0]};
                    }) : this.chartConfig.series;
                } else {
                    this.chartConfigAfterProcessing[element] = this.chartConfigAfterProcessing[element] ? {...this.chartConfigAfterProcessing[element], ...this.chartConfig[element]} : this.chartConfig[element];
                }
            }
        });
    }

    // 绘图方法
    plot() {
        this.initChart();
        // const option = this.strategy.applyDataFormat(this.axisData);
        this.applyChartStyles();
        this.eChart.setOption(this.chartConfigAfterProcessing);
    }

    // 传入 图表参数 绘图
    plotWithConfig(chartConfig) {
        this.initChart();
        this.eChart.setOption(chartConfig, true);
    }

    // Getter and Setter
    getChartConfigAfterProcessing() {
        return this.chartConfigAfterProcessing;
    }

    setChartConfigAfterProcessing(chartConfigAfterProcessing) {
        this.chartConfigAfterProcessing = chartConfigAfterProcessing;
    }

    getAxisData() {
        return this.axisData;
    }

    setAxisData(axisData) {
        this.axisData = axisData;
    }

    getAxisLabels() {
        return this.axisLabels;
    }

    setAxisLabels(axisLabels) {
        this.axisLabels = axisLabels;
    }

    getChartType() {
        return this.chartType;
    }

    setChartType(chartType) {
        this.chartType = chartType;
    }

    getContainerId() {
        return this.containerId;
    }

    setContainerId(containerId) {
        this.containerId = containerId;
    }

    getStrategy() {
        return this.strategy;
    }

    setStrategy(strategy) {
        this.strategy = strategy;
    }

    getChartConfig() {
        return this.chartConfig;
    }

    setChartConfig(chartConfig) {
        this.chartConfig = chartConfig;
    }
}
