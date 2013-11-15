//This class will contains the algorithm that will be used to navigate through the field of obstacles.

import java.util.Stack;
import lejos.nxt.*;

public class Pathfinder {
	Odometer odo;
	TwoWheeledRobot patbot;
	ObjectDetection detect;
	double finishX, finishY;
	Stack<Point> currentStack;
	Stack<Point> backPedalStack;
	Navigation navi;
	LightLocalizer localizer;
	UltraDisplay ultra;
	NXTRegulatedMotor gateMotor;

	public Pathfinder(Odometer odo, TwoWheeledRobot patbot,
			ObjectDetection detect, LightLocalizer localizer,
			UltraDisplay ultra, NXTRegulatedMotor gateMotor) {
		this.odo = odo;
		this.patbot = patbot;
		this.detect = detect;
		this.ultra = ultra;
		this.navi = new Navigation(odo, detect, this.ultra);
		this.localizer = localizer;
	}

	public void setFinal(int finishX, int finishY) {
		this.finishX = finishX;
		this.finishY = finishY;
	}

	public Stack<Point> generatePath(boolean goToFinish, double finishX,
			double finishY, double widthX, double widthY,
			Stack<Point> currentStack) {
		double currentX, currentY, currentHeading;
		double stepX, stepY;
		int stepsReq;
		Stack<Point> courseStack = currentStack;
		Stack<Point> finishStack;
		widthY *= 30.46;
		widthX *= 30.46;
		// Builds a stack of coordinates that the robot will run
		if (goToFinish) {
			// if it has its tower
			currentX = odo.getX();
			currentY = odo.getY();
			finishStack = new Stack<Point>();
			stepsReq = (int) Math.floor(Math.abs(finishX - currentX) / 20.0);
			stepX = (finishX - currentX) / stepsReq;
			stepY = (finishY - currentY) / stepsReq;
			for (int i = 0; i < 2 * stepsReq; i++) {
				if (i % 2 == 0) {
					finishStack.push(new Point(finishX - (i / 2) * stepX,
							finishY, false));
				} else {
					finishStack.push(new Point(finishX, finishY - (i / 2)
							* stepY, false));
				}
			}
			courseStack = finishStack;
		} else {
			currentY = odo.getY();
			currentHeading = odo.getAng();
			stepsReq = (int) Math.floor(Math.abs(widthY - currentY) / 30.46);
			int stepsReqX = (int) Math.floor(Math.abs(widthX) / 30.46);
			stepY = (finishY - currentY) / stepsReq;
			int i;
			if (currentHeading < 90 || currentHeading >= 270) {
				i = 1;
			} else {
				i = 0;
			}
			for (int f = i; f < 2 * stepsReq; f++) {
				if (f % 2 == 0) {
					courseStack.push(new Point(0, widthY - f / 2 * stepY
							+ 30.46, false));
					courseStack
							.push(new Point(0, widthY - f / 2 * stepY, false));
				} else {
					courseStack.push(new Point(widthX, widthY - f / 2 * stepY
							+ 30.46, false));
					courseStack.push(new Point(widthX, widthY - f / 2 * stepY,
							false));
				}
			}
		}
		return courseStack;
	}

	public void runCourse(double widthX, double widthY) {
		this.currentStack = new Stack<Point>();
		this.currentStack = generatePath(false, finishX, finishY, widthX,
				widthY, this.currentStack);
		int i = 0, f = 0;
		while (!currentStack.isEmpty()) {
			if (!navi.hasBlock()) {
				if (i % 2 == 0) {
					localizer.doLocalization();
				}
				currentStack = navi.travelTo(currentStack.peek().x,
						currentStack.peek().y, 15, currentStack);
				currentStack.peek().setVisited();
				backPedalStack.push(currentStack.pop());
				i++;
				f = i;
			} else {
				if (f == i) {
					this.currentStack = generatePath(true, finishX, finishY,
							widthX, widthY, this.currentStack);
					f++;
				} else {
					if (f % 2 == 0) {
						localizer.doLocalization();
					}
					currentStack = navi.travelTo(currentStack.peek().x,
							currentStack.peek().y, 15, currentStack);
					currentStack.peek().setVisited();
					backPedalStack.push(currentStack.pop());
					f++;
				}
			}
		}
		gateMotor.rotate(90);
	}

}
