/* Startup.java 
 * This class will contain the main method of the program to be run on the robot during the competition
 */

import lejos.nxt.*;

public class Startup {
	public static void main(String[] args) {
		boolean useBluetooth = true;
		double finalX = 5.5, finalY = 5.5;
		
		if (useBluetooth) {
			bluetooth.BluetoothConnection blue = new bluetooth.BluetoothConnection();
			int[] safeZ = blue.getTransmission().greenZone;
			Sound.systemSound(false, 2);
			finalX = Math.abs(safeZ[2] - safeZ[0]) / 2;
			finalY = Math.abs(safeZ[3] - safeZ[1]) / 2;
		}
		
		LCD.clear();
		//Button.waitForAnyPress();
		
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
		USLocalizer usl = new USLocalizer(odometer, us,
				USLocalizer.LocalizationType.FALLING_EDGE);
		usl.doLocalization();
		UltrasonicSensor us2 = new UltrasonicSensor(SensorPort.S3);
		UltraDisplay usd = new UltraDisplay(us2);
		usd.start();
		Navigation turn = new Navigation(odometer,detect, usd);
		turn.turnTo(Math.toRadians(-70));

		LightLocalizer lsl = new LightLocalizer(odometer, leftLS);
		lsl.doLocalization();
		
		Pathfinder pathfinder = new Pathfinder(odometer, newBot, detect, /*lsl,*/
				usd, gateMotor);

		// use Pathfinder here to perform its task.
		//newBot.stop(0);
		odometer.setTheta(0);
		pathfinder.setFinal(finalX, finalY);
		pathfinder.runCourse(7, 7);
	}

}
