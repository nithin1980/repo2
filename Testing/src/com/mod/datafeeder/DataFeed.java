package com.mod.datafeeder;

import java.util.List;

import com.mod.ApplicationHelper;

public class DataFeed {

	public static List<List<String>> data(){
		return ApplicationHelper.getConfig();
	}
}
