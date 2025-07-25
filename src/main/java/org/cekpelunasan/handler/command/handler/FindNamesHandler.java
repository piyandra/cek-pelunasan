package org.cekpelunasan.handler.command.handler;

import lombok.RequiredArgsConstructor;
import org.cekpelunasan.entity.Repayment;
import org.cekpelunasan.handler.command.CommandProcessor;
import org.cekpelunasan.handler.command.template.MessageTemplate;
import org.cekpelunasan.service.auth.AuthorizedChats;
import org.cekpelunasan.service.repayment.RepaymentService;
import org.cekpelunasan.utils.TagihanUtils;
import org.cekpelunasan.utils.button.ButtonListForName;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class FindNamesHandler implements CommandProcessor {

	private static final int PAGE_SIZE = 5;
	private static final int FIRST_PAGE = 0;
	private final RepaymentService repaymentService;
	private final AuthorizedChats authService;
	private final MessageTemplate messageTemplateService;
	private final TagihanUtils tagihanUtils;


	@Override
	public String getCommand() {
		return "/fi";
	}

	@Override
	public String getDescription() {
		return """
			Gunakan command ini untuk mencari pelunasan
			berdasarkan yang anda kirim
			""";
	}

	@Override
	@Async
	public CompletableFuture<Void> process(long chatId, String text, TelegramClient telegramClient) {
		return CompletableFuture.runAsync(() -> {
			long startTime = System.currentTimeMillis();
			String keyword = extractKeyword(text);

			if (!authService.isAuthorized(chatId)) {
				sendMessage(chatId, messageTemplateService.unathorizedMessage(), telegramClient);
				return;
			}
			if (keyword.isEmpty()) {
				sendMessage(chatId, messageTemplateService.fiCommandHelper(), telegramClient);
				return;
			}

			Page<Repayment> repayments = repaymentService.findName(keyword, FIRST_PAGE, PAGE_SIZE);

			if (repayments.isEmpty()) {
				sendMessage(chatId, String.format("""
					❌ *Informasi* ❌
					
					Data `%s` tidak ditemukan. Periksa ejaan atau gunakan kata kunci yang lebih singkat.
					""", keyword), telegramClient);
				return;
			}

			StringBuilder messageBuilder = new StringBuilder(
				String.format("\uD83D\uDCC4 Halaman 1 dari %d\n\n", repayments.getTotalPages())
			);

			repayments.forEach(dto -> messageBuilder.append(tagihanUtils.getAllPelunasan(dto)));

			String footer = String.format("\n\nEksekusi dalam %dms", System.currentTimeMillis() - startTime);
			messageBuilder.append(footer);


			InlineKeyboardMarkup markup = new ButtonListForName().dynamicButtonName(repayments, FIRST_PAGE, keyword);
			sendMessage(chatId, messageBuilder.toString(), telegramClient, markup);
		});
	}

	private String extractKeyword(String text) {
		return text.length() > 4 ? text.substring(4).trim() : "";
	}

	public void sendMessage(Long chatId, String text, TelegramClient telegramClient) {
		try {
			telegramClient.execute(SendMessage.builder()
				.chatId(chatId.toString())
				.text(text)
				.parseMode("Markdown")
				.build());
		} catch (Exception e) {
			log.error("Error");
		}
	}

	public void sendMessage(Long chatId, String text, TelegramClient telegramClient, InlineKeyboardMarkup markup) {
		try {
			telegramClient.execute(SendMessage.builder()
				.chatId(chatId.toString())
				.text(text)
				.replyMarkup(markup)
				.parseMode("Markdown")
				.build());
		} catch (Exception e) {
			log.error("Error Sending Message");
		}
	}

}