package robots.blue;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robot.Gun;
import robot.RobotUtils;
import robot.WaveHitEvent;

public class BlueGun implements Gun{
	private static final double BULLET_POWER = 1.9;

	private static final double MAX_DISTANCE = 1000;
	private static final int DISTANCE_INDEXES = 5;
	private static final double MAX_VELOCITY = 8;
	private static final int VELOCITY_INDEXES = 5;
	
	private AdvancedRobot robot;
	private double oldEnemyHeading = 0;
	private int lateralDirection = 0;

	private static int[][][] statBuffers = new int[DISTANCE_INDEXES][VELOCITY_INDEXES][5];
	private int[] buffer;
	
	public BlueGun(AdvancedRobot robot){
		this.robot = robot;
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		double enemyAbsoluteBearing = robot.getHeadingRadians() + e.getBearingRadians();
		double enemyDistance = e.getDistance();
		double enemyVelocity = e.getVelocity();
		if (enemyVelocity != 0) {
			lateralDirection = RobotUtils.sign(enemyVelocity * Math.sin(e.getHeadingRadians() - enemyAbsoluteBearing));
		}
		
		Point2D robotLocation = new Point2D.Double(robot.getX(), robot.getY());
		Point2D targetLocation = RobotUtils.project(robotLocation, enemyAbsoluteBearing, enemyDistance);
		WaveHitEvent.targetLocation = targetLocation;
		
		//Fire
		robot.setTurnGunRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - robot.getGunHeadingRadians() /*+ wave.mostVisitedBearingOffset()*/));
		robot.setFire(BULLET_POWER);
		if (robot.getEnergy() >= BULLET_POWER) {
			//WaveHitEvent wave = new WaveHitEvent(robot, BULLET_POWER);
			//wave.setSegmentations(enemyDistance, enemyVelocity, lastEnemyVelocity);
			//robot.addCustomEvent(wave);
		}
		robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - robot.getRadarHeadingRadians()) * 2);
	}
	
	private int mostVisitedBin() {
		int bins = buffer.length;
		int mostVisited = buffer[bins / 2];
		for (int i = 0; i < bins; i++) {
			if (buffer[i] > buffer[mostVisited]) mostVisited = i;
		}
		return mostVisited;
	}
	
	double mostVisitedBearingOffset() {
		return mostVisitedBin() - (buffer.length / 2);
	}
	
	public void onCustomEvent(WaveHitEvent e) {
		//Increment the corresponding bin
		double distanceBinWidth = MAX_DISTANCE / DISTANCE_INDEXES;
		int distanceIndex = (int)(e.distance / distanceBinWidth);
		int velocityIndex = (int)Math.abs(e.velocity / 2.0);
		buffer = statBuffers[distanceIndex][velocityIndex];
		double correctBearing = RobotUtils.absoluteBearing(e.source, e.targetLocation) - e.bearing;
		double normalizedAngle = Utils.normalRelativeAngle(correctBearing);
		int bin = (int) (normalizedAngle / buffer.length);
		buffer[bin]++;
		robot.removeCustomEvent(e);

	}

	@Override
	public void onPaint(Graphics2D g) {
		// TODO Auto-generated method stub

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
	public void onWaveHitEvent(WaveHitEvent e) {
		// TODO Auto-generated method stub
		
	}

}
