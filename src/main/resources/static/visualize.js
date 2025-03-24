// 设置图标参数的全局变量
let chart = new Chart('chartContainer');
let jsonData // 后端数据

const PlotStrategy = {
    'SINGLE_COLUMN': 'SINGLE_COLUMN',
    'DOUBLE_COLUMN': 'DOUBLE_COLUMN',
    'MULTI_COLUMN': 'MULTI_COLUMN',
    'NULL_COLUMN': 'NULL_COLUMN'
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
            throw new Error(`请求失败: ${response.status}`);
        }

        return await response.json(); // 解析 JSON 响应
    } catch (error) {
        console.error("请求出错:", error);
        return null;
    }
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
 * 根据用户填写的表格更新图像配置信息
 */
function updateChartConfigAfterProcessing() {
    chart.getChartConfigAfterProcess().title.text = document.getElementById('chartTitle').value;
    chart.getChartConfigAfterProcess().title.subtext = document.getElementById('chartExplain').value;
    chart.getChartConfigAfterProcess().xAxis.name = document.getElementById('xLabel').value;
    chart.getChartConfigAfterProcess().yAxis.name = document.getElementById('yLabel').value;
}

/**
 * 初始化 图表参数
 */
function initChartConfig() {
    chart.getChartConfigAfterProcess().series.forEach((seriesItem, index) => {
        seriesItem.name = chart.getAxisLabels()['yColNames'][index];
    }); // 将各个数据序列命名为对应的 Y 轴标签

    updateChartConfigAfterProcessing();
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
 * 绘制图表
 * @returns {Promise<void>}
 */
async function plotChart() {
    populateFormWithSavedData([
        'xData', 'yData', 'zData',
        'chartTitle', 'chartExplain', 'xLabel', 'yLabel'
    ])

    // 将前端选择的坐标轴标签发送到后端
    pullJSON('http://localhost:8080/api/chart/axisLabels', 'POST', getAxisLabels());
    // 将前端选择的图表类型发送到后端
    const chartType = document.getElementById('chartOptions').value
    const isReadChartConfig = await fetch(`http://localhost:8080/api/chart/chartConfig?chartType=${chartType}`);
    if (!isReadChartConfig.ok) {
        throw new Error(`HTTP 错误! 状态: ${isReadChartConfig.status}`);
    }

    // 指定 绘图策略
    if (chartType === 'LineChart') {
        await fetch(`http://localhost:8080/api/chart/columnStrategy?columnStrategy=${PlotStrategy.MULTI_COLUMN}`);
    }

    // 获取 后端的图表信息
    fetch('http://localhost:8080/api/chart/chartConfigAfterProcess')
        .then(response => response.json()).then(chartConfigAfterProcess => {
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

addEventListenersToSaveElements(
    [
        'chartTitle', 'chartExplain', 'xLabel', 'yLabel',
    ],
    'input' // 适用于 文本输入框
)
addEventListenersToSaveElements(
    [
        'chartOptions', 'xData', 'zData',
    ],
    'change' // 适用于 下拉框
)

/**
 * 当 tab1 表单发生变化时更新图表
 */
document.getElementById('tab1').addEventListener('change', function () {
    plotChart().then(r => r);
})

/**
 * 当 tab2 中的表单元素发生变化时，更新图表
 */
document.getElementById('tab2').addEventListener('input', function () {
    plotChart().then(r => r);
});

/**
 * 当 chartOptions 发生变化时，重新绘制图表
 */
document.getElementById('chartOptions').addEventListener('change', async function () {
    // 绘制图表
    await plotChart();
});
