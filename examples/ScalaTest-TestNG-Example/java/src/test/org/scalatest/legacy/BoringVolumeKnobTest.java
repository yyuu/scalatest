package org.scalatest.legacy;

import org.scalatest.legacy.BoringVolumeKnob;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BoringVolumeKnobTest {

	@DataProvider(name="lowVolumes")
	public Integer[][] getLowVolumes(){ 
		return new Integer[][]{ {1},{3},{6},{9},{10} }; 
	}
	
	@DataProvider(name="highVolumes")
	public Integer[][] getGoodVolumes(){ 
		return new Integer[][]{ {11},{100} }; 
	}
	
	@Test(dataProvider="lowVolumes")
	public void boringVolumeKnobsCanBeCreatedWithLowMaxValues(int maxVolume){
		new BoringVolumeKnob(maxVolume);
	}

	@Test(dataProvider="highVolumes", expectedExceptions={IllegalArgumentException.class})
	public void boringVolumeKnobsCantHaveAMaxVolumeAboveTen(int maxVolume){
		new BoringVolumeKnob(maxVolume);
	}	
	
	@Test(dataProvider="lowVolumes")
	public void boringVolumeKnobsCantBeTurnedDownBelowZero(int maxVolume){
		BoringVolumeKnob boringVolumeKnob = new BoringVolumeKnob(maxVolume);
		
		while( boringVolumeKnob.currentVolume() > 0 )
				boringVolumeKnob.turnDown();
		
		Assert.assertEquals(boringVolumeKnob.currentVolume(), 0);
		
		// try to turn it down some more
		boringVolumeKnob.turnDown();
		Assert.assertEquals(boringVolumeKnob.currentVolume(), 0);
		
		// just for good measure
		boringVolumeKnob.turnDown();
		Assert.assertEquals(boringVolumeKnob.currentVolume(), 0);
	}
	
	
	@Test(dataProvider="lowVolumes")
	public void boringVolumeKnobsCantBeTurnedUpAboveTheirMaxVolume(int maxVolume){
		BoringVolumeKnob boringVolumeKnob = new BoringVolumeKnob(maxVolume);
		
		while( boringVolumeKnob.currentVolume() < maxVolume )
				boringVolumeKnob.turnUp();
		
		Assert.assertEquals(boringVolumeKnob.currentVolume(), maxVolume);
		
		// try to turn it up some more
		boringVolumeKnob.turnUp();
		Assert.assertEquals(boringVolumeKnob.currentVolume(), maxVolume);
		
		// just for good measure
		boringVolumeKnob.turnUp();
		Assert.assertEquals(boringVolumeKnob.currentVolume(), maxVolume);
	}
	
	
}
