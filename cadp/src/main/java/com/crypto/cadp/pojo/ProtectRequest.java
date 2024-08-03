package com.crypto.cadp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProtectRequest {
	@JsonProperty("protection_policy_name")
	private String policyName;
	private String data;

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
