package com.mod.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KiteHoldingsQueryResponse implements HoldingsQueryResponse {
	
	
	@JsonProperty("status")
	private String status;

	@JsonProperty("data")
	private KiteHoldingsDataLayer1[] data;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public KiteHoldingsDataLayer1[] getData() {
		return data;
	}

	public void setData(KiteHoldingsDataLayer1[] data) {
		this.data = data;
	}

	
	
	
	

}
