package org.cekpelunasan.handler.callback;

import org.cekpelunasan.entity.Repayment;
import org.cekpelunasan.service.RepaymentService;
import org.cekpelunasan.utils.ImageGeneratorUtils;
import org.cekpelunasan.utils.PenaltyUtils;
import org.cekpelunasan.utils.RupiahFormatUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.CompletableFuture;

@Component
public class PhotoHandler implements CallbackProcessor {


    private final RepaymentService repaymentService;

    public PhotoHandler(RepaymentService repaymentService1) {
        this.repaymentService = repaymentService1;
    }

    @Override
    public String getCallBackData() {
        return "photo";
    }

    @Override
    @Async
    public CompletableFuture<Void> process(Update update, TelegramClient telegramClient) {
        return CompletableFuture.runAsync(() -> {
            long start = System.currentTimeMillis();
            String data = update.getCallbackQuery().getData();
            String[] parts = data.split("_");
            String customerId = parts[1];
            Repayment repayment = repaymentService.findRepaymentById(Long.parseLong(customerId));
            ImageGeneratorUtils generatorUtils = new ImageGeneratorUtils(new RupiahFormatUtils(), new PenaltyUtils());
            InputFile inputFile = generatorUtils.generateImages(repayment);
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            sendPhoto(chatId, "Eksekusi Dalam " + (System.currentTimeMillis() - start) + " ms",inputFile, telegramClient);


        });
    }
}
