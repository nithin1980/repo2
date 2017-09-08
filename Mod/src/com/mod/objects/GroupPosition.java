package com.mod.objects;

import java.util.ArrayList;
import java.util.List;

public class GroupPosition {
	private List<Position> pePositions;
	private List<Position> cePositions;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String resp = "{";
		resp = resp+pePositions.toString();
		resp = resp+"\r\n";
		resp = resp+cePositions.toString();
		resp = resp+"}";
		return resp;
	}
	public GroupPosition() {
		// TODO Auto-generated constructor stub
		setPePositions(new ArrayList<Position>());
		setCePositions(new ArrayList<Position>());
	}
	
	public List<Position> getPePositions() {
		return pePositions;
	}
	public void setPePositions(List<Position> pePositions) {
		this.pePositions = pePositions;
	}
	public List<Position> getCePositions() {
		return cePositions;
	}
	public void setCePositions(List<Position> cePositions) {
		this.cePositions = cePositions;
	}
	
	
	public double total(){
		double pePos = 0.0;
		double cePos = 0.0;
		int size = 0;
		
		if(pePositions!=null){
			size = pePositions.size();
			if(size>0){
				for(int i=0;i<size;i++){
					pePos = pePos + pePositions.get(i).getProfit();
				}
			}
		}
		if(cePositions!=null){
			size = cePositions.size();
			if(size>0){
				for(int i=0;i<size;i++){
					cePos = cePos + cePositions.get(i).getProfit();
				}
			}
		}		
		
		return pePos+cePos;
		
	}
	
	
	
}
