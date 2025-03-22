package com.lucky.data_visual;


import com.lucky.data_visual.enums.ColumnStrategy;

public class Test {
    public static void main(String[] args) {
        String columnStrategy = "singleColumn";
        com.lucky.data_visual.enums.ColumnStrategy strategy = ColumnStrategy.valueOf(columnStrategy);
        System.out.println(strategy);
    }
}
