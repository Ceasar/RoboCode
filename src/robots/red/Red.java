package robots.red;

import guns.AngularGun;

import java.awt.Color;

import bodies.WaveSurfer1;

import robot.CJBRobot;


public class Red extends CJBRobot{
	
	public Red(){
		gun = new RedGun(this);
		body = new WaveSurfer1(this);
	}
	
	public void run() { 
		setColors(Color.RED, Color.RED, Color.RED);
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		do {
			turnRadarRightRadians(Double.POSITIVE_INFINITY); 
		} while (true);
    }

}