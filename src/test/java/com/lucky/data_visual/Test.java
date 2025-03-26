package com.lucky.data_visual;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<Object> list = List.of(1, 2, 3.1, 4.2, 5);
        List<Number> numberList = list.stream()
                                      .filter(e -> e instanceof Number)
                                      .map(e -> (Number) e)
                                      .toList();

        double sum = numberList.stream()
                               .mapToDouble(Number::doubleValue)
                               .sum();
        double average = sum / numberList.size();

        System.out.println("Average: " + average);
    }
}