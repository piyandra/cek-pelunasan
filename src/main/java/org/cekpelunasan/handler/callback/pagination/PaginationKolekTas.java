package org.cekpelunasan.handler.callback.pagination;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PaginationKolekTas {

	public InlineKeyboardMarkup dynamicButtonName(Page<?> page, int currentPage, String query) {
		List<InlineKeyboardRow> rows = new ArrayList<>();
		rows.add(buildPaginationRow(page, currentPage, query));
		return InlineKeyboardMarkup.builder().keyboard(rows).build();
	}

	private InlineKeyboardRow buildPaginationRow(Page<?> page, int currentPage, String query) {
		InlineKeyboardRow row = new InlineKeyboardRow();

		int total = (int) page.getTotalElements();
		int from = currentPage * page.getSize() + 1;
		int to = from + page.getNumberOfElements() - 1;
		log.info("Current Page = {}", page.hasPrevious());

		if (page.hasPrevious()) {
			row.add(buildButton("⬅ Prev", "koltas_" + query + "_" + (currentPage - 1)));
		}

		row.add(buildButton(from + " - " + to + " / " + total, "noop"));

		if (page.hasNext()) {
			row.add(buildButton("Next ➡", "koltas_" + query + "_" + (currentPage + 1)));
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
