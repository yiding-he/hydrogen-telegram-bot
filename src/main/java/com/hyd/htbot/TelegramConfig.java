package com.hyd.htbot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram")
@Data // 使用 Lombok 的 Data 注解简化 getter 和 setter 方法
public class TelegramConfig {
    private BotConfig bot;
    private FontConfig font = new FontConfig();

    @Data // 使用 Lombok 的 Data 注解简化 getter 和 setter 方法
    public static class BotConfig {
        private String token;
    }

    @Data
    public static class FontConfig {
        private String defaultFontName = "Default";
        private int defaultFontSize = 30;
    }
}