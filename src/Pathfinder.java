//This class will contains the algorithm that will be used to navigate through the field of obstacles.

import java.util.Stack;

import lejos.nxt.*;

public class Pathfinder {
	Odometer odo;
	TwoWheeledRobot patbot;
	ObjectDetection detect;
	double finishX, finishY;

	public Pathfinder(Odometer odo, TwoWheeledRobot patbot,
			ObjectDetection detect, double finishX, double finishY) {
		this.odo = odo;
		this.patbot = patbot;
		this.detect = detect;
		this.finishX = finishX;
		this.finishY = finishY;
		Navigation navi = new Navigation(odo);
	}

	public Stack<Point> generatePath(boolean goToFinish, double finishX,
			double finishY, double widthX, double widthY) {
		double currentX, currentY;
		double stepX, stepY;
		int stepsReq;
		Stack<Point> courseStack = new Stack<Point>();
		if (goToFinish) {
			currentX = odo.getX();
			currentY = odo.getY();
			stepsReq = (int) Math.floor((finishX - currentX) / 20.0);
			stepX = (finishX - currentX) / stepsReq;
			stepY = (finishY - currentY) / stepsReq;
			for (int i = 0; i < 2 * stepsReq; i++) {
				if (i % 2 == 0) {
					courseStack.push(new Point(finishX - (i / 2) * stepX,
							finishY, false));
				} else {
					courseStack.push(new Point(finishX, finishY - (i / 2)
							* stepY, false));
				}
			}

		} else {

		}
		return new Stack<Point>();
	}

	public void runCourse(double widthX, double widthY) {
		Stack<Point> course = generatePath(false, finishX, finishY, widthX,
				widthY);
	}

	class Point {
		double x;
		double y;
		boolean visited;

		public Point(double x, double y, boolean visited) {
			this.x = x;
			this.y = y;
			this.visited = visited;
		}
	}
}
