package com.hyd.htbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class TextToImageBot implements ApplicationListener<ContextRefreshedEvent> {

    private final TelegramConfig telegramConfig;
    // 新增字段: 最大行宽
    private final int maxLineWidth = 700;

    @Autowired
    private TextBoundaryService textBoundaryService;

    public TextToImageBot(TelegramConfig telegramConfig) {
        this.telegramConfig = telegramConfig;
    }

    public void startBot() {
        TelegramBot bot = new TelegramBot(telegramConfig.getBot().getToken());
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.message() == null) {
                    continue;
                }
                try {
                    // 处理接收到的消息
                    String messageText = update.message().text();
                    if (messageText != null && messageText.startsWith("/t2i")) {
                        textToImage(bot, update);
                    } else {
                        log.info("Received message: {}", messageText);
                        // 回复消息 "ok"
                        bot.execute(new SendMessage(update.message().chat().id(), "ok"));
                    }
                } catch (Exception e) {
                    // 回复消息 "后端出现异常，无法处理本次请求。"
                    bot.execute(new SendMessage(update.message().chat().id(), "后端出现异常，无法处理本次请求。"));
                    log.error("Error processing update", e);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
        // 添加: 日志输出确认 bot 的状态
        log.info("Bot started successfully.");
    }

    // 新增方法: 处理 /t2i 命令
    private void textToImage(TelegramBot bot, Update update) {
        // 获取消息内容，去掉 "/t2i" 前缀，然后去掉首尾空白字符
        String messageText = update.message().text().substring(4).trim();

        // 检查 messageText 是否为空
        if (messageText.isEmpty()) {
            // 返回文本消息 "请在命令后面附加上需要转图片的文字内容"
            bot.execute(new SendMessage(update.message().chat().id(), "请在命令后面附加上需要转图片的文字内容"));
            return;
        }

        // 生成 BufferedImage 对象
        BufferedImage image = textToImage0(messageText);

        // 将 BufferedImage 转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write image to ByteArrayOutputStream", e);
        }
        byte[] imageBytes = baos.toByteArray();

        // 发送图片
        SendPhoto sendPhoto = new SendPhoto(update.message().chat().id(), imageBytes);
        bot.execute(sendPhoto);
    }

    private BufferedImage textToImage0(String messageText) {
        // 解析颜色参数、字体参数和背景颜色参数
        String[] parts = messageText.split("\\|");
        String text = parts[0].trim();
        String colorStr = parts.length > 1 ? parts[1].trim() : "282828";
        String bgColorStr = parts.length > 2 ? parts[2].trim() : "FFFFFF";
        String fontName = parts.length > 3 ? parts[3].trim() : telegramConfig.getFont().getDefaultFontName();

        Color textColor = toColor(colorStr);
        Color bgColor = toColor(bgColorStr);
        int fontSize = telegramConfig.getFont().getDefaultFontSize();
        Font font = new Font(fontName, Font.PLAIN, fontSize);

        // 创建 TextBoundary 对象并设置相关属性
        TextBoundary textBoundary = new TextBoundary();
        textBoundary.setOriginalText(text);
        textBoundary.setMaxPixelWidth(maxLineWidth);
        textBoundary.setFont(font);
        textBoundaryService.process(textBoundary);

        // 获取计算出的高度，并创建 BufferedImage 对象
        int height = textBoundary.getCalculatedPixelHeight();
        int imageWidth = maxLineWidth + 100;
        int imageHeight = height + 60;
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, imageWidth, imageHeight);
        g2d.setColor(textColor);
        g2d.setFont(font);

        // 设置文本布局并绘制文本
        FontMetrics fm = g2d.getFontMetrics();
        int x = 50;
        int y = 50;
        int lineHeight = fm.getHeight();

        String[] lines = textBoundary.getSplitText();
        for (String line : lines) {
            g2d.drawString(line, x, y);
            y += lineHeight;
        }

        g2d.dispose();
        return image;
    }

    /**
     * 将 "#000000" 形式的颜色字符串转换为 Color 对象
     */
    private static Color toColor(String webColor) {
        if (!webColor.startsWith("#")) {
            webColor = "#" + webColor;
        }
        int r = Integer.parseInt(webColor.substring(1, 3), 16);
        int g = Integer.parseInt(webColor.substring(3, 5), 16);
        int b = Integer.parseInt(webColor.substring(5, 7), 16);
        return new Color(r, g, b);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        startBot();
    }
}