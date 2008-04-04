package org.scalatest.testng;

import org.scalatest.legacy.VolumeKnob

class AwesomeVolumeKnob( val maxVolume: int ) extends VolumeKnob {

  if( maxVolume < 11 ) throw new IllegalArgumentException("...These go to eleven.");
  
  var currentVolume = maxVolume;
  
  def turnDown = throw new IllegalAccessError("AwesomeVolumeKnobs cannot be turned down");
  
  // AwesomeVolumeKnobs don't care about max volume
  def turnUp = currentVolume = currentVolume + 1;
}
