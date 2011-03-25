package robots.blue;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robot.Body;
import robot.RobotUtils;
import robot.WaveHitEvent;

public class BlueBody implements Body{

	private static final double MAX_TRIES = 125;
	private static final double REVERSE_TUNER = 0.421075;
	private static final double DEFAULT_EVASION = 1.2;
	private static final double WALL_BOUNCE_TUNER = 0.699484;

	private AdvancedRobot robot;
	private Rectangle2D fieldRectangle = new Rectangle2D.Double(WALL_MARGIN, WALL_MARGIN,
			BATTLE_FIELD_WIDTH - WALL_MARGIN * 2, BATTLE_FIELD_HEIGHT - WALL_MARGIN * 2);
	private double enemyFirePower = 3;
	private double direction = 0.4;

	public BlueBody(AdvancedRobot robot){
		this.robot = robot;
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
