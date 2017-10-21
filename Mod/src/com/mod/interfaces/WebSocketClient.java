package com.mod.interfaces;

import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;

public abstract class WebSocketClient {
	
	public abstract String mode();
	
	public ConfigData appConfig(){
		return ApplicationHelper.Application_Config_Cache.get("app");
	}

	public abstract ConfigData modeConfig();
}
