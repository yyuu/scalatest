package org.scalatest.testng;

import org.testng.annotations._

class AwesomeVolumeKnobTest extends TestNGSuite{

  @DataProvider{val name="good volumes"}
  def goodVolumes = Array(v(11), v(20),v(30),v(40),v(50),v(60),v(70),v(80),v(90),v(100))
  
  @DataProvider{val name="low volumes"}
  def lowVolumes = Array(v(1), v(2),v(3),v(4),v(5),v(6),v(7),v(8),v(9),v(10))
  
  def v( i: Integer ): Array[Integer] = Array(i)
  @Test{ val dataProvider="low volumes", 
         val expectedExceptions = Array( classOf[IllegalArgumentException] )}
  def awesomeVolumeKnobsWithLowMaxVolumesCantExist(maxVolume: int){
    new AwesomeVolumeKnob(maxVolume);  
  }  
  
  @Test{ val dataProvider="good volumes"}
  def awesomeVolumeKnobsWithHighVolumesAreCool(maxVolume: int){
    new AwesomeVolumeKnob(maxVolume);  
  } 

  @Test{ val dataProvider="good volumes", 
    val expectedExceptions = Array( classOf[IllegalAccessError] )}
  def awesomeVolumeKnobsCanNeverBeTurnedDown(maxVolume: int){
    new AwesomeVolumeKnob(maxVolume).turnDown();
  } 
 
}
