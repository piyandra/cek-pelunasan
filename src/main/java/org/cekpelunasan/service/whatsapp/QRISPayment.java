package org.cekpelunasan.service.whatsapp;

import org.cekpelunasan.dto.InvoiceRequest;
import org.cekpelunasan.dto.InvoiceResponse;
import org.cekpelunasan.dto.WhatsappMessageDTO;
import org.cekpelunasan.utils.RupiahFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class QRISPayment {

	private final RupiahFormatUtils rupiahFormatUtils;
	@Value("${whatsapp.gateway.url}")
	private String gatewayUrl;

	@Value("${whatsapp.gateway.password}")
	private String gatewayPassword;

	@Value("${whatsapp.gateway.username}")
	private String gatewayUsername;

	private final PaymentSetting paymentSetting;

	public QRISPayment(PaymentSetting paymentSetting, RupiahFormatUtils rupiahFormatUtils) {
		this.paymentSetting = paymentSetting;
		this.rupiahFormatUtils = rupiahFormatUtils;
	}

	public void sendingPaymentData(WhatsappMessageDTO dto) {
		InvoiceRequest request = new InvoiceRequest();
		request.setAmount(Long.parseLong(dto.getMessage().getText().replace(".qris ", "")));
		request.setNotes("Bayar Apapun itu dari " + dto.getPushname());
		request.setExpiresAt(600L);
		InvoiceResponse paymentData = paymentSetting.getPaymentData(request);
		byte[] qrisData = paymentSetting.getQrisData(paymentData);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setBasicAuth(gatewayUsername, gatewayPassword);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("phone", dto.getFrom());
		body.add("caption", "Silahkan bayar sejumlah " + rupiahFormatUtils.formatRupiah(paymentData.getAmount()) + "\nPembayaran expired dalam 10 menit");

	}
}
