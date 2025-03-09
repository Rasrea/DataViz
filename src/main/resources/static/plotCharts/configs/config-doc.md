# 📌 ECharts 配置文件说明

## 1️⃣ `title`（标题）
- **`text`**：图表的主标题。
- **`subtext`**：图表的副标题。
- **`left`**：标题的位置，如 `'center'` 表示居中。
- **`textStyle`**：
  - **`fontSize`**：标题文字大小。
  - **`fontWeight`**：标题文字粗细。

## 2️⃣ `xAxis, yAxis`（X 轴，Y 轴）
- **`name`**：坐标轴名称。
- **`nameLocation`**：名称显示位置，`'middle'` 表示居中。
- **`nameTextStyle`**：
  - **`fontWeight`**：坐标轴名称加粗。
  - **`fontSize`**：坐标轴名称字体大小。
- **`min` / `max`**：取值范围，`'dataMin'` / `'dataMax'` 让 ECharts 自动计算。
- **`boundaryGap`**：是否留白，`false` 让折线图从边界开始。
- **`axisTick`**：
  - **`show`**：是否显示刻度线。
- **`splitLine`**：
  - **`show`**：是否显示网格线。
  - **`lineStyle.type`**：网格线样式，例如 `'dashed'`（虚线）或 `'dotted'`（点线）。
- **`axisLine`**：
  - **`show`**：是否显示轴线。
  - **`lineStyle.color`**：轴线颜色。
- **`type`**（仅 `yAxis`）：坐标轴类型，如 `'value'` 表示数值轴。

## 3️⃣ `tooltip`（提示框）
- **`trigger`**：触发方式，`'axis'` 表示在坐标轴触发。
- **`axisPointer`**：
  - **`type`**：指示器类型，`'cross'` 表示十字指示。
  - **`label.backgroundColor`**：指示器标签背景颜色。
- **`formatter`**：自定义提示框内容。

## 4️⃣ `legend`（图例）
- **`show`**：是否显示图例。
- **`top` / `left`**：图例位置。
- **`textStyle.fontSize`**：图例字体大小。

## 5️⃣ `series`（数据系列）
- **`name`**：数据系列的名称。
- **`type`**：图表类型，如 `'line'`（折线图）。
- **`smooth`**：是否平滑曲线。
- **`symbolSize`**：数据点大小。
- **`itemStyle.color`**：折线颜色。
- **`areaStyle.opacity`**：区域填充透明度。
- **`markPoint`**：
  - **`data`**：标记点，例如最大值、最小值。
- **`markLine`**：
  - **`data`**：标记线，例如平均值。
- **`roseType`**：玫瑰图类型，如 `'radius'` 表示半径玫瑰图。
- **`radius`: ['0%', '75%']`**：饼图半径范围，可设置为环形图。

---