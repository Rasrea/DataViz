// 设置图标参数的全局变量
let chartConfigAfterProcessing // 图表参数
let axisData // 坐标轴数据
let axisLabels // 坐标轴标签
let chart // 图表对象
let chartType // 图表类型

/**
 * 创建数据列选项
 * @param currentColType
 */
function createSeriesLabel(currentColType) {
    const labelID = ['xData', 'yData', 'zData'];
    labelID.forEach(elementID => {
        const labelSelect = document.getElementById(elementID);
        if (elementID === 'yData') {
            labelSelect.innerHTML = `<option value="无法选中" class="String-label" style="font-family: '黑体', serif">可选择多个</option>` +
                Object.entries(currentColType).map(([key, value]) => `<option value="${key}" class="${value}-label">${key}</option>`).join('');
        } else {
            labelSelect.innerHTML = Object.entries(currentColType).map(([key, value]) => `<option value="${key}" class="${value}-label">${key}</option>`).join('');
        }
    });
}

/**
 * 显示选项卡
 * @param tabNumber
 */
function showTab(tabNumber) {
    // 隐藏所有tab内容和标签
    document.querySelectorAll('.tab-content').forEach(function (tab) {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.scene-item').forEach(function (scene) {
        scene.classList.remove('active');
    })

    // 显示选择的tab内容和标签
    document.getElementById('tab' + tabNumber).classList.add('active');
    document.getElementById('scene' + tabNumber).classList.add('active');
}

/**
 * 获取 x轴 和 y 轴标签的列表
 * @returns {{xColName: *, yColNames: *[]}}
 */
function getAxisLabels() {
    // 获取所有 selected-y-item 元素
    let selectedItems = document.querySelectorAll('.selected-y-item');

    // 创建存储 x轴 和 y 轴标签的列表
    const xColName = document.getElementById('xData').value;
    let yColNames = [];

    // 遍历每个元素，获取 data-value 属性值并放入列表中
    selectedItems.forEach(item => {
        yColNames.push(item.getAttribute('data-value'));
    });

    return {
        'xColName': xColName,
        'yColNames': yColNames
    }
}

/**
 * 获取 x轴 和 y 轴标签的数据
 * @param axisLabels
 * @returns {Promise<{labels: *[], values: *[]}|{labels: *, values: *[]}>}
 */
async function getAxisData(axisLabels) {
    // 读取 X 和 Y 轴标签
    const xColName = axisLabels['xColName'];
    const yColNames = axisLabels['yColNames'];

    try {
        // 读取数据
        const response = await fetch('http://localhost:8080/data/fetch-csv');
        const data = await response.json();
        const fileData = data['data'];
        const xData = fileData.map(row => row[xColName]);
        const yData = yColNames.map(yColName => fileData.map(row => row[yColName]));

        return {
            'labels': xData,
            'values': yData
        };
    } catch (error) {
        console.error('数据读取失败：', error);
        return {
            'labels': [],
            'values': []
        };
    }
}

/**
 * 对 axisData 进行检查并排序
 * @param axisData
 * @param chartConfig
 * @returns {{labels: *[], values: *[][]}}
 */
function sortAxisData(axisData, chartConfig) {
    // 合并 X 轴和 Y 轴数据
    let rowAxisData = axisData.labels.map((value, index) => [value, ...axisData.values.map(arr => arr[index])]);

    if (Array.isArray(axisData.labels) && axisData.labels.every(item => typeof item === 'number')) {
        // 如果 X 轴数据是数值，则修改数据轴
        chartConfig.xAxis.type = 'value';

        // 根据 X 轴数据排序
        rowAxisData.sort((a, b) => a[0] - b[0]);
    }
    return {
        'labels': rowAxisData.map(row => row[0]),
        'values': rowAxisData
    }
}

/**
 * 将 axisValues 转换为适合绘图的格式
 * @param axisValues
 * @returns {*}
 */
function splitAxisValues(axisValues) {
    // 生成 rowData，相当于 Python 的列表推导式
    const rowData = axisValues.map(item => item.slice(1).map(y => [item[0], y]));

    // 转置 rowData（Python zip 逻辑）
    return rowData[0].map((_, colIndex) => ({
        data: rowData.map(row => row[colIndex])
    }))
}

/**
 * 初始化 图表参数
 */
function initChartConfig() {
    chartConfigAfterProcessing.series.forEach((seriesItem, index) => {
        seriesItem.name = axisLabels['yColNames'][index];
    }); // 将各个数据序列命名为对应的 Y 轴标签
    chartConfigAfterProcessing.title.text = document.getElementById('chartTitle').value;
    chartConfigAfterProcessing.title.subtext = document.getElementById('chartExplain').value;
    chartConfigAfterProcessing.xAxis.name = document.getElementById('yLabel').value;
    chartConfigAfterProcessing.yAxis.name = document.getElementById('yLabel').value;
}

/**
 * 绘制图表
 * @returns {Promise<void>}
 */
