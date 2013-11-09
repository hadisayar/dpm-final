import lejos.nxt.ColorSensor;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;

/* 
 * OdometryCorrection.java
 */

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private ColorSensor leftLS;
	private ColorSensor rightLS;
	static final ColorSensor colorSensor = new ColorSensor(SensorPort.S1); //remove this later
	
	private int firstLight = 0, secondLight = 0;
	private boolean deltaLight;
	private int currentCount = 0, previousCount = 0;
	private int actualCount;
	private double x = 0;
	private double y = 0;

	private double sensorCalibration = 3.7;

	// constructor
	public OdometryCorrection(Odometer odometer, ColorSensor leftLS, ColorSensor rightLS) {
		this.odometer = odometer;
		this.leftLS = leftLS;
		this.rightLS = rightLS;
		firstLight = colorSensor.getNormalizedLightValue();
	}

	public int getCount() {
		return actualCount;
	}

	public int getDeltaLight() {
		return secondLight - firstLight;
	}

	public int getCurrentLightValue() {
		return colorSensor.getNormalizedLightValue();
	}


	//Modifiy for all the cases of odometry correction (make sure its not hardcoded)
	//ESSENTIALLY HAVE TO REWRITE THE RUN METHOD.
	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		while (true) {
			correctionStart = System.currentTimeMillis();
			
			// put your correction code here
			// colorSensor.setFloodlight(ColorSensor.BLUE);
			secondLight = colorSensor.getNormalizedLightValue();
			deltaLight = (secondLight - firstLight) > 10
					&& (secondLight - firstLight) < 25;

			if (deltaLight && previousCount == 0 && currentCount == 0) {
				Sound.beep();
				currentCount++;
				actualCount++;
				switch (actualCount) {
				
				case 0:
					break;
				case 1:
					odometer.setY(15 - sensorCalibration);
					break;
				case 2:
					odometer.setY(45 - sensorCalibration);
					break;
				case 3:
					odometer.setX(15 - sensorCalibration);
					break;
					// odometer.setY(40.3);
				case 4:
					odometer.setX(45 - sensorCalibration);
					break;
					// odometer.setY(10.3);
				case 5:
					// odometer.setX(0);
					odometer.setY(45 - sensorCalibration);
					break;
				case 6:
					// odometer.setX(0);
					odometer.setY(15 - sensorCalibration);
					break;
				case 7:
					odometer.setX(45 - sensorCalibration);
					break;
					// odometer.setY(10.3);
				case 8:
					odometer.setX(15 - sensorCalibration);
					break;
					// odometer.setY(10.3);
				default:
					break;
				}
				previousCount = 1;
			} else if (deltaLight == false && previousCount == 1 && currentCount == 0) {
				previousCount = 0;
			} else if (deltaLight == false) {
				currentCount = 0;
			}
			firstLight = secondLight;

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
}
