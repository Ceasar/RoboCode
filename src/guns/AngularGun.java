package guns;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robot.Gun;

public class AngularGun implements Gun{
	
	private static final double BULLET_POWER = 1.92;
	
	private AdvancedRobot robot;
	private double oldEnemyHeading = 0;
	
	public AngularGun(AdvancedRobot robot){
		this.robot = robot;
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		double myX = robot.getX();
		double myY = robot.getY();
		double absoluteBearing = robot.getHeadingRadians() + e.getBearingRadians();
		double enemyX = myX + e.getDistance() * Math.sin(absoluteBearing);
		double enemyY = myY + e.getDistance() * Math.cos(absoluteBearing);
		double enemyHeading = e.getHeadingRadians();
		double enemyHeadingChange = enemyHeading - oldEnemyHeading;
		double enemyVelocity = e.getVelocity();
		oldEnemyHeading = enemyHeading;
		 
		double deltaTime = 0;
		double predictedX = enemyX, predictedY = enemyY;
		while((++deltaTime) * (20.0 - 3.0 * BULLET_POWER) < Point2D.Double.distance(myX, myY, predictedX, predictedY)){		
			predictedX += Math.sin(enemyHeading) * enemyVelocity;
			predictedY += Math.cos(enemyHeading) * enemyVelocity;
			enemyHeading += enemyHeadingChange;
			if(	predictedX < 18.0 
				|| predictedY < 18.0
				|| predictedX > BATTLE_FIELD_WIDTH - 18.0
				|| predictedY > BATTLE_FIELD_HEIGHT - 18.0){
		 
				predictedX = Math.min(Math.max(18.0, predictedX), 
						BATTLE_FIELD_WIDTH - 18.0);	
				predictedY = Math.min(Math.max(18.0, predictedY), 
						BATTLE_FIELD_HEIGHT - 18.0);
				break;
			}
		}
		double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX - myX, predictedY - myY));
		 
		robot.setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - robot.getRadarHeadingRadians()));
		robot.setTurnGunRightRadians(Utils.normalRelativeAngle(theta - robot.getGunHeadingRadians()));
		robot.fire(3);
		
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
}
