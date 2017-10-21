package com.mod.support;

import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("applicationconfigdata")
public class ConfigData {
	
	private Map<String, String> keyValueConfigs;
	
	private Map<String, List<String>> referenceDataMap;

	public Map<String, String> getKeyValueConfigs() {
		return keyValueConfigs;
	}

	public void setKeyValueConfigs(Map<String, String> keyValueConfigs) {
		this.keyValueConfigs = keyValueConfigs;
	}

	public Map<String, List<String>> getReferenceDataMap() {
		return referenceDataMap;
	}

	public void setReferenceDataMap(Map<String, List<String>> referenceDataMap) {
		this.referenceDataMap = referenceDataMap;
	}
	
	

}
