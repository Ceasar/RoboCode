package bodies;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robot.Body;
import robot.RobotUtils;
import robot.WaveHitEvent;

public class WaveSurfer1 implements Body{

	private static final double MAX_TRIES = 125;
	private static final double REVERSE_TUNER = 0.421075;
	private static final double DEFAULT_EVASION = 1.0;
	private static final double WALL_BOUNCE_TUNER = 0.699484;

	private AdvancedRobot robot;
	private double enemyFirePower = 3;
	private int hitsTaken = 0;
	private double direction = 0.4;
	private double lastEnemyEnergy = 100;

	public WaveSurfer1(AdvancedRobot robot){
		this.robot = robot;
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		double eEnergy = e.getEnergy();
		if (eEnergy != lastEnemyEnergy) {
			//WaveHitEvent wave = new WaveHitEvent(robot, enemyFirePower);
		}
		double tries = 0;
		direction = 1.0 - (robot.getEnergy() / e.getEnergy());
		double enemyAbsoluteBearing = robot.getHeadingRadians() + e.getBearingRadians();
		double enemyDistance = e.getDistance();
		Point2D robotLocation = new Point2D.Double(robot.getX(), robot.getY());
		
		Point2D enemyLocation = RobotUtils.project(robotLocation, enemyAbsoluteBearing, enemyDistance);
		double angle = enemyAbsoluteBearing + Math.PI + direction; //Attempts to orbit the opponent, moving perpendicularly.
		double distance = enemyDistance * (DEFAULT_EVASION - tries / 100.0);
		Point2D robotDestination = RobotUtils.project(enemyLocation, angle, distance);
		while (!FIELD.contains(robotDestination) && tries < MAX_TRIES) {
			tries++;
			distance = enemyDistance * (DEFAULT_EVASION - tries / 100.0);
			robotDestination = RobotUtils.project(enemyLocation, angle, distance);
		}
		double x = (RobotUtils.bulletVelocity(enemyFirePower) / REVERSE_TUNER) / enemyDistance;
		double y = enemyDistance / RobotUtils.bulletVelocity(enemyFirePower) / WALL_BOUNCE_TUNER;
		if (Math.random() < x || tries > y) {
			direction = -direction;
		}
		angle = RobotUtils.absoluteBearing(robotLocation, robotDestination) - robot.getHeadingRadians();
		robot.setAhead(Math.cos(angle) * 100);
		robot.setTurnRightRadians(Math.tan(angle));
	}

	@Override
	public void onBulletHit(BulletHitEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHitByBullet(HitByBulletEvent e) {
		double totalDamage = enemyFirePower * hitsTaken;
		hitsTaken++;
		enemyFirePower = (totalDamage + e.getPower()) / hitsTaken;
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
