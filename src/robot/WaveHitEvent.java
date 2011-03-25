package robot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import robocode.*;

public class WaveHitEvent extends Condition{
	public static Point2D targetLocation;
	public static ArrayList<WaveHitEvent> waves = new ArrayList<WaveHitEvent>();
	
	private CJBRobot robot;
	private final Point2D SOURCE;
	private final double BULLET_VELOCITY;
	private double distanceTraveled;
	
	//to be retrieved later
	public double distance; 
	public double velocity;
	public double absBearing;
	public double lateralDirection;
	
	//Paint stuff
	private Color color;
	private double shotAngle;
	private double mea;
	
	public WaveHitEvent(CJBRobot robot, double power){
		this.robot = robot;
		this.SOURCE = new Point2D.Double(robot.getX(), robot.getY());
		this.BULLET_VELOCITY = RobotUtils.bulletVelocity(power);
		
		waves.add(this);
		color = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
	}
	
	public boolean test(){
		//System.out.print("wave moving");
		distanceTraveled += BULLET_VELOCITY;
		if (targetReached()){
			robot.onWaveHitEvent(this);
			waves.remove(this);
			return true;
		}
		return false;
	}
	
	private boolean targetReached(){
		return distanceTraveled > SOURCE.distance(targetLocation) - 18;
	}
	
	public Point2D getSource(){
		return SOURCE;
	}
	
	public void setUpPaint(double shotAngle, double mea){
		this.shotAngle = shotAngle;
		this.mea = mea;
	}

	public void onPaint(Graphics2D g) {
		g.setColor(color);
		int x = (int) (SOURCE.getX()); int y = (int) (SOURCE.getY());
		
		//Paint bullet path
		Point2D laser = RobotUtils.project(SOURCE, shotAngle, distanceTraveled);
		g.drawLine(x, y, (int)laser.getX(), (int)laser.getY());
		
		//Paint Wave
		int radius = (int) distanceTraveled;
		g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
		
		//Paints maximum escape angle
		Point2D laser2 = RobotUtils.project(SOURCE, mea * lateralDirection + absBearing, distance);
		g.drawLine(x, y, (int)laser2.getX(), (int)laser2.getY());
		Point2D laser3 = RobotUtils.project(SOURCE, mea * -lateralDirection + absBearing, distance);
		g.drawLine(x, y, (int)laser3.getX(), (int)laser3.getY());
		g.drawLine((int)laser2.getX(), (int)laser2.getY(), (int)laser3.getX(), (int)laser3.getY());
	}
}
