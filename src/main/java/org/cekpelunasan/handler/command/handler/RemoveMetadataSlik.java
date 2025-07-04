package org.cekpelunasan.handler.command.handler;

import lombok.RequiredArgsConstructor;
import org.cekpelunasan.handler.command.CommandProcessor;
import org.cekpelunasan.service.slik.GenerateMetadataSlikForUncompletedDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class RemoveMetadataSlik implements CommandProcessor {

	private final GenerateMetadataSlikForUncompletedDocument generateMetadataSlikForUncompletedDocument;
	@Value("${telegram.bot.owner}")
	private String owner;

	@Override
	public String getCommand() {
		return "/remdata";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public CompletableFuture<Void> process(long chatId, String text, TelegramClient telegramClient) {
		if (chatId != Long.parseLong(owner)) {
			return CompletableFuture.runAsync(() -> sendMessage(chatId, "Hanya Admin yang dapat menggunakan command ini", telegramClient));
		}
		return CompletableFuture.runAsync(() ->{
			String key = text.replace("/remdata ", "");
			if (key.isEmpty()) {
				sendMessage(chatId, "Key Harus Diisi", telegramClient);
				return;
			}
			if (key.length() < 2) {
				sendMessage(chatId, "Key Harus Diisi lebih dari 2 karakter", telegramClient);
				return;
			}
			generateMetadataSlikForUncompletedDocument.deleteMetadata(key);
			sendMessage(chatId, "Berhasil Menghapus Metadata", telegramClient);
		});
	}
}
