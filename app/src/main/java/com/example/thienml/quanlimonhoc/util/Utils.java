package com.example.thienml.quanlimonhoc.util;

public class Utils {
    public static Integer strToInt(String str, Integer defaultValue) {
        Integer value = defaultValue;
        try {
            value = Integer.parseInt(str);
        } catch (Exception e) {
        }
        return value;
    }
}
