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
}