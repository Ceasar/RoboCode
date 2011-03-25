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
 * A gun implementing basic guess factor targeting with an added dimension of distance.
 * Also features the minimum number of segments to hit.
 */


public class GFGun3 implements Gun{
	private static final double BULLET_POWER = 1.92;
	private static final double MAX_DISTANCE = Math.max(BATTLE_FIELD_HEIGHT, BATTLE_FIELD_WIDTH);
	private static final double MAX_ESCAPE_ANGLE = maxEscapeAngle(BULLET_POWER);
	private static final double BOT_WIDTH = 36.0;
	private static final double ERROR = BULLET_POWER;
	private static final double DISTANCE_METRIC = ((BOT_WIDTH) - ERROR) / Math.tan(MAX_ESCAPE_ANGLE);
	private static final double MAX_VELOCITY = 8.0;
	private static final double VELOCITY_METRIC = 2.0;
	private static final double MAX_GUN_HEAT = 1.0 + BULLET_POWER / 5.0;
	private static final int distanceBinCount = (int) Math.ceil(MAX_DISTANCE / DISTANCE_METRIC);
	private static final int velocityBinCount = (int) Math.ceil((MAX_VELOCITY / VELOCITY_METRIC) * 2 + 1);

	private CJBRobot robot;
	private int lateralDirection = 0;

	private static int[][][] bins = null;

	public GFGun3(CJBRobot robot){
		this.robot = robot;
		if (bins == null){
			bins = new int[velocityBinCount][distanceBinCount][];
			for (int velocityBin = 0;  velocityBin < bins.length;  velocityBin++){
				int[][] distanceBins = bins[velocityBin];
				for (int distanceBin = 0; distanceBin < distanceBins.length; distanceBin++){
					bins[velocityBin][distanceBin] = new int[distanceBin+1];
				}
			}
		}
	}

