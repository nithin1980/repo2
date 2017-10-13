package com.mod.objects;

import java.util.ArrayList;
import java.util.List;

public class CacheMetaData {
	
	private List<String> data;
	
	public CacheMetaData() {
		// TODO Auto-generated constructor stub
		setData(new ArrayList<String>());
		
	}
	public CacheMetaData(List<String> data) {
		// TODO Auto-generated constructor stub
		setData(data);
		
	}
	

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	
	

}
