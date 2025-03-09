// 用于初始化表单和设置事件监听器
function initializeForm(itemDict) {
    Object.entries(itemDict).forEach(([key, type]) => {
        // 获取 SessionStorage 中保存的选项
        const data = sessionStorage.getItem(key);
        // 如果有保存的数据，更新表单的选项
        if (data) {
            document.getElementById(key).value = data;
        }

        // 在用户选择或输入时，保存数据到 SessionStorage
        document.getElementById(key).addEventListener(type === 'textarea' ? 'input' : type, function () {
            sessionStorage.setItem(key, this.value);
        });
    });
}

// 创建数据列选项
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

// 显示指定的页面
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

// 自动生成 itemDict 对象
function generateItemDict() {
    const itemDict = {};
    document.querySelectorAll('form input, form select, form textarea').forEach(element => {
        const id = element.id;
        if (id) {
            if (element.tagName.toLowerCase() === 'textarea') {
                itemDict[id] = 'input';
            } else if (element.tagName.toLowerCase() === 'select') {
                itemDict[id] = 'change';
            } else {
                itemDict[id] = 'input';
            }
        }
    });
    return itemDict;
}

// 获取 x轴 和 y 轴标签的列表
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

// 获取 x轴 和 y 轴标签的数据
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

// 是否对 axisData 的X轴进行排序
// 检查并排序数组
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

// 将 原始axis.values ：
// [['a', 1, 2, 12], ['b', 3, 4, 34], ['c', 5, 6, 56]]
// 分解为[
//   {data: [['a', 1], ['a', 2], ['a', 12]]},
//   {data: [['b', 3], ['b', 4], ['b', 34]]},
//   {data: [['c', 5], ['c', 6], ['c', 56]]}
// ]
function splitAxisValues(axisValues) {
    // 生成 rowData，相当于 Python 的列表推导式
    const rowData = axisValues.map(item => item.slice(1).map(y => [item[0], y]));

    // 转置 rowData（zip 逻辑）
    return rowData[0].map((_, colIndex) => ({
        data: rowData.map(row => row[colIndex])
    }))
}

// 绘图函数
async function plotChart() {
    // 读取坐标轴标签和数据
    const axisLabels = getAxisLabels();
    let axisData = await getAxisData(axisLabels);

    // 读取图表类型
    const chartType = document.getElementById('chartOptions').value;
    console.log(chartType);

    // 绘制相关图表
    fetch(`plotCharts/configs/${chartType}.json`)
        .then(response => response.json())
        .then(chartConfig => {
            axisData = sortAxisData(axisData, chartConfig);
            console.log(axisData);

            axisData.values = splitAxisValues(axisData.values);
            console.log(axisData);

            const chart = new Chart('chartContainer', axisData, chartConfig, new MultiColumnStrategy());
            chart.plot();
        });
}

// 页面加载时调用初始化函数
window.onload = function () {
    // 定义表单参数
    const itemDict = generateItemDict();

    // 加载已保存的 Y 轴标签
    const savedYData = JSON.parse(sessionStorage.getItem('selectedYData')) || [];
    const container = document.getElementById("selectedYDataContainer");
    savedYData.forEach(({value, text}) => {
        addYDataItem(container, value, text);
    });

    // 设置 chartOptions 的默认值
    const savedChartOption = sessionStorage.getItem('chartOptions');
    if (savedChartOption) {
        document.getElementById('chartOptions').value = savedChartOption;
    }

    fetch('http://localhost:8080/data/fetch-csv')
        .then(response => response.json())
        .then(data => {
            // 创建数据列选项
            createSeriesLabel(data['colTypes']['currentColType']); // 创建坐标轴标签选型
            initializeForm(itemDict); // 保存表格参数，防止刷新丢失
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

// 监听 chartOptions 的变化并存储到 sessionStorage
document.getElementById('chartOptions').addEventListener('change', function () {
    const selectedChart = this.value;
    sessionStorage.setItem('chartOptions', selectedChart);
});

// 监听 chartOptions, xData, yData, zData 的变化
document.getElementById('chartOptions').addEventListener('change', plotChart);
document.getElementById('xData').addEventListener('change', plotChart);
document.getElementById('yData').addEventListener('change', plotChart);
document.getElementById('zData').addEventListener('change', plotChart);

// 添加 Y 轴标签的函数
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
    });

    container.appendChild(item);
}
