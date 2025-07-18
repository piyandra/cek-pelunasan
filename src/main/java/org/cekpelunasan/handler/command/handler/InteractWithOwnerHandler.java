package org.cekpelunasan.handler.command.handler;

import lombok.RequiredArgsConstructor;
import org.cekpelunasan.handler.command.CommandProcessor;
import org.cekpelunasan.service.auth.AuthorizedChats;
import org.cekpelunasan.utils.button.DirectMessageButton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class InteractWithOwnerHandler implements CommandProcessor {

	private final AuthorizedChats authorizedChats1;
	private final DirectMessageButton directMessageButton;
	@Value("${telegram.bot.owner}")
	private Long ownerId;

	@Override
	public String getCommand() {
		return "/id";
	}

	@Override
	public String getDescription() {
		return """
			Gunakan command ini untuk generate User Id anda
			untuk kebutuhan Authorization
			""";
	}

	@Override
	@Async
	public CompletableFuture<Void> process(Update update, TelegramClient telegramClient) {
		return CompletableFuture.runAsync(() -> {
			String text = update.getMessage().getText();
			Long chatId = update.getMessage().getChatId();
			log.info("Get message {}", update.getMessage().getText());
			Message message = update.getMessage();

			if (text.equals(getCommand())) {
				sendMessage(chatId, "ID Kamu `" + chatId + "`", telegramClient);
				return;
			}
			Integer messageId = message.getMessageId();
			log.info("Account Is {}", authorizedChats1.isAuthorized(chatId));
			if (authorizedChats1.isAuthorized(chatId)) {
				log.info("Text is valid {}", isValidAccount(text));
				if (isValidAccount(text)) {
					log.info("Valid account {}", text);
					String id = text.trim();
					sendMessage(chatId, "Pilih salah satu action dibawah ini", directMessageButton.selectServices(id), telegramClient);
					return;
				}
			}

			if (!chatId.equals(ownerId)) {
				forwardMessage(chatId, ownerId, messageId, telegramClient);
				return;
			}
			Long originalUserId = message.getReplyToMessage().getForwardFrom().getId();
			log.info("Original message Is {}", originalUserId);
			copyMessage(ownerId, messageId, originalUserId, telegramClient);
		});
	}

	public void sendMessage(Long chatId, String text, TelegramClient telegramClient) {
		try {
			telegramClient.execute(SendMessage.builder()
				.chatId(chatId.toString())
				.text(text)
				.parseMode("Markdown")
				.build());
		} catch (Exception e) {
			log.error("Gagal kirim pesan", e);
		}
	}

	public void sendMessage(Long chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup, TelegramClient telegramClient) {
		try {
			telegramClient.execute(SendMessage.builder()
				.chatId(chatId.toString())
				.text(text)
				.replyMarkup(inlineKeyboardMarkup)
				.parseMode("Markdown")
				.build());
		} catch (Exception e) {
			log.error("Gagal kirim pesan", e);
		}
	}

	public void forwardMessage(Long fromChatId, Long toChatId, Integer messageId, TelegramClient telegramClient) {
		try {
			telegramClient.execute(ForwardMessage.builder()
				.chatId(toChatId.toString())
				.fromChatId(fromChatId.toString())
				.messageId(messageId)
				.build());
		} catch (Exception e) {
			log.error("Gagal forward pesan", e);
		}
	}

	public void copyMessage(Long fromChatId, Integer messageId, Long toChatId, TelegramClient telegramClient) {
		try {
			telegramClient.execute(CopyMessage.builder()
				.chatId(toChatId.toString())
				.fromChatId(fromChatId.toString())
				.messageId(messageId)
				.build());
		} catch (Exception e) {
			log.error("Gagal copy pesan", e);
		}
	}

	public boolean isValidAccount(String input) {
		Pattern pattern = Pattern.compile("\\d{12}");
		Matcher matcher = pattern.matcher(input);
		return matcher.find();
	}

}
