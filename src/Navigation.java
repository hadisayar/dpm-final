/* Navigation.java
 * This class is used to allow the robot to navigate to specified coordinates using the odometer.
 * 
 * This navigation class was used from lab 3 and modified for the purposes of lab4 to work with the provided odometer.
 * 
 * Written by:
 * Hadi Sayar, Student ID: 260531679 
 * Antonio D'Aversa, Student ID: 260234498
 */

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.LCD;

public class Navigation {
	private TwoWheeledRobot robot;

	private Odometer odo;
	NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	private boolean isNav = false;
	private double currentX = 0, currentY = 0, currentTheta = 0;
	private double deltaX = 0, deltaY = 0, deltaTheta = 0, heading;
	private static final double PI = Math.PI;
	private double wheelRadii, width;
	private double[] pos = new double[3];

	
	// Navigation constructor
	public Navigation(Odometer odometer) {
		this.odo = odometer;
		this.robot = odo.getTwoWheeledRobot();
		wheelRadii = 2.125; // wheel radius

		width = 14.9; // width between wheels

		//set the acceleration to prevent slipping.
		this.robot.getLeftMotor().setAcceleration(1000);
		this.robot.getRightMotor().setAcceleration(1000);
	}


	// Travel to method which determines the distance that robot needs to travel
	public void travelTo(double x, double y) {
		// Sets Navigation to true
		isNav = true;
		// set the motor speeds
		this.robot.setForwardSpeed(250);
		// clear and write to the LCD
		odo.getPosition(pos);
		// Calculate the heading of the robot and turn towards it
		currentX = this.odo.getX();
		currentY = this.odo.getY();
		deltaX = x - currentX;
		deltaY = y - currentY;
		heading = Math.atan2(deltaX, deltaY);
		turnTo(heading);
		this.odo.getPosition(pos);
		
		// Until the robot is at its destination within 1 cm, it will move
		// forward in the heading's direction
		while (Math.abs(x - this.odo.getX()) >= 1.0 || Math.abs(y - this.odo.getY()) >= 1.0) {
			this.robot.getLeftMotor().setSpeed(100);
			this.robot.getRightMotor().setSpeed(100);
			this.robot.getLeftMotor().forward();
			this.robot.getRightMotor().forward();
		}
		
		// When it exits the loop, STOP
		this.robot.stop(1);
		// 1 second cat-nap
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// there is nothing to be done here because it is not expected
			// that the odometer will be interrupted by another thread
		}

		// sets navigation to false when it gets to destination
		isNav = false;

		// UPDATE LCD
		// this.odo.getPosition(pos);
	}

	public void turnTo(double theta) {
		isNav = true;

		// Finds current heading according to odometer
		currentTheta = pos[2];
		deltaTheta = theta - Math.toRadians(currentTheta);

		// computes acute angle (Shortest turn)
		if (deltaTheta < -PI) {
			deltaTheta += 2 * PI;
		} else if (deltaTheta > PI) {
			deltaTheta -= 2 * PI;
		}

		// Performs rotation
		deltaTheta = Math.toDegrees(deltaTheta);
		this.robot.getLeftMotor().rotate(convertAngle(wheelRadii, width, deltaTheta), true);
		this.robot.getRightMotor().rotate(-convertAngle(wheelRadii, width, deltaTheta), false);
	}

	boolean isNavigating() {
		return isNav;
	}

	// Method "borrowed" from SquareDriver.java
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	// Method "borrowed" from SquareDriver.java
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
