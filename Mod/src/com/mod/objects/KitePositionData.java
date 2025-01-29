package com.mod.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KitePositionData {
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("data")
	private KitePositionDataLayer1 data;
	
	

}
