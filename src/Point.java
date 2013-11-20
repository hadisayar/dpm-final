public class Point {
	double x;
	double y;
	boolean visited;

	public Point(double x, double y, boolean visited) {
		this.x = x;
		this.y = y;
		this.visited = visited;
	}

	public void setVisited() {
		this.visited = true;
	}
	
	public String pointToString() {
		return "[" + this.x + ", " + this.y + ", " + this.visited + "]";
	}
	public static boolean areSamePoints(Point A, Point B){
		return (Double.compare(A.x, B.x)==0) && (Double.compare(A.y, B.y) == 0) && (A.visited == B.visited);
	}
}