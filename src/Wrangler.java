import java.util.Stack;
import java.util.ArrayList;
import java.util.ListIterator;
import lejos.nxt.*;

public class Wrangler {

	Odometer odo;
	TwoWheeledRobot patbot;
	ObjectDetection detect;
	double finishX, finishY;
	double redX1, redY1;
	double redX2, redY2;
	double startX, startY;
	int widthX, widthY;
	Stack<Point> currentStack;
	Stack<Point> backPedalStack = new Stack<Point>();
	Navigation navi;
	LightLocalizer localizer;
	UltraDisplay ultra;
	NXTRegulatedMotor gateMotor;
	ArrayList<Point> dangerPointsL = new ArrayList<Point>();
	Point dangerPoints[] = new Point[12];

	public Wrangler(Odometer odo, TwoWheeledRobot patbot,
			ObjectDetection detect, // LightLocalizer localizer,
			UltraDisplay ultra, NXTRegulatedMotor gateMotor) {
		this.odo = odo;
		this.patbot = patbot;
		this.detect = detect;
		this.ultra = ultra;
		this.navi = new Navigation(this.odo/* , this.detect, this.ultra */);
		// this.localizer = localizer;
	}

	public void setStarter(double startX, double startY) {
		this.startX = startX * 30.48;
		this.startY = startY * 30.48;
	}

	public void setFinal(double finishX, double finishY) {
		this.finishX = finishX * 30.48;
		this.finishY = finishY * 30.48;
	}

	public void setArenaSize(int widthX, int widthY) {
		this.widthX = widthX;
		this.widthY = widthY;
	}

	public void createDangerList(double redX1, double redY1, double redX2,
			double redY2) {
		int stepsX = (int) Math.round(Math.abs(redX2 - redX1) / 30.48);
		int stepsY = (int) Math.round(Math.abs(redY2 - redY1) / 30.48);
		for (int i = 0; i < stepsX; i++) {
			for (int j = 0; j < stepsY; i++) {
				this.dangerPointsL.add(new Point(this.redX1 + 30.48 * stepsX,
						this.redY1 + 30.48 * stepsY, false));
			}
		}
	}

	private boolean isDangerous(Point currentPoint,
			ArrayList<Point> dangerousPoints) {
		ListIterator<Point> iter = dangerousPoints.listIterator();
		Point nextPoint;
		while (iter.hasNext()) {
			nextPoint = iter.next();
			if (Point.areSamePoints(currentPoint, nextPoint)) {
				return true;
			}
		}
		return false;
	}

	public Stack<Point> generatePath(boolean setPath, double endPointX,
			double endPointY, double widthX, double widthY,
			Stack<Point> firstStack) {
		Stack<Point> generatedStack = firstStack;
		Stack<Point> backpedalStack = new Stack<Point>();
		double currentX = this.startX;
		double currentY = this.startY;
		double displacementX = endPointX - currentX;
		double displacementY = endPointY - currentY;
		if (setPath) {
			int i;
			if (currentX < (widthX * 30.48) / 2) {
				i = 0;
			} else {
				i = 1;
			}
			while (widthY >= 0) {
				if (i % 2 == 0) {
					for (int j = (int) widthX; j >= 0; j--) {
						generatedStack.push(new Point((j) * 30.48, Math
								.abs(currentY - widthY * 30.48), false));
					}
				} else {
					for (int j = 0; j <= (int) widthX; j++) {
						generatedStack.push(new Point((j) * 30.48, Math
								.abs(currentY - widthY * 30.48), false));
					}
				}
				System.out.println(widthY);
				widthY--;
				i++;
			}
		} else {
			int stepsX = (int) Math.abs(Math.round(displacementX / 30.48));
			int stepsY = (int) Math.abs(Math.round(displacementY / 30.48));
			for (int i = 0; i <= stepsY; i++) {
				if (i % 2 == 0) {
					if (endPointX != (displacementX + currentX)) {
						currentX += displacementX / stepsX;
						generatedStack
								.push(new Point(currentX, currentY, false));
						backpedalStack
								.push(new Point(currentX, currentY, false));
						i++;
					}
				}
				if (i % 2 == 1) {
					if (endPointY != displacementY + currentY) {
						currentY += displacementY / stepsY;
						generatedStack
								.push(new Point(currentX, currentY, false));
						backpedalStack
								.push(new Point(currentX, currentY, false));
						i++;
					}
				}
			}
			while (!backpedalStack.empty()) {
				generatedStack.push(backpedalStack.pop());
			}
		}
		return generatedStack;
	}

	public void runSimpleCourse() {
		// navi.travelTo(0, 0, 15, this.currentStack);
		this.currentStack = new Stack<Point>();
		/*
		 * int widthX = 2; int widthY = 10; double startX = 0; double startY =
		 * 0; double finishX = 1; double finishY = 5;
		 * 
		 * setArenaSize(widthX, widthY); setFinal(finishX, finishY);
		 * setStarter(startX, startY);
		 */

		this.currentStack = generatePath(true, this.finishX, this.finishY,
				this.widthX, this.widthY, this.currentStack);
		this.currentStack = generatePath(false, this.finishX, this.finishY,
				this.widthX, this.widthY, this.currentStack);
		int i = 0, f = 0;
		while (!this.currentStack.isEmpty()) {
			// if (!navi.hasBlock()) {
			if (!isDangerous(this.currentStack.peek(), this.dangerPointsL)) {
				LCD.drawInt((int) (10 * this.currentStack.peek().x), 0, 6);
				LCD.drawInt((int) (10 * this.currentStack.peek().y), 6, 6);
				this.currentStack = navi.travelTo(currentStack.peek().x,
						currentStack.peek().y, 15, currentStack);
				currentStack.peek().setVisited();
				this.backPedalStack.push(currentStack.pop());
				LCD.drawString("Popping", 5, 0);
				i++;
				f = i;
				// }
			} else {
				this.currentStack.pop();
				this.currentStack = generatePath(false, currentStack.peek().x,
						currentStack.peek().y, widthX, widthY,
						this.currentStack);
			}
		}
	}
}
