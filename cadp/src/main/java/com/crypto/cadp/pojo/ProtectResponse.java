package com.crypto.cadp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProtectResponse {
	@JsonProperty("protected_data")
	private String cipherText;

	public String getCipherText() {
		return cipherText;
	}

	public void setCipherText(String cipherText) {
		this.cipherText = cipherText;
	}
}
