package com.hyd.htbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
public class BotService implements ApplicationListener<ContextRefreshedEvent> {

    private final TelegramConfig telegramConfig;

    @Autowired
    public BotService(TelegramConfig telegramConfig) {
        this.telegramConfig = telegramConfig;
    }

    public void startBot() {
        TelegramBot bot = new TelegramBot(telegramConfig.getBot().getToken());
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                // 处理接收到的消息
                System.out.println("Received message: " + update.message().text());
                // 回复消息 "ok"
                bot.execute(new SendMessage(update.message().chat().id(), "ok"));
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        startBot();
    }
}