	double enemyBearing;
	double enemyAbsoluteBearing;
	double enemyDistance;
	double enemyVelocity;
	double gunAdjust;
	double gunHeat;
	double binAngle;
	public void onScannedRobot(ScannedRobotEvent e) {
		enemyBearing = e.getBearingRadians();
		enemyAbsoluteBearing = robot.getHeadingRadians() + enemyBearing;
		enemyDistance = e.getDistance();
		enemyVelocity = e.getVelocity();
		if (enemyVelocity != 0) {
			lateralDirection = RobotUtils.sign(enemyVelocity * Math.sin(e.getHeadingRadians() - enemyAbsoluteBearing));
		}

		Point2D robotLocation = new Point2D.Double(robot.getX(), robot.getY());
		Point2D targetLocation = RobotUtils.project(robotLocation, enemyAbsoluteBearing, enemyDistance);
		WaveHitEvent.targetLocation = targetLocation;

		//Fire
		int velocityIndex = (int) (enemyVelocity / VELOCITY_METRIC) + 4;
		int distanceIndex = (int) (enemyDistance / DISTANCE_METRIC);
		int bestIndex = mostVisitedBin(enemyVelocity, enemyDistance);
		int[] bin = bins[velocityIndex][distanceIndex];
		binAngle = binAngle(bin);
		double guessFactor = -1.0 + bestIndex * binAngle + (binAngle / 2.0);
		double angleOffset = lateralDirection * guessFactor * MAX_ESCAPE_ANGLE;
		gunAdjust = Utils.normalRelativeAngle(enemyAbsoluteBearing - robot.getGunHeadingRadians() + angleOffset);
		robot.setTurnGunRightRadians(gunAdjust * 1.5);
		gunHeat = robot.getGunHeat();
		if (gunHeat == 0 /*&& Math.abs(gunAdjust) < acceptableError()*/ && robot.setFireBullet(BULLET_POWER) != null) {
			makeWave();
		}
		robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - robot.getRadarHeadingRadians()) * 2);
	}
	
	public void makeWave(){
		WaveHitEvent wave = new WaveHitEvent(robot, BULLET_POWER);
		wave.distance = enemyDistance;
		wave.velocity = enemyVelocity;
		wave.shotAngle = robot.getGunHeadingRadians();
		wave.absBearing = enemyAbsoluteBearing;
		wave.lateralDirection = lateralDirection;
		wave.binAngle = binAngle;
		wave.mea = maxEscapeAngle(BULLET_POWER);
		robot.addCustomEvent(wave);
	}
	
	public double acceptableError(){
		return Math.atan((ERROR / 2.0) / enemyDistance);
	}
	
	
	public double binAngle(int[] bin){
		return 2.0 / bin.length;
	}
	
	public int middleBin(int[] bin){
		int middle = 0;
		if (bin.length % 2 == 0)
			middle = bin.length / 2;
		else
			middle = (bin.length - 1) / 2;
		return middle;
	}

	private int mostVisitedBin(double velocity, double distance) {
		int velocityIndex = (int) (velocity / VELOCITY_METRIC) + 4;
		int distanceIndex = (int) (distance / DISTANCE_METRIC);
		int[] bin = bins[velocityIndex][distanceIndex];
		int mostVisited = middleBin(bin);
		for (int i = 0; i < bin.length; i++) {
			if (bin[i] > bin[mostVisited]) mostVisited = i;
		}
		return mostVisited;
	}

	public static double maxEscapeAngle(double power){
		return Math.asin(MAX_VELOCITY / RobotUtils.bulletVelocity(power));
	}

	public void onWaveHitEvent(WaveHitEvent e) {
		int velocityIndex = (int) (e.velocity / VELOCITY_METRIC) + 4;
		int distanceIndex = (int) (e.distance / DISTANCE_METRIC);
		double correctBearing = RobotUtils.absoluteBearing(e.source, WaveHitEvent.targetLocation);
		double angleOffset = Utils.normalRelativeAngle(correctBearing - e.absBearing);
		double guessFactor = Math.max(-1, Math.min(1, angleOffset / MAX_ESCAPE_ANGLE)) * e.lateralDirection;
		System.out.println("Hit: " + guessFactor);
		int[] bin = bins[velocityIndex][distanceIndex];
		double binAngle = binAngle(bin);
		int index = (int) ((guessFactor + 1.0 - binAngle / 2.0) / binAngle);
		bin[index]++;
		robot.removeCustomEvent(e);

	}

	@Override
	public void onPaint(Graphics2D g) {
		int dIndex = (int) (enemyDistance / DISTANCE_METRIC);
		int vIndex = (int) (enemyVelocity / VELOCITY_METRIC + 4);
		int x = (int) robot.getX(); int y = (int) robot.getY();
		double x2 = WaveHitEvent.targetLocation.getX(); double y2 = WaveHitEvent.targetLocation.getY();
		Point2D source = new Point2D.Double(x, y);
		Point2D midpoint = RobotUtils.project(source, enemyAbsoluteBearing, enemyDistance / 2);
		
		g.setColor(Color.GRAY);
		g.drawLine(x, y, (int) x2, (int) y2); //Line to target
		
		g.setColor(new Color((int) (255 * gunHeat / MAX_GUN_HEAT), 0, (int) (255 * (1.0 - gunHeat / MAX_GUN_HEAT) )));
		Point2D laser2 = RobotUtils.project(source, robot.getGunHeadingRadians(), enemyDistance);
		g.drawLine(x, y, (int) laser2.getX(), (int) laser2.getY()); //Actual gun heading. "Cools down".
		
		for (int i = 1; i < distanceBinCount; i++){
			g.setColor(Color.GRAY.darker());
			g.drawOval((int)(x - DISTANCE_METRIC * i), (int)(y - DISTANCE_METRIC * i), (int)DISTANCE_METRIC * i * 2, (int)DISTANCE_METRIC * i * 2);
		}
		g.setColor(Color.GREEN);
		g.drawString("gunAdjust: " + gunAdjust + " " + acceptableError(), 10, 50);
		g.drawString(enemyVelocity + "", (int)WaveHitEvent.targetLocation.getX(), (int)WaveHitEvent.targetLocation.getY());
		g.drawString(enemyDistance + "", (int) midpoint.getX(), (int) midpoint.getY());
		g.drawString("metric: " + DISTANCE_METRIC, 10, 20);
	
		int[] bin = bins[vIndex][dIndex];
		String str = "[";
		for (int i = 0; i <= bin.length; i++){
			g.setColor(Color.WHITE);
			double guessFactor = -1.0 + i * binAngle + (binAngle / 2.0);
			double angleOffset = guessFactor * MAX_ESCAPE_ANGLE;
			double bucketCenter = enemyAbsoluteBearing + angleOffset;
			double bucketEdge = bucketCenter - MAX_ESCAPE_ANGLE * binAngle / 2.0;
			Point2D laser = RobotUtils.project(source, bucketEdge, (dIndex + 1) * DISTANCE_METRIC);
			g.drawLine(x, y, (int) laser.getX(), (int) laser.getY());
			if (i < bin.length){
				Point2D bucket = RobotUtils.project(source, bucketCenter, (dIndex + 1) * DISTANCE_METRIC);
				g.drawString("" + bin[i], (int) bucket.getX(), (int) bucket.getY());
				str += bin[i] + ", ";
			}
		}
		str += "]";
		g.drawString("index: " + vIndex + " " + dIndex + " "  + str, 10, 10);
		
		for (WaveHitEvent wave : WaveHitEvent.waves){
			wave.onPaint(g);
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
