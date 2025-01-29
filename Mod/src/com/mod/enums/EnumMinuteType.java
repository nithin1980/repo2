package com.mod.enums;

public enum EnumMinuteType {
	minute,Fiveminute,Fifteenminute;
	
	public String getMinuteString() {
		if("minute".equalsIgnoreCase(toString())) {
			return toString();
		}
		if("Fiveminute".equalsIgnoreCase(toString())) {
			return "5minute";
		}
		if("Fifteenminute".equalsIgnoreCase(toString())) {
			return "15minute";
		}

		throw new RuntimeException("No string for name:"+toString());
	}
}
