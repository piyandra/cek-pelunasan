package org.cekpelunasan.handler.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class StartCommandHandler implements CommandProcessor {

    private final CommandHandler commandHandler;

    private static final String START_MESSAGE = """
            👋 *Halo! Selamat datang di Bot Pelunasan.*

            Bot ini bukan tempat tanya jodoh, ya. Saya cuma bisa bantu cek *pelunasan*

            Berikut beberapa perintah yang bisa kamu pakai:

            🔹 */pl <No SPK>* — Cek pelunasan nasabah
            🔹 */fi <Nama>* — Cari nasabah by nama
            🔹 */help* — Kalau kamu butuh bimbingan hidup (atau cuma mau lihat perintah)

            📌 *Kalau kamu belum terdaftar*, jangan baper. Ketik `.id`, kirim ke admin, dan sabar tunggu restu. 🧘‍♂️

            📌 Kalau mau curhat bisa langsung ke admin ya, kirim aja disini, siapa tahu mau ramalan zodiak kamu

            Yuk, langsung aja dicoba.
            """;

    public StartCommandHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public void process(Update update, TelegramClient telegramClient) {
        Long chatId = update.getMessage().getChatId();
        if (commandHandler.isAuthorized(chatId)) {
            sendWelcomeMessage(chatId, telegramClient);
        } else {
            sendUnauthorizedMessage(chatId, telegramClient);
        }
    }

    private void sendWelcomeMessage(Long chatId, TelegramClient telegramClient) {
        sendMessage(chatId, START_MESSAGE, telegramClient);
    }

    private void sendUnauthorizedMessage(Long chatId, TelegramClient telegramClient) {
        sendMessage(chatId, commandHandler.sendUnauthorizedMessage(), telegramClient);
    }
}
