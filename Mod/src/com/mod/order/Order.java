package com.mod.order;

import com.mod.interfaces.KiteInterface;

public class Order {
	private static final KiteInterface kiteInterface = new KiteInterface();
	public static void orderOption(){
		kiteInterface.notify();
	}

}
