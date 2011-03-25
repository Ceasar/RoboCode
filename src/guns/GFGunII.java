package guns;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robot.CJBRobot;
import robot.Gun;
import robot.RobotUtils;
import robot.WaveHitEvent;

/*DESCRIPTION
 * A gun implementing basic guess factor targeting with an added dimension of distance.
 * Also features the minimum number of segments to hit.
 * KNOWN WEAKNESS: Needs to keep track of lateral direction or velocity of target.
 */

public class GFGunII implements Gun{
	private static final double BULLET_POWER = 3;
	private static final double MAX_DISTANCE = Math.max(BATTLE_FIELD_HEIGHT, BATTLE_FIELD_WIDTH);
	private static final double MAX_ESCAPE_ANGLE = maxEscapeAngle(BULLET_POWER);
	private static final double BOT_WIDTH = 36;
	private static final double DISTANCE_METRIC = ((BOT_WIDTH * 2) - 1) / Math.tan(MAX_ESCAPE_ANGLE);

	private CJBRobot robot;
	private int lateralDirection = 0;

	private static int[][] bins = new int[(int) Math.ceil(MAX_DISTANCE / DISTANCE_METRIC)][];

	public GFGunII(CJBRobot robot){
		this.robot = robot;
		if (bins[0] == null){
			for (int i = 0; i < bins.length; i++){
				bins[i] = new int[i+1];
			}
		}
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
		int[] bin = bins[distanceIndex];
		int middleBin = (bin.length - 1) / 2;
		double guessFactor = (double)(bestIndex - middleBin) / Math.max(1, middleBin);
		double angleOffset = lateralDirection * guessFactor * MAX_ESCAPE_ANGLE;
		double gunAdjust = Utils.normalRelativeAngle(enemyAbsoluteBearing - robot.getGunHeadingRadians() + angleOffset);
		robot.setTurnGunRightRadians(gunAdjust);
		if (robot.setFireBullet(BULLET_POWER) != null) {
			WaveHitEvent wave = new WaveHitEvent(robot, BULLET_POWER);
			wave.distance = enemyDistance;
			wave.absBearing = enemyAbsoluteBearing;
			wave.lateralDirection = lateralDirection;
			robot.addCustomEvent(wave);
		}
		robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - robot.getRadarHeadingRadians()) * 2);
	}

	private int mostVisitedBin(double distance) {
		int distanceIndex = (int) (distance / DISTANCE_METRIC);
		int[] bin = bins[distanceIndex];
		int mostVisited = (bin.length - 1) / 2;
		for (int i = 0; i < bin.length; i++) {
			if (bin[i] > bin[mostVisited]) mostVisited = i;
		}
		return mostVisited;
	}

	public static double maxEscapeAngle(double power){
		return Math.asin(8 / RobotUtils.bulletVelocity(power));
	}

	public void onWaveHitEvent(WaveHitEvent e) {
		int distanceIndex = (int) (e.distance / DISTANCE_METRIC);
		System.out.println("distanceIndex: " + distanceIndex);
		double correctBearing = RobotUtils.absoluteBearing(e.source, WaveHitEvent.targetLocation);
		System.out.println("eBearing: " + e.absBearing);
		System.out.println("Correct Bearing: " + correctBearing);
		double angleOffset = Utils.normalRelativeAngle(correctBearing - e.absBearing);
		System.out.println("Offset: " + angleOffset);
		double guessFactor = Math.max(-1, Math.min(1, angleOffset / MAX_ESCAPE_ANGLE)) * e.lateralDirection;
		System.out.println("Guess Factor: " + guessFactor);
		int[] bin = bins[distanceIndex];
		int guessFactor0 = (bin.length - 1) / 2;
		System.out.println("Guess Factor0: " + guessFactor0);
		int index = (int) (guessFactor0 * (guessFactor + 1));
		System.out.println("index: " + index);
		System.out.println("length: " + bin.length);
		bin[index]++;
		robot.removeCustomEvent(e);

	}

	@Override
	public void onPaint(Graphics2D g) {
		g.setColor(Color.RED);
		g.drawLine((int)robot.getX(), (int)robot.getY(), (int)WaveHitEvent.targetLocation.getX(), (int)WaveHitEvent.targetLocation.getY());
		g.setColor(Color.GREEN);
		String str;
		for (int bin1[] : bins){
			str = "";
			g.drawString(str, 10, bin1.length * 20);
			for (int bin : bin1){
				str += bin + " ";
			}
			g.drawString(str, 10, bin1.length * 20);
		}


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
