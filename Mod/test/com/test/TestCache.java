package com.test;

import org.junit.Test;

import com.mod.process.models.CacheService;

import gnu.trove.list.TDoubleList;

public class TestCache {

	@Test
	public void testCache(){
		
		
		for(int i=1;i<7271;i++){
			System.out.println(CacheService.getValueForIndex(256265.0, i));
		}
		
		//TDoubleList list = CacheService.getItemsFromDateDataRecord_Test(256265.0, 1,4);
		
		
	}
}
