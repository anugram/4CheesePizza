package com.crypto.cadp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EncryptRequest {
	@JsonProperty("Encrypt")
	private Encrypt encrypt;

	public Encrypt getEncrypt() {
		return encrypt;
	}

	public void setEncrypt(Encrypt encrypt) {
		this.encrypt = encrypt;
	}

	public static class Encrypt {
		private String username;
		private String password;
		private String keyname;
		private String keyiv;
		private String transformation;
		private String plaintext;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getKeyname() {
			return keyname;
		}

		public void setKeyname(String keyname) {
			this.keyname = keyname;
		}

		public String getKeyiv() {
			return keyiv;
		}

		public void setKeyiv(String keyiv) {
			this.keyiv = keyiv;
		}

		public String getTransformation() {
			return transformation;
		}

		public void setTransformation(String transformation) {
			this.transformation = transformation;
		}

		public String getPlaintext() {
			return plaintext;
		}

		public void setPlaintext(String plaintext) {
			this.plaintext = plaintext;
		}
	}
}
