package com.hyd.htbot;

import lombok.Data;

import java.awt.Font;

@Data
public class TextBoundary {
    private int maxPixelWidth;
    private String originalText;
    private String[] splitText;
    private int calculatedPixelHeight;
    private Font font; // 新增属性: font 类型为字体
}