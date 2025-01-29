package com.mod.objects;

import java.util.List;

import com.mod.enums.EnumPositionType;
import com.mod.enums.EnumStratergyPositionType;
import com.mod.enums.EnumStratergyType;
import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.mod.support.PositionConfigData;

public class PositionalData {
	
	public static final String POSITION_BUY="BUY";
	public static final String POSITION_SELL="SELL";
	
	private String tradeType;
	
	private String orderType;
	
	private String exchange;
	
	private String tradingSymbol;
	
	private long key;
	
	private int count;
	
	private double buyPrice; 
	
	private double sellPrice;
	
	private double currentSL;
	
	private double expectedSL;
	
	private double systemClosingPoint;
	
	private Candle dayCandle;
	
	private EnumPositionStatus status;
	
	private StopLossType stopLossType;
	
	private List<Double> buyDepth;
	private List<Double> sellDepth;
	
	private boolean gap15mntcheckDone;
	
	private PositionConfigData configData;
	
	private EnumPositionType positionType; 
	
	private EnumStratergyType stratergyType;
	
	private EnumStratergyPositionType stratergyPositionType;
	
	
	private boolean hardSLinPlce;
	
	private int buyQuantity;
	private int sellQuantity;	
	
	private StopLossPosition stopLossPosition;
	
	private boolean isHoldings;
	
	
	
	private PositionMetaData metadata;
	
	
	public PositionalData() {
		// TODO Auto-generated constructor stub
		setDayCandle(new Candle());
		setConfigData(new PositionConfigData());
		setMetadata(new PositionMetaData());
	}
	
	public void reset() {
		setCurrentSL(0);
		setExpectedSL(0);
		setStopLossType(null);
		setGap15mntcheckDone(false);
		getDayCandle().reset();
		setHardSLinPlce(false);
		setPositionType(null);
		getConfigData().clear();

	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Positional data: Buy:"+getBuyPrice()+" Sell:"+getSellPrice()+" Strat:"+getStratergyType()+" Key:"+getKey()+" Future:"+getTradingSymbol()+" Lot:"+getBuyQuantity()+ " | ";
	}
	
	public boolean doesPriceBreaksSL(double price,double SLPercen,double SLinAbsolute,EnumPositionType positionType) {
		
		double newSL=0;
		
		if(SLPercen!=0) {
			
			if(EnumPositionType.Long.equals(positionType) && currentSL==0 && expectedSL==0) {
				newSL = buyPrice *(1-(SLPercen/100));
			}if(EnumPositionType.Short.equals(positionType) && currentSL==0 && expectedSL==0) {
				newSL = buyPrice *(1+(SLPercen/100));
			}
			else if(expectedSL!=0) {
				newSL  = expectedSL;
			}else if(currentSL!=0) {
				newSL = currentSL;
			}
			
			if(newSL==0) {
				throw new RuntimeException("Unable to calculate SL..");
			}
			
			if((EnumPositionType.Long.equals(positionType) && (price < newSL)) || 
					(EnumPositionType.Short.equals(positionType) && (price > newSL))  ) {
				return true;
			}else {
				return false;
			}
			
		}else if(SLinAbsolute!=0) {

			if(EnumPositionType.Long.equals(positionType) && currentSL==0 && expectedSL==0) {
				newSL = SLinAbsolute;
			}if(EnumPositionType.Short.equals(positionType) && currentSL==0 && expectedSL==0) {
				newSL = SLinAbsolute;
			}
			else if(expectedSL!=0) {
				newSL  = expectedSL;
			}else if(currentSL!=0) {
				newSL = currentSL;
			}else {
				throw new RuntimeException("SL cannot be calculated..");
			}
			
			if((EnumPositionType.Long.equals(positionType) && (price < newSL)) && 
					(EnumPositionType.Short.equals(positionType) && (price > newSL))  ) {
				return true;
			}else {
				return false;
			}
			
		}
		if(SLinAbsolute==0 && SLPercen==0)
			throw new RuntimeException(" SL is neither in percentage or absolute");
		
		
		throw new RuntimeException("Something went wrong while SL check");
		
	}

	
	public void checkProfit(double currentPrice, boolean printProfit) {
		
		double profit = 0.00;
		if(getBuyPrice()!=0) {
			if(printProfit) {
				if(currentPrice<=getExpectedSL() && !isHardSLinPlce()) {
					
					setHardSLinPlce(true);
					setCurrentSL(currentPrice);
					
					profit = currentPrice-getBuyPrice();
					System.out.println("Profit:Buy-SLB:R"+getKey()+":"+profit+":Slip:"+getMetadata().getSlippage()
							+":HP:"+getMetadata().getHighestProfit()+":HL:"+getMetadata().getHighestLoss()+":QT:"+getBuyQuantity()+":TC:"
							+(getBuyQuantity()*getBuyPrice())+":PV:"+getBuyPrice()+":Sect:"+getMetadata().getSector());
				}else {
					
					if(isHardSLinPlce()) {
						profit = getCurrentSL()-getBuyPrice();
					}else {
						profit = currentPrice-getBuyPrice();
					}
					
					System.out.println("Profit:Buy:R"+getKey()+":"+profit+":Slip:"+getMetadata().getSlippage()
							+":HP:"+getMetadata().getHighestProfit()+":HL:"+getMetadata().getHighestLoss()+":QT:"+getBuyQuantity()+":TC:"
									+(getBuyQuantity()*getBuyPrice())+":PV:"+getBuyPrice()+":Sect:"+getMetadata().getSector());
				}
				
			}
		}
		
		if(getSellPrice()!=0) {
			if(printProfit) {
				if(currentPrice>=getExpectedSL() && !isHardSLinPlce()) {
					
					setHardSLinPlce(true);
					setCurrentSL(currentPrice);
					
					profit = getSellPrice()-currentPrice;
					System.out.println("Profit:Sell-B:R"+getKey()+":"+profit+":Slip:"+getMetadata().getSlippage()
							+":HP:"+getMetadata().getHighestProfit()+":HL:"+getMetadata().getHighestLoss()+":QT:"+getSellQuantity()+":TC:"
									+(getSellQuantity()*getSellPrice())+":PV:"+getSellPrice()+":Sect:"+getMetadata().getSector());
				}else {
					
					if(isHardSLinPlce()) {
						profit = getSellPrice()-getCurrentSL();
					}else {
						profit = getSellPrice()-currentPrice;
					}
					
					System.out.println("Profit:Sell:R"+getKey()+":"+profit+":Slip:"+getMetadata().getSlippage()
							+":HP:"+getMetadata().getHighestProfit()+":HL:"+getMetadata().getHighestLoss()+":QT:"+getSellQuantity()+":TC:"
									+(getSellQuantity()*getSellPrice())+":PV:"+getSellPrice()+":Sect:"+getMetadata().getSector());
				}
			}
			
		}

		if(profit>getMetadata().getHighestProfit()) {
			getMetadata().setHighestProfit(profit);
		}
		
		if(profit<getMetadata().getHighestLoss()) {
			getMetadata().setHighestLoss(profit);
		}
		
		
		
	}
	