async function plotChart() {
    // 读取 坐标轴标签 和 数据
    axisLabels = getAxisLabels();
    axisData = await getAxisData(axisLabels);

    // 读取图表类型
    chartType = document.getElementById('chartOptions').value;

    // 绘制相关图表
    fetch(`plotCharts/configs/${chartType}.json`)
        .then(response => response.json())
        .then(chartConfig => {
            // 转化 axisData 数据格式
            axisData = sortAxisData(axisData, chartConfig);
            axisData.values = splitAxisValues(axisData.values);

            // 创建 绘图对象 并设置 图表参数
            chart = new Chart('chartContainer', axisData, chartConfig, new MultiColumnStrategy());
            chart.applyChartStyles()
            chartConfigAfterProcessing = chart.chartConfigAfterProcessing;

            initChartConfig() // 初始化 图像参数

            chart.plotWithConfig(chartConfigAfterProcessing);
        });
}

/**
 * 添加 Y 轴标签，并保存到 sessionStorage
 * @param container
 * @param value
 * @param text
 */
function addYDataItem(container, value, text) {
    let item = document.createElement("div");
    item.className = "selected-y-item";
    item.setAttribute("data-value", value);
    item.innerHTML = `${text} <button type="button" class="remove-btn">❌</button>`;

    // 绑定删除事件
    item.querySelector(".remove-btn").addEventListener("click", function () {
        item.remove();
        // 从 sessionStorage 中移除
        const savedYData = JSON.parse(sessionStorage.getItem('selectedYData')) || [];
        const updatedYData = savedYData.filter(data => data.value !== value);
        sessionStorage.setItem('selectedYData', JSON.stringify(updatedYData));

        // 调用 plotChart 函数更新图表
        plotChart().then(r => r);
    });

    container.appendChild(item);
}

/**
 * 当 chartOptions, xData, yData, selectedYDataContainer, zData
 * 任何一个元素发生变化时，重新绘制图像
 */
function addEventListenersToPlot(eventType, elements, plot) {
    elements.forEach(elementId => {
        document.getElementById(elementId).addEventListener(eventType, plot);
    });
}

/**
 * 为 elements 中的元素添加 eventType 事件监听器，当事件发生时将元素的值保存到 sessionStorage
 * @param elements
 * @param eventType
 */
function addEventListenersToSaveElements(elements, eventType) {
    elements.forEach(elementId => {
        document.getElementById(elementId).addEventListener(eventType, function () {
            const selectedValue = this.value;
            sessionStorage.setItem(elementId, selectedValue);
        });
    });
}

/**
 * 读取 sessionStorage 中的数据，填充到表单中
 */
function populateFormWithSavedData(elements) {
    elements.forEach(elementId => {
        const savedValue = sessionStorage.getItem(elementId);
        if (savedValue) {
            document.getElementById(elementId).value = savedValue;

            console.log(savedValue);
        }
    });
}

/**
 * 页面加载时调用的初始化函数
 */
window.onload = function () {
    // 加载已保存的 Y 轴标签
    const savedYData = JSON.parse(sessionStorage.getItem('selectedYData')) || [];
    const container = document.getElementById("selectedYDataContainer");
    savedYData.forEach(({value, text}) => {
        addYDataItem(container, value, text);
    });

    // 获取后端数据
    fetch('http://localhost:8080/data/fetch-csv')
        .then(response => response.json())
        .then(data => {
            createSeriesLabel(data['colTypes']['currentColType']); // 创建坐标轴标签选型
            populateFormWithSavedData(
                [
                    'chartOptions', 'xData', 'zData',
                    'chartTitle', 'chartExplain', 'xLabel', 'yLabel'
                ]
            ); // 填充表单
            showTab(1); // 默认显示界面一：数据列选择
            plotChart().then(r => r); // 绘图
        })
        .catch(error => {
            console.error('数据读取失败：', error)
        });
}

// 选择多个Y轴标签
document.getElementById("yData").addEventListener("change", function () {
    let select = document.getElementById("yData");
    let selectedValue = select.value;
    let selectedText = select.options[select.selectedIndex].text;
    let container = document.getElementById("selectedYDataContainer");

    if (!selectedValue || selectedValue === "无法选中") {
        alert("请选择一个 Y 轴数据！");
        return;
    }

    // 避免重复添加
    if (document.querySelector(`#selectedYDataContainer [data-value="${selectedValue}"]`)) {
        alert("该列已添加！");
        return;
    }

    // 创建新的Y轴标签
    addYDataItem(container, selectedValue, selectedText);

    // 保存到 sessionStorage
    const savedYData = JSON.parse(sessionStorage.getItem('selectedYData')) || [];
    savedYData.push({value: selectedValue, text: selectedText});
    sessionStorage.setItem('selectedYData', JSON.stringify(savedYData));
});

addEventListenersToSaveElements(
    [
        'chartOptions', 'xData', 'zData',
        'chartTitle', 'chartExplain', 'xLabel', 'yLabel'
    ],
    'change'
)

addEventListenersToPlot(
    'change',
    ['chartOptions', 'xData', 'yData', 'zData', 'selectedYDataContainer'],
    plotChart
);

/**
 * 当 tab2 中的表单元素发生变化时，更新图表
 */
document.getElementById('tab2').addEventListener('input', function () {
    // 获取表单中的数据并更新 JSON
    chartConfigAfterProcessing.title.text = document.getElementById('chartTitle').value;
    chartConfigAfterProcessing.title.subtext = document.getElementById('chartExplain').value;
    chartConfigAfterProcessing.xAxis.name = document.getElementById('xLabel').value;
    chartConfigAfterProcessing.yAxis.name = document.getElementById('yLabel').value;

    // 重新绘制图表
    chart.plotWithConfig(chartConfigAfterProcessing);
});
