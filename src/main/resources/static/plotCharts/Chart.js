// 创建图表类 Chart
class Chart {
    constructor(containerId, chartData, chartElements, strategy) {
        this.containerId = containerId; // 图像位置
        this.chartData = chartData; // 图表数据
        this.chartElements = chartElements; // 图表详细信息
        this.strategy = strategy; // 绘图策略
        this.chart = null; // ECharts 实例
        this.chartConfigAfterProcessing = null; // 处理后的图表配置
    }

    // 初始化 ECharts 实例
    initChart() {
        // this.chart = echarts.init(document.getElementById(this.containerId), 'dark'); // 深色
        this.chart = echarts.init(document.getElementById(this.containerId));
    }

    // 设置图表的细节
    applyChartStyles() {
        this.chartConfigAfterProcessing = this.strategy.applyDataFormat(this.chartData);

        const elements = ['title', 'xAxis', 'yAxis', 'series', 'tooltip', 'legend'];
        elements.forEach(element => {
            if (this.chartElements[element]) {
                if (element === 'series') {
                    this.chartConfigAfterProcessing.series = this.chartConfigAfterProcessing.series ? this.chartConfigAfterProcessing.series.map((seriesItem, _) => {
                        return {...seriesItem, ...this.chartElements.series[0]};
                    }) : this.chartElements.series;
                } else {
                    this.chartConfigAfterProcessing[element] = this.chartConfigAfterProcessing[element] ? {...this.chartConfigAfterProcessing[element], ...this.chartElements[element]} : this.chartElements[element];
                }
            }
        });

        // console.log(this.chartConfigAfterProcessing);
    }

    // 绘图方法
    plot() {
        this.initChart();
        // const option = this.strategy.applyDataFormat(this.chartData);
        this.applyChartStyles();
        this.chart.setOption(this.chartConfigAfterProcessing);
    }

    // 传入 图表参数 绘图
    plotWithConfig(chartConfig) {
        this.initChart();
        this.chart.setOption(chartConfig, true);
    }
}

// 统一绘图策略的基类
class ColumnStrategy {
    // 设计数据表现格式
    applyDataFormat(data) {
        throw new Error('applyChartData 需要在子类中实现！');
    }
}

// 单列数据：适用于分类统计
class SingleColumnStrategy extends ColumnStrategy {
    applyDataFormat(data) {
        return {
            series: [
                {
                    data: data.values.map((value, index) => ({
                        name: data.labels[index],
                        value
                    }))
                }
            ]
        };
    }
}

// 双列数据：适用于双坐标轴
class DoubleColumnStrategy extends ColumnStrategy {
    applyDataFormat(data) {
        return {
            xAxis: {type: 'category', data: data.labels},
            yAxis: {},
            series: [
                {data: data.values}
            ]
        }
    }
}

// 绘制多列数据
class MultiColumnStrategy extends ColumnStrategy {
    applyDataFormat(data) {
        return {
            xAxis: {type: 'category'},
            yAxis: {},
            series: data.values
        };
    }
}

// 特殊数据：全部自定义
class NullColumnStrategy extends ColumnStrategy {
    applyDataFormat(data) {
        return {};
    }
}