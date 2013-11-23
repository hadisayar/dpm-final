public class Point {
	double x;
	double y;
	boolean visited;

	
	public Point(){
		this.x = 0;
		this.y = 0;
		this.visited = false;
	}
	
	public Point(double x, double y, boolean visited) {
		this.x = x;
		this.y = y;
		this.visited = visited;
	}

	public void setVisited() {
		this.visited = true;
	}

	public boolean outOfBounds(double widthX, double widthY){
		return this.x < 0 || this.x > widthX || this.y < 0 || this.y > widthY;
	}
	
	public String pointToString() {
		return "[" + this.x + ", " + this.y + ", " + this.visited + "]";
	}

	public String pointToCoords(double stepCase) {
		return "[" + this.x / stepCase + ", " + this.y / stepCase + ", "
				+ this.visited + "]";
	}

	public String pointToTV() {
		return "" + this.x + "     " + this.y;
	}

	public static boolean areSamePoints(Point A, Point B) {
		return (Double.compare(A.x, B.x) == 0)
				&& (Double.compare(A.y, B.y) == 0) && (A.visited == B.visited);
	}

	public static Point leftAdjacentPoint(Point A) {
		return new Point(A.x - 1, A.y, false);
	}

	public static Point rightAdjacentPoint(Point A) {
		return new Point(A.x + 1, A.y, false);
	}

	public static Point upAdjacentPoint(Point A) {
		return new Point(A.x, A.y + 1, false);
	}

	public static Point downAdjacentPoint(Point A) {
		return new Point(A.x, A.y - 1, false);
	}
}