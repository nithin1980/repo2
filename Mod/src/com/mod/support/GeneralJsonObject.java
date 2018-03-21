package com.mod.support;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralJsonObject {

	@JsonProperty("DateTime_24HR")
	private String DateTime_24HR;
	
	@JsonProperty("requestHeadersText")
	private String requestHeadersText;

	@JsonProperty("Sec-WebSocket-Key")
	private String SecWebSocketKey;
	
	@JsonProperty("requestHeaders")
	private GeneralJsonObject requestHeaders;
	
	@JsonProperty("message")
	private GeneralJsonObject message;
	
	@JsonProperty("params")
	private GeneralJsonObject params;	
	
	@JsonProperty("response")
	private GeneralJsonObject response;
	
	@JsonProperty("data")
	private GeneralJsonObject data;
	
	@JsonProperty("candles")
	private List<String[]> candles;
	
	@JsonProperty("niftydata")
	private GeneralJsonObject niftydata;
	
	public String websocketKey(){
		return getMessage().getParams().getResponse().getRequestHeaders().getSecWebSocketKey();
	}
	public String url(){
		return getMessage().getParams().getResponse().getRequestHeadersText().split("\\ ")[1];
	}
	
	public GeneralJsonObject getMessage() {
		return message;
	}

	public void setMessage(GeneralJsonObject message) {
		this.message = message;
	}

	public GeneralJsonObject getParams() {
		return params;
	}

	public void setParams(GeneralJsonObject params) {
		this.params = params;
	}

	public GeneralJsonObject getResponse() {
		return response;
	}

	public void setResponse(GeneralJsonObject response) {
		this.response = response;
	}

	public String getDateTime_24HR() {
		return DateTime_24HR;
	}

	public void setDateTime_24HR(String dateTime_24HR) {
		DateTime_24HR = dateTime_24HR;
	}

	public String getRequestHeadersText() {
		return requestHeadersText;
	}

	public void setRequestHeadersText(String requestHeadersText) {
		this.requestHeadersText = requestHeadersText;
	}

	public String getSecWebSocketKey() {
		return SecWebSocketKey;
	}

	public void setSecWebSocketKey(String secWebSocketKey) {
		SecWebSocketKey = secWebSocketKey;
	}

	public GeneralJsonObject getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(GeneralJsonObject requestHeaders) {
		this.requestHeaders = requestHeaders;
	}
	public GeneralJsonObject getData() {
		return data;
	}
	public void setData(GeneralJsonObject data) {
		this.data = data;
	}
	public List<String[]> getCandles() {
		return candles;
	}
	public void setCandles(List<String[]> candles) {
		this.candles = candles;
	}
	public GeneralJsonObject getNiftydata() {
		return niftydata;
	}
	public void setNiftydata(GeneralJsonObject niftydata) {
		this.niftydata = niftydata;
	}
	
	
	

}
