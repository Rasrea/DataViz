import sys
import requests
import json
import numpy as np
import pandas as pd


def request_table_data(SERVERPATH):
    """
    请求后端数据
    :return: 表格数据
    """
    response = requests.get(SERVERPATH)
    json_result = response.json()

    # 读取表格数据
    return pd.DataFrame(json_result['data'])


def fill_values(series, fill_type, n_round=2):
    """
    填充缺失值
    :param series: 该列数据
    :param fill_type: 填充类型
    :param n_round: 小数位
    :return: 填充后的数据列、缺失值个数
    """

    # 填充缺失值
    if fill_type == 'mean':
        series = series.fillna(series.mean()).round(n_round)
    elif fill_type == 'median':
        series = series.fillna(series.median()).round(n_round)
    elif fill_type == 'mode':
        series = series.fillna(series.mode()[0]).round(n_round)
    elif fill_type == 'upperAndLowerMean':  # 上下值的均值
        series = series.fillna((series.shift() + series.shift(-1) / 2)).round(n_round)
    elif fill_type == 'fillUnknown':  # 使用字符串"Unknown"填充
        series = series.replace('', 'Unknown')
    else:
        series = series

    return series, series.isna().sum()


def drop_values(df, col_name):
    """
    若该列中存在空值，则删除该行
    :param df: 完整数据data
    :param col_name: 需要修改的列名
    :return: 新的data
    """
    return df.dropna(subset=[col_name])


def convert_col_type(series, t_col_type):
    """
    将相关数据列转换为特定类型
    :param series: 数据列
    :param t_col_type: 目标类型
    :return: 转换成功的数据列或数据列
    """
    try:
        if t_col_type == 'Number':
            return '0', pd.to_numeric(series)
        if t_col_type == 'String':
            return '0', series.astype(str)
        if t_col_type in ['Nan', 'Date']:
            return '0', series
    except ValueError:
        return '1', series


def read_form_data(form_string, SERVERPATH):
    """
    解析前端传递到表格数据
    :param form_string: 表格数据字符串
    :param SERVERPATH: 后端数据网址
    :return:
    """
    # 操作状态码
    # CODE = 0  # 0: 操作成功, 1: 操作失败

    # 按照表格数据进行操作
    try:
        form_json = json.loads(form_string)
    except json.JSONDecodeError as e:
        print("Received JSON string:", form_string)  # 打印 JSON 字符串
        print("JSONDecodeError:", e)
        raise
    data = request_table_data(SERVERPATH)  # 表格原始数据
    col_name = form_json['colName']  # 预操作的数据列名

    # 转换数据列类型
    CODE, data[col_name] = convert_col_type(data[col_name], form_json['colType'])
    if CODE == '1':
        print('数据类型转换失败！', file=sys.stderr)  # 将错误信息传入 错误流 供Java读取
        sys.exit(1)  # 退出并返回错误代码1，表示错误

    # 判断是否删除缺失值
    if form_json['isDropNa']:
        data = data.replace('', None)
        data = drop_values(data, col_name)

    else:
        data[col_name], _ = fill_values(data[col_name], form_json['fillType'])

    # 将DataFrame转换为JSON格式（记录形式）
    json_data = data.to_dict(orient='records')

    # 输出JSON数据给Java
    print(json.dumps(json_data))


if __name__ == '__main__':
    if sys.argv[1] == 'read_form_data':
        read_form_data(sys.argv[2], sys.argv[3])

    # 后端数据地址，根据实际情况修改
    # SERVERPATH = 'http://localhost:8080/data/fetch-csv'
    # form_string = '{"isDropNa":false,"colName":"Age","colType":"Number","fillType":"upperAndLowerMean"}'
    # read_form_data(form_string, SERVERPATH)
