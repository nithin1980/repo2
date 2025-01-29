package com.mod.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mod.objects.KitePositionDataLayer1;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KitePositionQueryResponse implements PostionQueryResponse {

	@JsonProperty("status")
	private String status;
	
	@JsonProperty("data")
	private KitePositionDataLayer1 data;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public KitePositionDataLayer1 getData() {
		return data;
	}

	public void setData(KitePositionDataLayer1 data) {
		this.data = data;
	}

}
