// 统计数据中空值和非空值的个数
function countNullValues(data) {
    const totalCount = data.length;
    const missingCount = data.filter(item => item === "").length;
    return {
        totalCount,
        missingCount,
        nonMissingCount: totalCount - missingCount
    };
}

// 统计前K项词频，之后合并为"others"
function TopKWordFrequency(words, K) {
    const wordCount = {};

    // 统计每个词的频率
    words.forEach(word => {
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

    // 转为相应的键值对
    const result = {}
    topK.forEach(([word, count]) => {
        result[word] = count;
    })

    return result;
}

// 计算四分位数和上下须
function calculateQuartiles(data) {
    const Q1 = getPercentile(data, 25);
    const Q2 = getPercentile(data, 50);
    const Q3 = getPercentile(data, 75);
    const IQR = Q3 - Q1;
    const lowerWhisker = Math.max(Q1 - 1.5 * IQR, data[0]);
    const upperWhisker = Math.min(Q3 + 1.5 * IQR, data[data.length - 1]);
    return [lowerWhisker, Q1, Q2, Q3, upperWhisker];
}

// 计算百分位数
function getPercentile(data, percentile) {
    const index = (percentile / 100) * (data.length - 1);
    if (Number.isInteger(index)) {
        return data[index];
    } else {
        const lower = Math.floor(index);
        const upper = Math.ceil(index);
        return (data[lower] + data[upper]) / 2;
    }
}

// 计算离群点
function calculateOutliers(data, lowerWhisker, upperWhisker) {
    return data.filter(value => value < lowerWhisker || value > upperWhisker);
}
