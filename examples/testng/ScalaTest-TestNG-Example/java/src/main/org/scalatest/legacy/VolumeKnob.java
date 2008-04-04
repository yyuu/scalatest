package org.scalatest.legacy;

public interface VolumeKnob {

	public abstract int currentVolume();
	
	public abstract int maxVolume();

	public abstract void turnUp();
	
	public abstract void turnDown();
}