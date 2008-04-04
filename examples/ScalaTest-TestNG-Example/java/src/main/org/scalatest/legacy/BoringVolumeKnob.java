package org.scalatest.legacy;

public class BoringVolumeKnob implements VolumeKnob {

	private int currentVolume;
	private final int maxVolume;
	
	public BoringVolumeKnob(int maxVolume){
		if( maxVolume < 1 ) 
			throw new IllegalArgumentException("cant have max volume less than 1!");
		if( maxVolume > 10 ) 
			throw new IllegalArgumentException("cant have max volume greater than 10!");
		this.maxVolume = maxVolume;
		this.currentVolume = 1;
	}

	public int maxVolume() { return maxVolume; }
	
	public int currentVolume() { return currentVolume; }
	
	public void turnDown(){ if( currentVolume > 0 ) currentVolume--;  }

	public void turnUp() { if( currentVolume < maxVolume ) currentVolume++; }
}
