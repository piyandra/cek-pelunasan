package org.cekpelunasan.handler.callback.pagination;

import org.cekpelunasan.entity.Savings;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
public class PaginationSavingsButton {

	public InlineKeyboardMarkup keyboardMarkup(Page<Savings> page, String branch, int currentPage, String query) {
		return InlineKeyboardMarkup.builder()
			.keyboard(List.of(buildPaginationRow(page, currentPage, branch, query)))
			.build();
	}

	private InlineKeyboardRow buildPaginationRow(Page<?> page, int currentPage, String branch, String query) {
		InlineKeyboardRow row = new InlineKeyboardRow();
		int total = (int) page.getTotalElements();
		int from = currentPage * page.getSize() + 1;
		int to = from + page.getNumberOfElements() - 1;

		if (page.hasPrevious()) {
			row.add(buildButton("⬅ Prev", "tab_" + query + "_" + branch + "_" + (currentPage - 1)));
		}

		row.add(buildButton(from + " - " + to + " / " + total, "noop"));

		if (page.hasNext()) {
			row.add(buildButton("Next ➡", "tab_" + query + "_" + branch + "_" + (currentPage + 1)));
		}

		return row;
	}

	private InlineKeyboardButton buildButton(String text, String callbackData) {
		return InlineKeyboardButton.builder()
			.text(text)
			.callbackData(callbackData)
			.build();
	}
}
