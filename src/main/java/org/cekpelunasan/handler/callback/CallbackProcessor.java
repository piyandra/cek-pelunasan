package org.cekpelunasan.handler.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


public interface CallbackProcessor {

    Logger log = LoggerFactory.getLogger(CallbackProcessor.class);

    String getCallBackData();

    void process(Update update, TelegramClient telegramClient);

    default void editMessageWithMarkup(Long chatId, int messageId, String text, TelegramClient telegramClient, InlineKeyboardMarkup replyMarkup) {
        try {
            telegramClient.execute(EditMessageText.builder()
                    .chatId(chatId)
                    .text(text)
                    .replyMarkup(replyMarkup)
                    .messageId(messageId)
                    .parseMode("Markdown")
                    .build());
        } catch (TelegramApiException e) {
            log.warn("Error Receiving Callback Data");
        }
    }
    default void sendMessage(Long chatId, String text, TelegramClient telegramClient) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .parseMode("Markdown")
                    .build());
        } catch (TelegramApiException e) {
            log.info("Error sending message: {}", e.getMessage());
        }
    }

}