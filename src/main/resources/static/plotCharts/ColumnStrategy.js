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