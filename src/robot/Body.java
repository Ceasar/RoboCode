package robot;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;

public interface Body {
	
	static final double BATTLE_FIELD_WIDTH = 800;
	static final double BATTLE_FIELD_HEIGHT = 600;
	static final double WALL_MARGIN = 18;
	static final Rectangle2D FIELD = new Rectangle2D.Double(WALL_MARGIN, WALL_MARGIN, BATTLE_FIELD_WIDTH - WALL_MARGIN * 2, BATTLE_FIELD_HEIGHT - WALL_MARGIN * 2);
	
	public void onScannedRobot(ScannedRobotEvent e);
	
	public void onBulletHit(BulletHitEvent e);
	
	public void onHitByBullet(HitByBulletEvent e);
	
	public void onPaint(Graphics2D g);
	
	public void onCustomEvent(WaveHitEvent e);

}
