package robots.blue;

import guns.AngularGun;

import java.awt.Color;

import robot.CJBRobot;

public class Blue extends CJBRobot{

	public Blue(){
		gun = new AngularGun(this);
		body = new BlueBody(this);
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