	public double getExpectedSL() {
		return expectedSL;
	}


	public void setExpectedSL(double expectedSL) {
		//this.expectedSL = Math.round(expectedSL);
		this.expectedSL = expectedSL;
	}


	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public double getCurrentSL() {
		return currentSL;
	}

	public void setCurrentSL(double currentSL) {
		this.currentSL = currentSL;
	}

	public Candle getDayCandle() {
		return dayCandle;
	}

	public void setDayCandle(Candle dayCandle) {
		this.dayCandle = dayCandle;
	}

	public List<Double> getBuyDepth() {
		return buyDepth;
	}

	public void setBuyDepth(List<Double> buyDepth) {
		this.buyDepth = buyDepth;
	}

	public List<Double> getSellDepth() {
		return sellDepth;
	}

	public void setSellDepth(List<Double> sellDepth) {
		this.sellDepth = sellDepth;
	}

	public EnumPositionStatus getStatus() {
		return status;
	}

	public void setStatus(EnumPositionStatus status) {
		if(EnumPositionStatus.PositionClosed_WithBuy.equals(this.status) 
				|| EnumPositionStatus.PositionClosed_WithSell.equals(this.status)) {
			throw new RuntimeException("Cannot change status. Position is already closed:Key:"+getKey()+" New Status:"+status);
		}
		this.status = status;
	}

	public double getSystemClosingPoint() {
		return systemClosingPoint;
	}

	public void setSystemClosingPoint(double systemClosingPoint) {
		this.systemClosingPoint = systemClosingPoint;
	}


	public StopLossType getStopLossType() {
		return stopLossType;
	}


	public void setStopLossType(StopLossType stopLossType) {
		this.stopLossType = stopLossType;
	}



	public boolean isGap15mntcheckDone() {
		return gap15mntcheckDone;
	}



	public void setGap15mntcheckDone(boolean gap15mntcheckDone) {
		this.gap15mntcheckDone = gap15mntcheckDone;
	}



	public PositionConfigData getConfigData() {
		return configData;
	}



	public void setConfigData(PositionConfigData configData) {
		this.configData = configData;
	}



	public int getCount() {
		return count;
	}



	public void setCount(int count) {
		this.count = count;
	}



	public EnumPositionType getPositionType() {
		return positionType;
	}



	public void setPositionType(EnumPositionType positionType) {
		this.positionType = positionType;
	}



	public EnumStratergyType getStratergyType() {
		return stratergyType;
	}



	public void setStratergyType(EnumStratergyType stratergyType) {
		this.stratergyType = stratergyType;
	}



	public EnumStratergyPositionType getStratergyPositionType() {
		return stratergyPositionType;
	}



	public void setStratergyPositionType(EnumStratergyPositionType stratergyPositionType) {
		this.stratergyPositionType = stratergyPositionType;
	}





	public int getBuyQuantity() {
		return buyQuantity;
	}



	public void setBuyQuantity(int buyQuantity) {
		
		this.buyQuantity = buyQuantity;
	}



	public int getSellQuantity() {
		return sellQuantity;
	}



	public void setSellQuantity(int sellQuantity) {
		
		this.sellQuantity = sellQuantity;
	}



	public boolean isHardSLinPlce() {
		return hardSLinPlce;
	}



	public void setHardSLinPlce(boolean hardSLinPlce) {
		this.hardSLinPlce = hardSLinPlce;
	}






	public String getExchange() {
		return exchange;
	}



	public void setExchange(String exchange) {
		this.exchange = exchange;
	}



	public StopLossPosition getStopLossPosition() {
		return stopLossPosition;
	}



	public void setStopLossPosition(StopLossPosition stopLossPosition) {
		this.stopLossPosition = stopLossPosition;
	}



	public String getTradingSymbol() {
		return tradingSymbol;
	}



	public void setTradingSymbol(String tradingSymbol) {
		this.tradingSymbol = tradingSymbol;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public boolean isHoldings() {
		return isHoldings;
	}

	public void setHoldings(boolean isHoldings) {
		this.isHoldings = isHoldings;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}


	public PositionMetaData getMetadata() {
		return metadata;
	}

	public void setMetadata(PositionMetaData metadata) {
		this.metadata = metadata;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
	
	

}
