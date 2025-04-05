// 设置图标参数的全局变量
let chart = new Chart('chartContainer');
let jsonData // 后端数据

// 设置绘图策略
const STRATEGY = {
    'SINGLE_COLUMN': 'SINGLE_COLUMN',
    'DOUBLE_COLUMN': 'DOUBLE_COLUMN',
    'MULTI_COLUMN': 'MULTI_COLUMN',
    'NULL_COLUMN': 'NULL_COLUMN'
}

// 设置图表对应的绘图策略
const CHART_TYPE = {
    'LineChart': STRATEGY.MULTI_COLUMN,
    'ScatterChart': STRATEGY.MULTI_COLUMN,
    'BarChart': STRATEGY.MULTI_COLUMN,

    'PieChart': STRATEGY.SINGLE_COLUMN,
    'WordCloudChart': STRATEGY.SINGLE_COLUMN,
}


/**
 * 将 JSON 数据发送到后端
 * @param url
 * @param method
 * @param data
 * @returns {Promise<any|null>}
 */
async function pullJSON(url, method = 'POST', data = null) {
    try {
        let options = {
            method,
            headers: {'Content-Type': 'application/json'}
        };

        if (data) {
            options.body = JSON.stringify(data); // 仅在 POST/PUT 时需要 body
        }

        let response = await fetch(url, options);
        if (!response.ok) {
            console.error(`请求失败: ${response.status}`);
        }

        return await response.json(); // 解析 JSON 响应
    } catch (error) {
        console.error("请求出错:", error);
        return null;
    }
}

/**
 * 显示选项卡
 * @param tabNumber 选项卡编号
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
 * 添加 Y 轴标签，并保存到 sessionStorage
 * @param container 选项容器
 * @param value 对应数据
 * @param text 标签
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
 * 为指定的元素添加事件监听器，并在事件发生时更新图表
 * @param {string[]} elementIds - 元素ID数组
 * @param {string} eventType - 事件类型
 */
function addPlotChartEventListeners(elementIds, eventType) {
    elementIds.forEach(elementId => {
        document.getElementById(elementId).addEventListener(eventType, function () {
            plotChart().then(r => r);
        });
    });
}


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
        }
    });
}

/**
 * 初始化 图表表单配置
 */
function initChartConfig() {
    // tab2
    chart.getChartConfigAfterProcess().series.forEach((seriesItem, index) => {
        // 将各个数据序列命名为对应的 Y 轴标签
        seriesItem.name = chart.getAxisLabels()['yColNames'][index];
    });

    // 根据用户填写的表格更新图像配置信息
    if (CHART_TYPE[chart.getChartType()] === STRATEGY.MULTI_COLUMN) {
        chart.getChartConfigAfterProcess().xAxis.name = document.getElementById('xLabel').value;
        chart.getChartConfigAfterProcess().yAxis.name = document.getElementById('yLabel').value;
        chart.getChartConfigAfterProcess().xAxis.nameTextStyle.fontSize = document.getElementById('xLabelFontSize').value;
        chart.getChartConfigAfterProcess().yAxis.nameTextStyle.fontSize = document.getElementById('yLabelFontSize').value;

        // 如果坐标轴的横坐标是数值型，则控制显示范围
        if (chart.getChartConfigAfterProcess().xAxis.type === 'value') {
            chart.getChartConfigAfterProcess().xAxis.min = 'dataMin';
        }
    }
    chart.getChartConfigAfterProcess().title.text = document.getElementById('chartTitle').value;
    chart.getChartConfigAfterProcess().title.subtext = document.getElementById('chartExplain').value;

    // tab3
    chart.getChartConfigAfterProcess().title.textStyle.fontSize = document.getElementById('titleFontSize').value;
    const roseType = document.getElementById('isPlotRose').value;
    if (roseType) {
        chart.getChartConfigAfterProcess().series[0].roseType = roseType;
    }

}

/**
 * 绘制图表
 * @returns {Promise<void>}
 */
