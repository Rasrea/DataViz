package com.lucky.data_visual;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        // 创建第一个 ArrayList 并添加一些元素
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("Apple");
        list1.add("Banana");

        // 创建第二个 ArrayList 并添加一些元素
        ArrayList<String> list2 = new ArrayList<>();
        list2.add("Cherry");
        list2.add("Date");

        // 将 list1 中的所有元素添加到 list2 中
        list2.addAll(list1);

        System.out.println("list2: " + list2);
        // 输出：list2: [Cherry, Date, Apple, Banana]
    }
}
