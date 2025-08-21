package org.cekpelunasan.service.whatsapp;

import org.cekpelunasan.dto.InvoiceRequest;
import org.cekpelunasan.dto.InvoiceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentSetting {


	private final RestTemplateBuilder restTemplateBuilder;
	@Value("${config.api.path}")
	private String gateway;

	@Value("${config.api.token}")
	private String token;

	public PaymentSetting(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplateBuilder = restTemplateBuilder;
	}

	public InvoiceResponse getPaymentData(InvoiceRequest request) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("Authorization", "Bearer " + token);
		HttpEntity<InvoiceRequest> entity = new HttpEntity<>(params);
		return restTemplateBuilder.build().postForEntity(gateway, entity, InvoiceResponse.class).getBody();
	}
	public byte[] getQrisData(InvoiceResponse response) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.IMAGE_PNG);
		HttpEntity<String> request = new HttpEntity<>(headers);
		String url = gateway + "/api/v2/invoices/qris/" + response.getInvoiceId();
		RestTemplate restTemplate = restTemplateBuilder.build();
		ResponseEntity<byte[]> exchange = restTemplate.exchange(
			url,
			HttpMethod.GET,
			request,
			byte[].class
		);
		return exchange.getBody();
	}
}