async function plotChart() {
    populateFormWithSavedData([
        'xData', 'yData', 'zData', // tab1
        'chartTitle', 'chartExplain', 'xLabel', 'yLabel', // tab2
        'titleFontSize', 'xLabelFontSize', 'yLabelFontSize',
        'sectorsCount', 'focusCount' // tab3

    ])


    // 将 图表类型 和 绘图策略 传给后端
    const chartType = document.getElementById('chartOptions').value
    chart.setChartType(chartType);
    await fetch(`http://localhost:8080/api/chart/chartConfig?chartType=${chartType}`); // 发送图表类型到后端
    await fetch(`http://localhost:8080/api/chart/columnStrategy?columnStrategy=${CHART_TYPE[chartType]}`); // 发送绘图策略到后端
    await pullJSON('http://localhost:8080/api/chart/axisLabels', 'POST', getAxisLabels()); // 发送 x轴 和 y 轴标签到后端

    // 获取后端的 图表信息
    let sectorsCount = 0;
    if (chartType === 'PieChart') {
        sectorsCount = document.getElementById('sectorsCount').value; // 扇形个数
    } else if (chartType === 'WordCloudChart') {
        sectorsCount = document.getElementById('focusCount').value; // 焦点个数
    }

    // 将表单数据嵌入配置信息
    fetch(`http://localhost:8080/api/chart/chartConfigAfterProcess?seriesCount=${sectorsCount}`)
        .then(response => response.json()).then(chartConfigAfterProcess => {
            if (chartType === 'WordCloudChart') {
                chartConfigAfterProcess.series[0].textStyle.color = function () { // 随机生成颜色
                    return "rgb(" + [Math.round(Math.random() * 160), Math.round(Math.random() * 160), Math.round(Math.random() * 160)].join(",") + ")";
                };
            }

            chart.setChartConfigAfterProcess(chartConfigAfterProcess);
            chart.setAxisLabels(getAxisLabels());
            initChartConfig(); // 初始化图表参数
            chart.plotWithConfig(chart.getChartConfigAfterProcess()); // 绘制图表
        }
    );
}

/**
 * 页面加载时调用的初始化函数
 */
window.onload = async function () {
    // 加载已保存的 Y 轴标签
    const savedYData = JSON.parse(sessionStorage.getItem('selectedYData')) || [];
    const container = document.getElementById("selectedYDataContainer");
    savedYData.forEach(({value, text}) => {
        addYDataItem(container, value, text);
    });

    // 获取 后端数据 和 原始图表数据
    let response = await fetch('http://localhost:8080/data/fetch-csv');
    jsonData = await response.json();

    // 设置表单数据
    createSeriesLabel(jsonData['colTypes']['currentColType']);

    // 绘制图表
    await plotChart();
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

// 根据图表类型，显示不同的表单项
document.getElementById("chartOptions").addEventListener("change", function () {
    let selectedChart = this.value;
    document.querySelectorAll(".chart-specific").forEach(el => {
        let applicableCharts = el.getAttribute("data-chart").split(" ");
        el.style.display = applicableCharts.includes(selectedChart) ? "block" : "none";
    });
});
// 触发一次以初始化界面
document.getElementById("chartOptions").dispatchEvent(new Event("change"));


// 为 tab1, tab2 和 chartOptions 添加事件监听器
addPlotChartEventListeners(['tab1', 'chartOptions'], 'change');
addPlotChartEventListeners(['tab2', 'tab3'], 'input');

// 为指定的元素添加事件监听器，并将值保存到 sessionStorage
addEventListenersToSaveElements(
    [
        'chartTitle', 'chartExplain', 'xLabel', 'yLabel', // tab2
        'titleFontSize', 'xLabelFontSize', 'yLabelFontSize', 'sectorsCount', 'focusCount'  // tab3
    ],
    'input' // 适用于 文本输入框
)
addEventListenersToSaveElements(
    [
        'chartOptions', 'xData', 'zData', // tab1
    ],
    'change' // 适用于 下拉框
)


