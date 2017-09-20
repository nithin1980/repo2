package com.mod.order;

public class OrderInfo {
	
	private String name;
	private String value;
	private String type;
	
	public OrderInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public OrderInfo(String name,String value,String type) {
		// TODO Auto-generated constructor stub
		setName(name);
		setType(type);
		setValue(value);
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	

}
