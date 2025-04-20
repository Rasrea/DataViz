// 获取JSON数据并创建表格
function fetchDataAndCreateTable() {
    // 显示“请稍等”消息
    const loadingMessage = document.getElementById("loadingMessage");
    loadingMessage.style.display = "block";

    fetch('http://localhost:8080/data/fetch-csv') // 获取样本数据集
        .then(response => response.json())
        .then(data => {
            createCsvTable(data["data"], data["colTypes"]) // 创建 CSV 表格
            loadingMessage.style.display = "none"; // 隐藏“请稍等”消息
        })
        .catch(error => {
            console.error("操作失败：", error)
            loadingMessage.style.display = "none"; // 隐藏“请稍等”消息
        });
}

// 创建 CSV 表格的函数
function createCsvTable(csvData, colTypes) {
    const headerRow = document.getElementById("checkCsvHeaderRow");
    const body = document.getElementById("checkCsvBody");

    // 获取数据列类型：当前类型
    const currentColType = colTypes["currentColType"];

    // 清空之前的内容
    headerRow.innerHTML = body.innerHTML = "";

    // 创建表头
    const headers = Object.keys(csvData[0]);
    headerRow.innerHTML = `<th id="tableIndex">#</th>` + headers.map((key) => `<th><button id="${currentColType[key]}" class="header-button" onclick="handleHeaderClick('${key}')">${key}</button></th>`).join('');

    // 创建表格数据行
    body.innerHTML = csvData.slice(0, 250).map((row, rowIndex) => {
        return `
            <tr>
                <td class="row-index" title="${rowIndex + 1}">${rowIndex + 1}</td>
                ${Object.entries(row).map(([key, value]) =>
            `<td id="${currentColType[key]}" class="tooltip-cell" title="${value}">${value}</td>`
        ).join('')}
            </tr>
        `;
    }).join('');

    // 计算行数和列数
    const rowCount = csvData.length;
    const colCount = headers.length;

    // 创建显示行数和列数的元素
    const tableShape = document.getElementById("checkTableShape");
    tableShape.textContent = `${rowCount} rows x ${colCount} cols`;
}

// 定义点击表头的处理函数
function handleHeaderClick(columnKey) {
    console.log(`Column ${columnKey} clicked`);
    // 打开一个新窗口，并传递列名作为查询参数
    // window.open(`operation.html?column=${columnKey}`);
    window.location.href = `operation.html?column=${columnKey}`;
}

// 下载逻辑
function downloadTable() {
    // 请求后端数据
    fetch('http://localhost:8080/data/fetch-csv')
        .then(response => response.json()) // 将响应数据解析为 JSON
        .then(data => {
            // 将 JSON 数据转换为 CSV
            let csv = jsonToCSV(data.data);

            // 添加 UTF-8 BOM
            const BOM = '\uFEFF';
            csv = BOM + csv;  // 在 CSV 数据的开始添加 BOM

            // 创建 Blob 对象
            let blob = new Blob([csv], {type: 'text/csv;charset=utf-8;'});

            // 创建下载链接并触发下载
            let link = document.createElement('a');
            if (link.download !== undefined) {
                let url = URL.createObjectURL(blob);
                link.setAttribute('href', url);
                link.setAttribute('download', 'data.csv');
                link.style.visibility = 'hidden';

                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
            }
        })
        .catch(error => {
            console.error('请求失败：', error);
        });
}

// 将 JSON 数据转换为 CSV 格式的函数
function jsonToCSV(jsonData) {
    const headers = Object.keys(jsonData[0]);
    const csvRows = [];

    // 添加表头
    csvRows.push(headers.join(','));

    // 添加数据行
    jsonData.forEach(row => {
        const values = headers.map(header => row[header]);
        csvRows.push(values.join(','));
    });

    return csvRows.join('\n');
}

// 初始化表格数据
function initializeTable() {
    // 发送请求到后端的初始化接口
    fetch('http://localhost:8080/api/csv/initData', {
        method: 'POST'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('初始化失败');
            } else {
                window.location.reload(); // 刷新页面
            }
        })
        .catch(error => {
            console.error('操作失败：', error);
        });
}

// 根据数据类型格式化值
window.onload = function () {
    fetchDataAndCreateTable();
};

window.addEventListener('DOMContentLoaded', function () {
    let params = new URLSearchParams(window.location.search);
    if (params.get('refresh') === 'true') {
        location.reload(); // 刷新页面
        // 刷新完成后将 URL 恢复成原来的样子
        window.location = 'check.html';
    }
});

