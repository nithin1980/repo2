package com.mod.objects;

import java.util.List;

public class MarketDepthWrapper {

	//Buying people
	private List<MarketDepth> bids;
	
	//selling people
	private List<MarketDepth> offers;

	public List<MarketDepth> getBids() {
		return bids;
	}

	public void setBids(List<MarketDepth> bids) {
		this.bids = bids;
	}

	public List<MarketDepth> getOffers() {
		return offers;
	}

	public void setOffers(List<MarketDepth> offers) {
		this.offers = offers;
	}
	
	
	
}
