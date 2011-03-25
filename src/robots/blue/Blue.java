package robots.blue;

import guns.AngularGun;
import guns.GFGun;
import guns.GFGun2;
import guns.GFGun3;

import java.awt.Color;

import bodies.OrbitalBody;

import robot.CJBRobot;

public class Blue extends CJBRobot{

	public Blue(){
		gun = new GFGun3(this);
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
