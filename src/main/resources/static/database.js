const resizer = document.querySelector('.resizer');
const chartArea = document.querySelector('.chartArea');
const databaseList = document.querySelector('.databaseList');
const searchInput = document.getElementById('searchInput');

let isResizing = false;

// 调整宽度功能
resizer.addEventListener('mousedown', (e) => {
    isResizing = true;
    document.body.style.cursor = 'ew-resize';
});

document.addEventListener('mousemove', (e) => {
    if (!isResizing) return;

    const containerWidth = document.querySelector('.container').offsetWidth;
    const newChartWidth = e.clientX;
    const newDatabaseWidth = containerWidth - newChartWidth - resizer.offsetWidth;

    if (newChartWidth > 100 && newDatabaseWidth > 100) {
        chartArea.style.flex = `0 0 ${newChartWidth}px`;
        databaseList.style.flex = `0 0 ${newDatabaseWidth}px`;
    }
});

document.addEventListener('mouseup', () => {
    isResizing = false;
    document.body.style.cursor = 'default';
});

// 折叠/展开功能
const databaseHeaders = document.querySelectorAll('.databaseItem h3');

databaseHeaders.forEach(header => {
    header.addEventListener('click', () => {
        const tableList = header.nextElementSibling;
        const arrow = header.querySelector('.arrow');

        if (tableList.style.display === 'none' || tableList.style.display === '') {
            tableList.style.display = 'block';
            arrow.classList.remove('collapsed');
        } else {
            tableList.style.display = 'none';
            arrow.classList.add('collapsed');
        }
    });
});
