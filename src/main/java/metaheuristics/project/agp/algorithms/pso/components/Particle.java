package metaheuristics.project.agp.algorithms.pso.components;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import com.vividsolutions.jts.algorithm.LineIntersector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Camera;

public class Particle {

	private static double EPSILON = 0.0005;

	private static int maxDivisor = 5;

	private static GeometryFactory gf = new GeometryFactory();

	/**
	 * 
	 */
	private static double c1 = 2;

	private static double c2 = 2;

	/**
	 * 
	 */
	private Random rand = new Random();

	/**
	 * Camera positions
	 */
	private Camera cam;

	private Camera pBestCam;

	/**
	 * Polygon instances
	 */
	private GalleryInstance gi;

	private Polygon triangle;

	/**
	 * Evaluation values.
	 */
	private double currValue;

	private double pBestValue;

	/**
	 * Current speed.
	 */
	private double[] speed;

	/**
	 * 
	 */
	private double[][] speedBounds;

	public Particle(GalleryInstance gi, Polygon triangle) {
		this.gi = gi;
		this.triangle = triangle;
		currValue = -1;
		this.speed = new double[2];
		this.speedBounds = new double[2][2];
		generateSpeedBounds();
		generateRandomPointAndSpeedInTriangle();
		this.pBestCam = cam;
	}

	private Particle(GalleryInstance gi, Polygon triangle, Camera cam,
			Camera pBest, double currValue, double pBestValue) {
		this.cam = new Camera(cam.x, cam.y);
		this.pBestCam = new Camera(pBest.x, pBest.y);
		this.gi = gi;
		this.triangle = triangle;
		this.currValue = currValue;
		this.pBestValue = pBestValue;

	}

	/**
	 * 
	 */
	private void generateSpeedBounds() {
		Coordinate[] cor = triangle.getCoordinates();
		double x = Math.max(
				Math.max(Math.abs(cor[0].x - cor[1].x),
						Math.abs(cor[0].x - cor[2].x)),
				Math.abs(cor[2].x - cor[1].x));
		double y = Math.max(
				Math.max(Math.abs(cor[0].y - cor[1].y),
						Math.abs(cor[0].y - cor[2].y)),
				Math.abs(cor[2].y - cor[1].y));
		speedBounds[0][0] = x / maxDivisor;
		speedBounds[0][1] = -x / maxDivisor;
		speedBounds[1][0] = y / maxDivisor;
		speedBounds[1][1] = -y / maxDivisor;

		// System.out.println("TRIANGLE:( " + cor[0].x + " " + cor[0].y + ")" +
		// "( " + cor[1].x + " " + cor[1].y + ")" + "( " + cor[2].x + " " +
		// cor[2].y + ")");
		// System.out.println("x: " + speedBounds[0][0] + " y:" +
		// speedBounds[1][0]);
		// System.out.println("------------------------------------------CHECKOUT
		// SPEED BOUNDS---------------------------------------------");
	}

	/**
	 * Claculate vector lenght.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private double vecLength(Coordinate a, Coordinate b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

	/**
	 * Generate random point within triangle.
	 * 
	 * @param triangle2
	 */
	private void generateRandomPointAndSpeedInTriangle() {
		Coordinate[] verts = triangle.getCoordinates();
		/**
		 * P(x) = (1 - sqrt(r1)) * A(x) + (sqrt(r1) * (1 - r2)) * B(x) +
		 * (sqrt(r1) * r2) * C(x) P(y) = (1 - sqrt(r1)) * A(y) + (sqrt(r1) * (1
		 * - r2)) * B(y) + (sqrt(r1) * r2) * C(y)
		 */
		double r1 = rand.nextDouble();
		double r2 = rand.nextDouble();
		double x = (1 - Math.sqrt(r1)) * verts[0].x
				+ (Math.sqrt(r1) * (1 - r2)) * verts[1].x
				+ (Math.sqrt(r1) * r2) * verts[2].x;
		double y = (1 - Math.sqrt(r1)) * verts[0].y
				+ (Math.sqrt(r1) * (1 - r2)) * verts[1].y
				+ (Math.sqrt(r1) * r2) * verts[2].y;

		// Point point = gf.createPoint(new Coordinate(x, y));
		// System.out.println(point.within(triangle));
		// System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");

		speed[0] = rand.nextDouble() * (speedBounds[0][0] - speedBounds[0][1])
				- speedBounds[0][1];
		speed[1] = rand.nextDouble() * (speedBounds[1][0] - speedBounds[1][1])
				- speedBounds[1][1];
		checkSpeedBounds();

		this.cam = new Camera(x, y);
	}

	private void checkSpeedBounds() {
		if (speed[0] > speedBounds[0][0]) {
			speed[0] = speedBounds[0][0];
		} else if (speed[0] < speedBounds[0][1]) {
			speed[0] = speedBounds[0][1];
		}

		if (speed[1] > speedBounds[1][0]) {
			speed[1] = speedBounds[1][0];
		} else if (speed[1] < speedBounds[1][1]) {
			speed[1] = speedBounds[1][1];
		}
	}

	/**
	 * Evaluate particle in gallery.
	 */
	public void evaluate() {
		double temp = -1;
		temp = cam.visibilityPolygon(gi).calculateArea();
		if (temp > pBestValue) {
			pBestValue = temp;
			pBestCam = new Camera(cam.x, cam.y);
		}
		currValue = temp;
	}

