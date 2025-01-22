package com.hyd.htbot;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TextBoundaryService {
    public void process(TextBoundary textBoundary) {
        // 获取字体度量
        Font font = textBoundary.getFont();
        FontMetrics fontMetrics;
        if (GraphicsEnvironment.isHeadless()) {
            // 创建虚拟图像来获取 FontMetrics
            fontMetrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
                    .getGraphics()
                    .getFontMetrics(font);
        } else {
            fontMetrics = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration()
                    .createCompatibleImage(1, 1)
                    .getGraphics()
                    .getFontMetrics(font);
        }

        // 初始化变量
        String originalText = textBoundary.getOriginalText();
        // 将不同平台的换行符统一换成 "\n"
        originalText = originalText.replace("\r\n", "\n").replace("\r", "\n");
        int maxPixelWidth = textBoundary.getMaxPixelWidth();
        List<String> splitText = new ArrayList<>();
        int calculatedPixelHeight = 0;

        // 使用 BreakIterator 来拆分文字
        BreakIterator boundary = BreakIterator.getCharacterInstance();
        boundary.setText(originalText);

        int start = boundary.first();
        int end = boundary.next();
        StringBuilder currentLine = new StringBuilder();

        while (end != BreakIterator.DONE) {
            String word = originalText.substring(start, end);

            // 新增逻辑：如果遇到换行符，则立刻结束当前行并开始新行
            if (word.equals("\n")) {
                splitText.add(currentLine.toString());
                calculatedPixelHeight += fontMetrics.getHeight();
                currentLine.setLength(0); // 清空当前行
            } else if (currentLine.length() == 0 || fontMetrics.stringWidth(currentLine.toString() + word) <= maxPixelWidth) {
                currentLine.append(word);
            } else {
                splitText.add(currentLine.toString());
                calculatedPixelHeight += fontMetrics.getHeight();
                currentLine.setLength(0); // 清空当前行
                currentLine.append(word);
            }

            start = end;
            end = boundary.next();
        }

        // 添加最后一行
        if (currentLine.length() > 0) {
            splitText.add(currentLine.toString());
            calculatedPixelHeight += fontMetrics.getHeight();
        }

        // 设置结果到 TextBoundary 对象
        textBoundary.setSplitText(splitText.toArray(new String[0]));
        textBoundary.setCalculatedPixelHeight(calculatedPixelHeight);
    }
}