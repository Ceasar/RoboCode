package bodies;

import java.awt.Graphics2D;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robot.Body;
import robot.WaveHitEvent;

/*
 * Does nothing.
 */

public class LameBody implements Body{

	public LameBody(AdvancedRobot robot){
	}


	public void onScannedRobot(ScannedRobotEvent e) {
	}


	@Override
	public void onBulletHit(BulletHitEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onHitByBullet(HitByBulletEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onPaint(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onCustomEvent(WaveHitEvent e) {
		// TODO Auto-generated method stub
		
	}
}
