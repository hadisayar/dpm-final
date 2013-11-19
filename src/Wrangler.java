import java.util.Stack;

import lejos.nxt.*;

public class Wrangler {

	Odometer odo;
	TwoWheeledRobot patbot;
	ObjectDetection detect;
	double finishX, finishY;
	Stack<Point> currentStack;
	Stack<Point> backPedalStack = new Stack<Point>();
	Navigation navi;
	LightLocalizer localizer;
	UltraDisplay ultra;
	NXTRegulatedMotor gateMotor;

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

	public void setFinal(int finishX, int finishY) {
		this.finishX = -finishX * 30.46;
		this.finishY = -finishY * 30.46;
	}

	public static Stack<Point> generatePath(boolean goToFinish, double finishX,
			double finishY, double widthX, double widthY,
			Stack<Point> firstStack) {
		Stack<Point> generatedStack = firstStack;
		Stack<Point> backpedalStack = new Stack<Point>();
		double currentX = 0;// widthX*30.48;
		double currentY = /* 0; */widthY * 30.48;
		double displacementX = finishX * 30.48 - currentX;
		double displacementY = finishY * 30.48 - currentY;
		if (!goToFinish) {
			int i;
			if (currentX < (widthX * 30.48) / 2) {
				i = 0;
			} else {
				i = 1;
			}
			while (widthY >= 0) {
				System.out.println("1");
				if (i % 2 == 0) {
					System.out.println("2");
					for (int j = (int) widthX; j >= 0; j--) {
						System.out.println("3");
						generatedStack.push(new Point((j) * 30.48, Math
								.abs(currentY - widthY * 30.48), false));
					}
				} else {
					System.out.println("4");
					for (int j = 0; j <= (int) widthX; j++) {
						System.out.println("5");
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
				System.out.println("6");
				if (i % 2 == 0) {
					System.out.println("7");
					if (finishX != (displacementX + currentX)) {
						System.out.println("8");
						currentX += displacementX / stepsX;
						generatedStack
								.push(new Point(currentX, currentY, false));
						backpedalStack
								.push(new Point(currentX, currentY, false));
						i++;
					}
				}
				if (i % 2 == 1) {
					if (finishY != displacementY + currentY) {
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
		navi.travelTo(0, 0, 15, this.currentStack);
		this.currentStack = new Stack<Point>();
		double widthX = 2;
		double widthY = 10;
		this.finishX = 1;
		this.finishY = 5;
		
		this.currentStack = generatePath(false, finishX, finishY, widthX, widthY,
				this.currentStack);
		this.currentStack = generatePath(true, finishX, finishY, widthX, widthY, this.currentStack);
		int i = 0, f = 0;
		while (!currentStack.isEmpty()) {
			// if (!navi.hasBlock()) {
			LCD.drawInt((int) (10 * currentStack.peek().x), 0, 6);
			LCD.drawInt((int) (10 * currentStack.peek().y), 6, 6);
			currentStack = navi.travelTo(currentStack.peek().x, currentStack.peek().y, 15,
					currentStack);
			// currentStack.peek().setVisited();
			backPedalStack.push(currentStack.pop());
			LCD.drawString("Popping", 5, 0);
			i++;
			f = i;
			// }
		}
	}

	public void setFinal(double finishX, double finishY) {
		this.finishX = finishX * 30.46;
		this.finishY = finishY * 30.46;
	}
}
