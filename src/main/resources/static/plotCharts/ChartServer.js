class ChartServer {

    // 统计数据列中空值和非空值的个数
    countNullValues(colData) {
        const totalCount = colData.length;
        const nullCount = colData.filter(item => item === "").length;
        return {
            labels: ['空值', '非空值'],
            values: [nullCount, totalCount - nullCount],
        }
    }

    // 统计前K项词频，之后合并为"others"
    topKWordFrequency(colData, K) {
        const wordCount = {};

        // 统计每个词的频率
        colData.forEach(word => {
            wordCount[word] = wordCount[word] ? wordCount[word] + 1 : 1;
        });

        // 将频率对象转换为数组并排序
        const sortedFrequencyArray = Object.entries(wordCount).sort((a, b) => b[1] - a[1]);

        // 保留前 K 项，其他项合并为“其他”
        const topK = sortedFrequencyArray.slice(0, K);
        const others = sortedFrequencyArray.slice(K);

        const otherCount = others.reduce((sum, item) => sum + item[1], 0);
        if (otherCount > 0) {
            topK.push(['others', otherCount]);
        }

        // 转为对应格式的键值对
        return {
            labels: topK.map(item => item[0]),
            values: topK.map(item => item[1])
        };
    }

    // 将该列转为 labels 为序号，values 为值的格式
    convertSingleColumnDataToLabelsAndValues(colData) {
        return {
            labels: Array.from({length: colData.length}, (_, i) => i),
            values: colData,
        }
    }

    // 计算百分位数
    getPercentile(numbers, percentile) {
        const index = (percentile / 100) * (numbers.length - 1);
        if (Number.isInteger(index)) {
            return numbers[index];
        } else {
            const lower = Math.floor(index);
            const upper = Math.ceil(index);
            return (numbers[lower] + numbers[upper]) / 2;
        }
    }

    // 计算四分位数和上下须
    calculateQuartiles(numbers) {
        const Q1 = this.getPercentile(numbers, 25);
        const Q2 = this.getPercentile(numbers, 50);
        const Q3 = this.getPercentile(numbers, 75);
        const IQR = Q3 - Q1;
        const lowerWhisker = Math.max(Q1 - 1.5 * IQR, numbers[0]);
        const upperWhisker = Math.min(Q3 + 1.5 * IQR, numbers[numbers.length - 1]);
        return [lowerWhisker, Q1, Q2, Q3, upperWhisker];
    }

    // 计算离群点
    calculateOutliers(numbers, lowerWhisker, upperWhisker) {
        return numbers.filter(value => value < lowerWhisker || value > upperWhisker);
    }
}