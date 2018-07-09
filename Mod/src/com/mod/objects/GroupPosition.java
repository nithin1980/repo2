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
	
	public boolean isEmpty(){
		return getPePositions().isEmpty() && getCePositions().isEmpty();
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
	/**
	 * 
	 * @return
	 * First: Position size
	 * Second: Total cost
	 * Third: profit
	 */
	public List<String> peInfo(){
		List<String> data = new ArrayList<String>();
		
		int num = 0;
		double amount=0;
		double prof = 0;
		Position pePos = null;
		if(pePositions!=null){
			int size = pePositions.size();
			if(size>0){
				for(int i=0;i<size;i++){
					pePos = pePositions.get(i);
					num = num + pePos.size();
					amount = amount+(pePos.size()*pePos.getBuy());
					prof = prof + pePos.getProfit();
				}
			}
		}
		data.add(String.valueOf(num));
		data.add(String.valueOf(amount));
		data.add(String.valueOf(prof));
		
		return data;
		
	}
	public List<String> ceInfo(){
		List<String> data = new ArrayList<String>();
		
		int num = 0;
		double amount=0;
		double prof = 0;
		Position cePos = null;
		if(cePositions!=null){
			int size = cePositions.size();
			if(size>0){
				for(int i=0;i<size;i++){
					cePos = cePositions.get(i);
					num = num + cePos.size();
					amount = amount+(cePos.size()*cePos.getBuy());
					prof = prof + cePos.getProfit();
				}
			}
		}
		data.add(String.valueOf(num));
		data.add(String.valueOf(amount));
		data.add(String.valueOf(prof));
		
		return data;
		
	}
	
	public void assignPESell(double sellPrice){
		
		if(pePositions!=null){
			int size = pePositions.size();
			if(size>0){
				for(int i=0;i<size;i++){
					pePositions.get(i).setSell(sellPrice);;
				}
			}
		}
		
	}
	public void assignCESell(double sellPrice){
		
		if(cePositions!=null){
			int size = cePositions.size();
			if(size>0){
				for(int i=0;i<size;i++){
					cePositions.get(i).setSell(sellPrice);;
				}
			}
		}
		
	}	
	
	public double overallProfPer(double pe,double ce){
		double peAvg = peAvg();
		double ceAvg=ceAvg();
		
		if(peAvg==0 && ceAvg==0){
			return 0;
		}
		if(peAvg==0 && ceAvg!=0){
			return ((ce-ceAvg)*100/ceAvg);
		}
		if(peAvg!=0 && ceAvg==0){
			return ((pe-peAvg)*100/peAvg);
		}
		
		return ((pe-peAvg)*100/peAvg)+((ce-ceAvg)*100/ceAvg);
		
		
	}
	public double peAvgFromPreviousClose(double prevPEClose){
		double peAvg = peAvg();
		if(peAvg==0){
			return 0;
		}
		
		return ((peAvg-prevPEClose)*100)/prevPEClose;
	}
	public double ceAvgFromPreviousClose(double prevCEClose){
		double ceAvg = ceAvg();
		if(ceAvg==0){
			return 0; 
		}
		
		return ((ceAvg-prevCEClose)*100)/prevCEClose;
	}
	
	public double peAvg(){
		double pePos = 0.0;
		int count=0;
		if(pePositions!=null){
			int size = pePositions.size();
			if(size>0){
				for(int i=0;i<size;i++){
					pePos = pePos + (pePositions.get(i).getBuy()*pePositions.get(i).size());
					count = count+pePositions.get(i).size();
				}
				
				return pePos/count;
			}
		}
		
		return 0;
		
	}
	public double ceAvg(){
		double cePos = 0.0;
		int count=0;
		if(cePositions!=null){
			int size = cePositions.size();
			if(size>0){
				for(int i=0;i<size;i++){
					cePos = cePos + (cePositions.get(i).getBuy()*cePositions.get(i).size());
					count = count+pePositions.get(i).size();
				}
				
				return cePos/count;
			}
		}
		
		return 0;
		
	}
	
	
	
}
