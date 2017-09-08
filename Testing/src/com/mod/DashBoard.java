package com.mod;

import java.util.HashMap;
import java.util.Map;

import com.mod.objects.GroupPosition;


public class DashBoard {

	public double overall;
	
	public static Map<String, GroupPosition> positionMap = new HashMap<String, GroupPosition>();
	
	public DashBoard() {
	}

	public Map<String, GroupPosition> getPositionMap() {
		return positionMap;
	}
	
	public static void main(String[] args) {
		double s = -212.0;
		double c=-224.0;
		System.out.println(c>s);
	}
	
}