	public void updateSpeed(Camera gBest) {
		double xCord = speed[0] + c1 * rand.nextDouble() * (pBestCam.x - cam.x)
				+ c2 * rand.nextDouble() * (gBest.x - cam.x);
		double yCord = speed[1] + c1 * rand.nextDouble() * (pBestCam.y - cam.y)
				+ c2 * rand.nextDouble() * (gBest.y - cam.y);
		speed[0] = xCord;
		speed[1] = yCord;
		checkSpeedBounds();
		// System.out.println("Speed update: " + xCord + ", " + yCord);
		// System.out.println("Personal best camera: " +pBestCam.x + " " +
		// pBestCam.y );
		// System.out.println("Global best camera: " +gBest.x + " " + gBest.y );
		// System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
	}

	/**
	 * 
	 */
	@Override
	public Particle clone() {
		return new Particle(gi, triangle, cam, pBestCam, currValue, pBestValue);
	}

	public void updatePosition() {
		Point p = gf.createPoint(new Coordinate(speed[0] + cam.x, speed[1] + cam.y));
		if(!p.within(triangle)){
			return;
		}
		Coordinate[] verts = triangle.getCoordinates();
		LineSegment ls1 = new LineSegment(verts[0], verts[1]);
		LineSegment ls2 = new LineSegment(verts[1], verts[2]);
		LineSegment ls3 = new LineSegment(verts[0], verts[2]);
		LineSegment speedVec = new LineSegment(
				new Coordinate(speed[0] + cam.x, speed[1] + cam.y), cam);

		Coordinate c;
		if ((c = ls1.intersection(speedVec)) != null) {

			cam = new Camera( c);
//			System.out.println("Bum u zid!");
//			Point point = gf.createPoint(cam);
//			System.out.println("1......  " + cam.x + ",  " + cam.y);
//			System.out.println(point.within(triangle));
//			System.out.println(
//					"-----------------------------------------------------------------------------------------------------------------------------------");
			
			Point point = gf.createPoint(cam);
			if(!point.within(triangle)){
				System.out.println("Vani je.........");
			}
		} else if ((c = ls2.intersection(speedVec)) != null) {
			cam = new Camera( c);
//			System.out.println("Bum u zid!");
//			Point point = gf.createPoint(cam);
//			System.out.println(point.within(triangle));
//			System.out.println("2...... " + cam.x + ",  " + cam.y);
//			System.out.println("2......c " + c.x + ",  " + c.y);
//			System.out.println(
//					"-----------------------------------------------------------------------------------------------------------------------------------");
			
			Point point = gf.createPoint(c);
			if(!point.within(triangle)){
				System.out.println("Vani je.........");
			}
		} else if ((c = ls3.intersection(speedVec)) != null) {
			cam = new Camera(c);	
//			System.out.println("Bum u zid!");
//			System.out.println("" + cam.x + ",  " + cam.y);
				Point point = gf.createPoint(c);
				if(!point.within(triangle)){
					System.out.println("Vani je.........");
				}
//			System.out.println(
//					"-----------------------------------------------------------------------------------------------------------------------------------");
		} else {
			// System.out.println("SPEED: " + speed[0] + " , " + speed[1]);
			cam.x = speed[0] + cam.x;
			cam.y = speed[1] + cam.y;
//			System.out.println("4......  " + cam.x + ",  " + cam.y);
//			Point point = gf.createPoint(cam);
//			System.out.println(point.within(triangle));
//			System.out.println(
//					"-----------------------------------------------------------------------------------------------------------------------------------");
		}
		// Point point = gf.createPoint(cam);
		// System.out.println(point.within(triangle));
	}

	/**
	 * @return the cam
	 */
	public Camera getCam() {
		return cam;
	}

	/**
	 * @param cam
	 *            the cam to set
	 */
	public void setCam(Camera cam) {
		this.cam = cam;
	}

	/**
	 * @return the pBestCam
	 */
	public Camera getpBestCam() {
		return pBestCam;
	}

	/**
	 * @param pBestCam
	 *            the pBestCam to set
	 */
	public void setpBestCam(Camera pBestCam) {
		this.pBestCam = pBestCam;
	}

	/**
	 * @return the gi
	 */
	public GalleryInstance getGi() {
		return gi;
	}

	/**
	 * @param gi
	 *            the gi to set
	 */
	public void setGi(GalleryInstance gi) {
		this.gi = gi;
	}

	/**
	 * @return the triangle
	 */
	public Polygon getTriangle() {
		return triangle;
	}

	/**
	 * @param triangle
	 *            the triangle to set
	 */
	public void setTriangle(Polygon triangle) {
		this.triangle = triangle;
	}

	/**
	 * @return the currValue
	 */
	public double getCurrValue() {
		return currValue;
	}

	/**
	 * @param currValue
	 *            the currValue to set
	 */
	public void setCurrValue(double currValue) {
		this.currValue = currValue;
	}

	/**
	 * @return the pBestValue
	 */
	public double getpBestValue() {
		return pBestValue;
	}

	/**
	 * @param pBestValue
	 *            the pBestValue to set
	 */
	public void setpBestValue(double pBestValue) {
		this.pBestValue = pBestValue;
	}

}
