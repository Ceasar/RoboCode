package robot;

import java.awt.Graphics2D;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;

public abstract class CJBRobot extends AdvancedRobot{

	protected Gun gun;
	protected Body body;

	public abstract void run();

	public void onScannedRobot(ScannedRobotEvent e) {
		gun.onScannedRobot(e);
		body.onScannedRobot(e);
	}

	public void onBulletHit(BulletHitEvent e){
		gun.onBulletHit(e);
		body.onBulletHit(e);
	}

	public void onHitByBullet(HitByBulletEvent e){
		gun.onHitByBullet(e);
		body.onHitByBullet(e);
	}

	public void onPaint(Graphics2D g) {
		gun.onPaint(g);
		body.onPaint(g);
	}

}
