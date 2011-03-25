package robot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import robocode.*;

public class WaveHitEvent extends Condition{
	public static Point2D targetLocation;
	public static ArrayList<WaveHitEvent> waves = new ArrayList<WaveHitEvent>();
	
	public Point2D source;
	private double bullet_velocity;
	private double distanceTraveled;
	private Color color;
	
	public double distance; //to be retrieved later
	public double velocity;
	public double shotAngle;
	public double absBearing;
	public double binAngle;
	public double lateralDirection;
	public CJBRobot robot;
	public double mea;
	
	public WaveHitEvent(CJBRobot robot, double power){
		this.robot = robot;
		this.source = new Point2D.Double(robot.getX(), robot.getY());
		this.bullet_velocity = RobotUtils.bulletVelocity(power);
		this.origin = targetLocation;
		waves.add(this);
		color = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
	}
	
	public boolean test(){
		//System.out.print("wave moving");
		distanceTraveled += bullet_velocity;
		if (targetReached()){
			robot.onWaveHitEvent(this);
			waves.remove(this);
			return true;
		}
		return false;
	}
	
	private boolean targetReached(){
		return distanceTraveled > source.distance(targetLocation) - 18;
	}
	
	Point2D origin;
	public void onPaint(Graphics2D g) {
		g.setColor(color);
		int x = (int) (source.getX()); int y = (int) (source.getY());
		Point2D laser = RobotUtils.project(source, shotAngle, distanceTraveled);
		g.drawLine(x, y, (int)laser.getX(), (int)laser.getY());
		g.drawLine((int)targetLocation.getX(), (int)targetLocation.getY(), (int)origin.getX(), (int)origin.getY());
		int radius = (int) distanceTraveled;
		g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
		//MEA
		Point2D laser2 = RobotUtils.project(source, mea * lateralDirection + absBearing, distance);
		g.drawLine(x, y, (int)laser2.getX(), (int)laser2.getY());
		Point2D laser3 = RobotUtils.project(source, mea * -lateralDirection + absBearing, distance);
		g.drawLine(x, y, (int)laser3.getX(), (int)laser3.getY());
		g.drawLine((int)laser2.getX(), (int)laser2.getY(), (int)laser3.getX(), (int)laser3.getY());
	}
}
