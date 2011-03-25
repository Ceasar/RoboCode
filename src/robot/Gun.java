package robot;

import java.awt.Graphics2D;

import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;

public interface Gun {
	
	static final double BATTLE_FIELD_WIDTH = 800;
	static final double BATTLE_FIELD_HEIGHT = 600;
	static final double WALL_MARGIN = 18;

	public void onScannedRobot(ScannedRobotEvent e);
	
	public void onBulletHit(BulletHitEvent e);
	
	public void onHitByBullet(HitByBulletEvent e);

	public void onPaint(Graphics2D g);

	public void onWaveHitEvent(WaveHitEvent e);

}
