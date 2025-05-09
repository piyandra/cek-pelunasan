package org.cekpelunasan.bot;

import org.cekpelunasan.handler.callback.CallbackHandler;
import org.cekpelunasan.handler.command.CommandHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class TelegramBot {

	private final CommandHandler commandHandler;
	private final CallbackHandler callbackHandler;

	public TelegramBot(CommandHandler commandHandler, CallbackHandler callbackHandler) {
		this.commandHandler = commandHandler;
		this.callbackHandler = callbackHandler;
	}

	@Async
	public void startBot(Update update, TelegramClient telegramClient) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			commandHandler.handle(update, telegramClient);
		}
		if (update.hasCallbackQuery()) {
			callbackHandler.handle(update, telegramClient);
		}

	}
}
