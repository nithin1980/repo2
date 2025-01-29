package com.mod.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KitePositionDataLayer1 {
	
	@JsonProperty("net")
	private KitePositionDataLayer2[] net;
	
	@JsonProperty("day")
	private KitePositionDataLayer2[] day;

	public KitePositionDataLayer2[] getNet() {
		return net;
	}

	public void setNet(KitePositionDataLayer2[] net) {
		this.net = net;
	}

	public KitePositionDataLayer2[] getDay() {
		return day;
	}

	public void setDay(KitePositionDataLayer2[] day) {
		this.day = day;
	}
	
	

}
