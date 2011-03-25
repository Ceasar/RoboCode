package robots;

import guns.LameGun;

import java.awt.Color;

import bodies.WaveSurfer1;

import robot.CJBRobot;

/*
 * Target bot. For testing guns.
 */

public class Red extends CJBRobot{
	
	public Red(){
		gun = new LameGun(this);
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