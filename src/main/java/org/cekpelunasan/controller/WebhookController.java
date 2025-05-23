package org.cekpelunasan.controller;

import org.cekpelunasan.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@RestController
public class WebhookController {

	private final TelegramClient telegramClient;
	private final TelegramBot telegramBot;

	public WebhookController(@Value("${telegram.bot.token}") String botToken, TelegramBot telegramBot) {
		this.telegramClient = new OkHttpTelegramClient(botToken);
		this.telegramBot = telegramBot;
	}

	@PostMapping("/webhook")
	public String webhook(@RequestBody Update update) {
		telegramBot.startBot(update, telegramClient);
		return "Webhook is working!";
	}
}
