package guns;

import java.awt.Color;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.CustomEvent;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robot.CJBRobot;
import robot.Gun;
import robot.RobotUtils;
import robot.WaveHitEvent;

/*DESCRIPTION
 * A gun implementing basic guess factor targeting.
 */

public class GFGun implements Gun{
	private static final double BULLET_POWER = 3;
	private static final double MAX_DISTANCE = 1200;
	private static final double DISTANCE_METRIC = 75;

	private CJBRobot robot;
	private double oldEnemyHeading = 0;
	private int lateralDirection = 0;

	private static int[] bins = new int[15];

	public GFGun(CJBRobot robot){
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
		int distanceIndex = (int) (enemyDistance / DISTANCE_METRIC);
		int bestIndex = mostVisitedBin(enemyDistance);
		int middleBin = (bins.length - 1) / 2;
		double guessFactor = (double)(bestIndex - middleBin) / Math.max(1, middleBin);
		double angleOffset = lateralDirection * guessFactor * maxEscapeAngle();
		double gunAdjust = Utils.normalRelativeAngle(enemyAbsoluteBearing - robot.getGunHeadingRadians() + angleOffset);
		robot.setTurnGunRightRadians(gunAdjust);
		if (robot.setFireBullet(BULLET_POWER) != null) {
			WaveHitEvent wave = new WaveHitEvent(robot, BULLET_POWER);
			wave.distance = enemyDistance;
			wave.bearing = gunAdjust;
			wave.lateralDirection = lateralDirection;
			robot.addCustomEvent(wave);
		}
		robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - robot.getRadarHeadingRadians()) * 2);
	}

	private int mostVisitedBin(double distance) {
		int distanceIndex = (int) (distance / DISTANCE_METRIC);
		int mostVisited = (bins.length - 1) / 2;
		for (int i = 0; i < bins.length; i++) {
			if (bins[i] > bins[mostVisited]) mostVisited = i;
		}
		return mostVisited;
	}

	public double maxEscapeAngle(){
		return Math.asin(8 / RobotUtils.bulletVelocity(BULLET_POWER));
	}

	public void onWaveHitEvent(WaveHitEvent e) {
		int distanceIndex = (int) (e.distance / DISTANCE_METRIC);
		System.out.println("distanceIndex: " + distanceIndex);
		double correctBearing = RobotUtils.absoluteBearing(e.source, e.targetLocation);
		System.out.println("eBearing: " + e.bearing);
		System.out.println("Correct Bearing: " + correctBearing);
		double angleOffset = Utils.normalRelativeAngle(correctBearing - e.bearing);
		System.out.println("Offset: " + angleOffset);
		System.out.println("MEA: " + maxEscapeAngle());
		double guessFactor = Math.max(-1, Math.min(1, angleOffset / maxEscapeAngle())) * e.lateralDirection;
		System.out.println("Guess Factor: " + guessFactor);
		int guessFactor0 = (bins.length - 1) / 2;
		System.out.println("Guess Factor0: " + guessFactor0);
		int index = (int) (guessFactor0 * (guessFactor + 1));
		System.out.println("index: " + index);
		System.out.println("length: " + bins.length);
		bins[index]++;
		robot.removeCustomEvent(e);

	}

	@Override
	public void onPaint(Graphics2D g) {
		g.setColor(Color.RED);
		g.drawLine((int)robot.getX(), (int)robot.getY(), (int)WaveHitEvent.targetLocation.getX(), (int)WaveHitEvent.targetLocation.getY());
		g.setColor(Color.GREEN);
		String str = "";
		for (int bin : bins){
			str += bin + " ";
			
		}
		g.drawString(str, 10, 10);


	}

	@Override
	public void onBulletHit(BulletHitEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHitByBullet(HitByBulletEvent e) {
		// TODO Auto-generated method stub

	}

}
