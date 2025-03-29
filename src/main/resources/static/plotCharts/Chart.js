// 创建图表类 Chart
class Chart {
    constructor(containerId) {
        this.containerId = containerId; // 图像位置
        this.axisLabels = null; // 坐标轴标签
        this.chartConfigAfterProcess = null; // 处理后的图表配置
        this.eChart = null; // ECharts 实例
        this.chartType = null; // 图表类型
    }

    // 初始化 ECharts 实例
    initChart() {
        // this.chart = echarts.init(document.getElementById(this.containerId), 'dark'); // 深色
        this.eChart = echarts.init(document.getElementById(this.containerId));
    }

    // 绘图方法
    plot() {
        this.initChart();
        this.eChart.setOption(this.chartConfigAfterProcess);
    }

    // 传入 图表参数 绘图
    plotWithConfig(chartConfig) {
        this.initChart();
        this.eChart.setOption(chartConfig, true);
    }

    // Getter and Setter
    getContainerId() {
        return this.containerId;
    }

    setContainerId(containerId) {
        this.containerId = containerId;
    }

    getChartConfigAfterProcess() {
        return this.chartConfigAfterProcess;
    }

    setChartConfigAfterProcess(chartConfigAfterProcess) {
        this.chartConfigAfterProcess = chartConfigAfterProcess;
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
}
