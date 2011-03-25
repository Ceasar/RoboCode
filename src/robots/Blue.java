package robots;

import guns.GFGunIII;

import java.awt.Color;

import bodies.LameBody;

import robot.CJBRobot;

/*
 * Shooter Bot - For testing guns.
 */

public class Blue extends CJBRobot{

	public Blue(){
		gun = new GFGunIII(this);
		body = new LameBody(this);
	}
	
	public void run() { 
		setColors(Color.BLUE, Color.BLUE, Color.BLUE);
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		do {
			turnRadarRightRadians(Double.POSITIVE_INFINITY); 
		} while (true);
    }

}
