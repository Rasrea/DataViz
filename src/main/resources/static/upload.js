// 上传文件并获取数据
function uploadAndFetchData() {
    const file = document.getElementById("fileInput").files[0]; // 获取上传的文件

    // 检查文件后缀
    const fileExtension = file.name.split('.').pop().toLowerCase();

    if (!file) {
        alert("请先选择文件！");
        return;
    }

    // 显示“请稍等”消息
    const loadingMessage = document.getElementById("loadingMessage");
    loadingMessage.style.display = "block";

    // 创建 FormData 对象并附加文件
    const formData = new FormData();
    formData.append("file", file);

    // 根据文件后缀判断请求接口
    let apiUrl;
    if (fileExtension === 'csv') {
        const excelSelect = document.getElementById("excelDataSelect");
        excelSelect.style.display = "none";
        apiUrl = "http://localhost:8080/api/csv/upload"; // CSV 文件上传接口
    } else if (fileExtension === 'xls' || fileExtension === 'xlsx') {
        apiUrl = "http://localhost:8080/api/excel/upload"; // Excel 文件上传接口
    } else {
        alert("不支持的文件类型！");
        return;
    }

    // 上传文件到后端并获取数据
    fetch(apiUrl, {
        method: "POST",
        body: formData,
    })
        .then((response) => {
            if (!response.ok) throw new Error("上传失败");
            return fetch("http://localhost:8080/data/fetch-csv"); // 上传成功后获取数据
        })
        .then(response => response.json())
        .then(data => {
            createTable(data.data); // 创建 CSV 表格
            loadingMessage.style.display = "none"; // 隐藏“请稍等”消息
            if (fileExtension === 'xls' || fileExtension === 'xlsx') {
                createExcelSelect(); // 更新 Excel 数据选择框
            }
        })
        .catch(error => {
            console.error("操作失败：", error);
            loadingMessage.style.display = "none"; // 隐藏“请稍等”消息
        });
}

// 创建Excel数据选择框
function createExcelSelect() {
    // 获取表格列表名称
    fetch('http://localhost:8080/api/excel/sheetNames')
        .then(response => response.json())
        .then(sheetNames => {
            const selectElement = document.getElementById("excelDataSelect");
            selectElement.innerHTML = ""; // 清空之前的选项
            selectElement.style.display = "flex";

            sheetNames.forEach(sheetName => {
                const option = document.createElement("option");
                option.value = sheetName;
                option.text = sheetName;
                selectElement.appendChild(option);
            });
        })
        .catch(error => {
            console.error('获取表格列表名称失败：', error);
        });
}

// 创建表格
function createTable(tableData) {
    const headerRow = document.getElementById("csvHeaderRow");
    const body = document.getElementById("csvBody");

    // 清空之前的内容
    headerRow.innerHTML = body.innerHTML = "";

    // 创建表头
    const headers = Object.keys(tableData[0]);
    headerRow.innerHTML = headers.map(key => `<th>${key}</th>`).join('');

    // 创建表格数据行
    body.innerHTML = tableData.slice(0, 250).map(row => {
        return `<tr>${Object.values(row).map(value => `<td>${value}</td>`).join('')}</tr>`;
    }).join('');

    // 计算行数和列数
    const rowCount = tableData.length;
    const colCount = headers.length;

    // 创建显示行数和列数的元素
    const tableShape = document.getElementById("table-shape");
    tableShape.textContent = `${rowCount} rows x ${colCount} cols`
}

// 选择样本数据
function selectSampleData(selectValue) {
    switch (selectValue) {
        case 'douban-books':
            fetch('http://localhost:8080/data/douban-books') // 获取样本数据集
                .then(response => response.json())
                .then(date => createTable(date.data))// cao！应该是data，拼成date了！
                .catch(error => console.error("操作失败：", error));
            break;
        case 'maoyan-films':
            fetch('http://localhost:8080/data/maoyan-films') // 获取样本数据集
                .then(response => response.json())
                .then(date => createTable(date.data))
                .catch(error => console.error("操作失败：", error));
            break;
        case 'metacritic-games':
            fetch('http://localhost:8080/data/metacritic-games') // 获取样本数据集
                .then(response => response.json())
                .then(date => createTable(date.data))
                .catch(error => console.error("操作失败：", error));
            break;
        case 'random-data':
            fetch('http://localhost:8080/data/random-data') // 获取样本数据集
                .then(response => response.json())
                .then(date => createTable(date.data))
                .catch(error => console.error("操作失败：", error));
            break;
        default:
            alert("请选择一个有效的数据集");
    }
}

// 获取指定Excel数据表
function selectExcelData(sheetName) {
    fetch(`http://localhost:8080/api/excel/data?sheetName=${sheetName}`)
        .then(response => response.json())
        .then(data => createTable(data.data))
        .catch((error => console.error(`${sheetName}表读取失败：`, error)));
}

// 等待页面加载完成后执行
window.onload = function () {
    document.getElementById("fileInput").addEventListener("change", uploadAndFetchData);

    // 根据 样本标签 实时修改数据表
    const datasetSelect = document.getElementById("demoDataset");
    datasetSelect.addEventListener("change", function (event) {
        const sampleValue = event.target.value;  // 获取选中的值
        selectSampleData(sampleValue);  // 执行相应的操作
    });

    // 根据 Excel标签 实时修改数据表
    const excelDataSelect = document.getElementById("excelDataSelect")
    excelDataSelect.addEventListener("change", function (event) {
        const excelValue = event.target.value;
        selectExcelData(excelValue);
    })
}