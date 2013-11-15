/* LightLocalizer.java
 * This class is used to allow the robot to localize itself to (0, 0, 0.0) using the light sensor
 * 
 * Written by:
 * Hadi Sayar, Student ID: 260531679 
 * Antonio D'Aversa, Student ID: 260234498
 */

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Sound;

public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor ls;
	private Navigation nav;
	//
	private int firstLight;
	private int count = 0; //counter
	private double[] angles = {0, 0, 0, 0}; //initialize the angles to 0
	private double d = 11.0; //adjust this
	private double x = 0;
	private double y = 0;
	private double deltaThetaY, deltaThetaX;

	
	public LightLocalizer(Odometer odo, ColorSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		this.nav = new Navigation(odo);
		// turn on the light
		ls.setFloodlight(true);
	}

	public void doLocalization() {
		// drive to location listed in tutorial
		nav.travelTo(Math.sqrt(50), Math.sqrt(50));


		// start rotating and clock all 4 gridlines
		// we will rotate counter-clockwise so that we will measure the y first and third and
		// x will be second and last.
		
		//set the turn speed higher to not detect the same line twice then rotate
		this.robot.getLeftMotor().setSpeed(500);
		this.robot.getRightMotor().setSpeed(500);
		
		robot.rotateCounterClockwise();
		
		while (count < 4) {
			
			firstLight = this.ls.getNormalizedLightValue();
			
			//if the normalize light value is below a certain threshold we have detected a line
			if(firstLight < 540){
				//beep whenever a line is detected.
				Sound.beep();
				//sleep here to not detect the same line twice (may be useless)
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				angles[count] = this.odo.getAng();
				count++;
				LCD.clear(5);
			}
			//stop the robot after the last ine is detected.
			if(count == 4){
				robot.stop();
			}
		}
		
		//reset speeds of the motors
		this.robot.getLeftMotor().setSpeed(200);
		this.robot.getRightMotor().setSpeed(200);
		
		
		// do trig to compute (0,0) and 0 degrees
		
		//Math taken from the tutorial slides.
		deltaThetaX = angles[1] - angles[3];
		deltaThetaY = angles[0] - angles[2];
		
		x = -1 * d * Math.cos(Math.toRadians(deltaThetaY)/2);
		y = -1 * d * Math.cos(Math.toRadians(deltaThetaX)/2);
		
		// when done travel to (0,0) and turn to 0 degrees
		//set current position as temporary origin
		odo.setPosition(new double[] { 0.0, 0.0, odo.getAng() },
				new boolean[] { true, true, true });
		
		//travel to the negative values of x and y to get to the actual origin
		this.nav.travelTo(-x, -y);
		//adjust the angle so that the the robot facing the y direction
		nav.turnTo(Math.toRadians(0));
		
		//reset the position to (0,0,0)
		LCD.clear();
		odo.setPosition(new double[] { 0.0, 0.0, 0.0 },
				new boolean[] { true, true, true });
	}

}
