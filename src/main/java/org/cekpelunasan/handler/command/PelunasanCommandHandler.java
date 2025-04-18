package org.cekpelunasan.handler.command;

import org.cekpelunasan.entity.Repayment;
import org.cekpelunasan.service.AuthorizedChats;
import org.cekpelunasan.service.RepaymentService;
import org.cekpelunasan.utils.PenaltyUtils;
import org.cekpelunasan.utils.RepaymentCalculator;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;

@Component
public class PelunasanCommandHandler implements CommandProcessor {

    private final RepaymentService repaymentService;
    private final AuthorizedChats authService;
    private final MessageTemplate messageTemplateService;

    public PelunasanCommandHandler(
            RepaymentService repaymentService,
            AuthorizedChats authService,
            MessageTemplate messageTemplateService
    ) {
        this.repaymentService = repaymentService;
        this.authService = authService;
        this.messageTemplateService = messageTemplateService;
    }

    @Override
    public String getCommand() {
        return "/pl";
    }

    @Override
    public void process(Update update, TelegramClient telegramClient) {
        if (update.getMessage() == null || update.getMessage().getText() == null) return;

        long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();

        if (!authService.isAuthorized(chatId)) {
            sendMessage(chatId, messageTemplateService.authorizedMessage(), telegramClient);
            return;
        }

        if (message.startsWith("/pl")) {
            handlePelunasanCommand(message, chatId, telegramClient);
        }
    }

    private void handlePelunasanCommand(String message, long chatId, TelegramClient telegramClient) {
        long start = System.currentTimeMillis();

        String[] tokens = message.trim().split("\\s+");
        if (tokens.length < 2) {
            sendUsageInstruction(chatId, telegramClient);
            return;
        }

        try {
            Long customerId = Long.parseLong(tokens[1]);
            Repayment repayment = repaymentService.findRepaymentById(customerId);

            if (repayment == null) {
                sendMessage(chatId, "Data Tidak Ditemukan", telegramClient);
                return;
            }

            Map<String, Long> penalty = new PenaltyUtils().penalty(
                    repayment.getStartDate(),
                    repayment.getPenaltyLoan(),
                    repayment.getProduct()
            );

            String result = new RepaymentCalculator().calculate(repayment, penalty);
            sendMessage(chatId, result + "\n\n_Eksekusi dalam " + (System.currentTimeMillis() - start) + "ms_", telegramClient);

        } catch (NumberFormatException e) {
            sendMessage(chatId, "Format ID tidak valid", telegramClient);
        }
    }

    private void sendUsageInstruction(long chatId, TelegramClient telegramClient) {
        String instruction = """
                ‼ *Informasi* ‼
                
                Gunakan `/pl <No SPK>` untuk mencari SPK dan melakukan penghitungan Pelunasan.
                """;
        sendMessage(chatId, instruction, telegramClient);
    }
}
