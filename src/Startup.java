/* Startup.java 
 * This class will contain the main method of the program to be run on the robot during the competition
 */

import lejos.nxt.*;

import java.util.Arrays;

import bluetooth.PlayerRole;
import bluetooth.StartCorner;

public class Startup {
	public static void main(String[] args) {
		boolean useBluetooth = false;

		double finalX = 2, finalY = 7;
		double startX = 0, startY = 0;
		double redX1 = 1, redX2 = 4, redY1 = 2, redY2 = 5;

		if (useBluetooth) {
			int safeZ[];
			int dangerZ[];
			int role;
			int startingCorner;
			
			bluetooth.BluetoothConnection blue = new bluetooth.BluetoothConnection();
			startX = blue.getTransmission().startingCorner.getX();
			startY = blue.getTransmission().startingCorner.getY();
			role = blue.getTransmission().role.getId();
			startingCorner = blue.getTransmission().startingCorner.getId();
			
			if (role == 1) {
				safeZ = blue.getTransmission().greenZone;
				dangerZ = blue.getTransmission().redZone;
			} else if (role == 2) {
				safeZ = blue.getTransmission().redZone;
				dangerZ = blue.getTransmission().greenZone;
			} else {
				safeZ = new int[4];
				dangerZ = new int[4];
			}
			
			LCD.drawString("I'm a " + PlayerRole.lookupRole(role) + "!", 5, 0);
			Sound.systemSound(false, 2);
			finalX = Math.abs(safeZ[2] - safeZ[0]) / 2;
			finalY = Math.abs(safeZ[3] - safeZ[1]) / 2;
			redX1 = dangerZ[0];
			redY1 = dangerZ[1];
			redX2 = dangerZ[2];
			redY2 = dangerZ[3];
			startX = StartCorner.lookupCorner(startingCorner).getX();
			startY = StartCorner.lookupCorner(startingCorner).getY();
		}

		LCD.clear();
		Button.waitForAnyPress();

		TwoWheeledRobot newBot = new TwoWheeledRobot(Motor.A, Motor.B);
		NXTRegulatedMotor gateMotor = Motor.C;
		// Retrieve the bluetooth signal and identify if the robot will be a
		// builder or a garbage collector

		// initiate the two sensors that will be taken in as input for the
		// odometry correction
		final ColorSensor leftLS = new ColorSensor(SensorPort.S1);
		// final ColorSensor rightLS = new ColorSensor(SensorPort.S2);

		// ultrasonic sensor
		final UltrasonicSensor us = new UltrasonicSensor(SensorPort.S3);
		final ColorSensor detectionLS = new ColorSensor(SensorPort.S4);
		// sensor to detect objects

		// Initiate the objects for the odometer, the display and odometry
		// correction
		Odometer odometer = new Odometer(newBot, true);
		// OdometryCorrection odometryCorrection = new
		// OdometryCorrection(odometer, leftLS, rightLS);

		LCDInfo lcd = new LCDInfo(odometer);

		ObjectDetection detect = new ObjectDetection(detectionLS, us);

		// initiate the localization object and perform the localization.
		/*
		 * USLocalizer usl = new USLocalizer(odometer, us,
		 * USLocalizer.LocalizationType.FALLING_EDGE); usl.doLocalization();
		 */
		UltrasonicSensor us2 = new UltrasonicSensor(SensorPort.S3);
		UltraDisplay usd = new UltraDisplay(us2);
		// usd.start();
		// Navigation turn = new Navigation(odometer,detect, usd);
		// turn.turnTo(Math.toRadians(-70));

		// LightLocalizer lsl = new LightLocalizer(odometer, leftLS);
		// lsl.doLocalization();

		Wrangler pathfinder = new Wrangler(odometer, newBot, detect, /* lsl, */
		usd, gateMotor);

		// use Pathfinder here to perform its task.
		// newBot.stop(0);
		// odometer.setTheta(0);
		pathfinder.setFinal(finalX, finalY);
		pathfinder.setArenaSize(2, 6);
		pathfinder.setStarter(startX, startY);
		pathfinder.setRedZone(redX1, redY1, redX2, redY2);
		pathfinder.createDangerList();
		pathfinder.generateSetPath();
		pathfinder.runSimpleCourse();
	}
}
