package com.crypto.cadp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EncryptResponse {
	@JsonProperty("EncryptResponse")
	private EncryptResponseData encryptResponse;

	public EncryptResponseData getEncryptResponse() {
		return encryptResponse;
	}

	public void setEncryptResponse(EncryptResponseData encryptResponse) {
		this.encryptResponse = encryptResponse;
	}

	public static class EncryptResponseData {
		private String cipherText;

		public String getCipherText() {
			return cipherText;
		}

		public void setCipherText(String cipherText) {
			this.cipherText = cipherText;
		}
	}
}
