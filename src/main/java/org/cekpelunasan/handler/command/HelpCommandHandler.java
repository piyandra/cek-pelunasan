package org.cekpelunasan.handler.command;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.CompletableFuture;

@Component
public class HelpCommandHandler implements CommandProcessor {
    private final MessageTemplate messageTemplate;

    public HelpCommandHandler(MessageTemplate messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String getCommand() {
        return "/help";
    }

    @Override
    @Async
    public CompletableFuture<Void> process(Update update, TelegramClient telegramClient) {
        return CompletableFuture.runAsync(() -> {
            if (isHelpCommand(update)) {
                sendHelpMessage(update.getMessage().getChatId(), telegramClient);
            }
        });
    }

    private boolean isHelpCommand(Update update) {
        String messageText = update.getMessage().getText();
        return messageText != null && messageText.trim().startsWith(getCommand());
    }

    private void sendHelpMessage(Long chatId, TelegramClient telegramClient) {
        sendMessage(chatId, messageTemplate.helpMessage(), telegramClient);
    }
}
