package metaheuristics.project.agp.algorithms.pso.components;

import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import metaheuristics.project.agp.instances.GalleryInstance;
import metaheuristics.project.agp.instances.components.Camera;

/**
 * Class represents a particle in PSO algorithm.
 * @author gbbanusic
 *
 */
public class Particle {

	/**
	 * Divisor for speed bounds.
	 */
	private static int sideDivisor = 5;

	/**
	 * Geometry factory.
	 */
	private static GeometryFactory gf = new GeometryFactory();

	/**
	 * Individuality factor.
	 */
	public static double c1 = 2;

	/**
	 * Social factor.
	 */
	public static double c2 = 2;

	/**
	 * Random number generator.
	 */
	private Random rand = new Random();

	/**
	 * Camera positions
	 */
	private Camera camera;

	/**
	 * Personal best camera.
	 */
	private Camera pBestCam;

	/**
	 * Polygon instances
	 */
	private GalleryInstance gi;

	/**
	 * Triangle with camera inside.
	 */
	private Polygon triangle;

	/**
	 * Evaluation values.
	 */
	private double currValue;

	/**
	 * Personal best value.
	 */
	private double pBestValue;

	/**
	 * Current speed.
	 */
	private double[] speed;

	/**
	 * Speed bounds, calculated by maximal side in triangle.
	 */
	private double[][] speedBounds;

	/**
	 * Constructor creating a particle with given gallery instance and triangle for camera position bounds.
	 * @param gi is the gallery instance.
	 * @param triangle
	 */
	public Particle(GalleryInstance gi, Polygon triangle) {
		this.gi = gi;
		this.triangle = triangle;
		currValue = -1;
		this.speed = new double[2];
		this.speedBounds = new double[2][2];
		generateSpeedBounds();
		generateRandomPointAndSpeedInTriangle();
		this.pBestCam = camera;
	}

	/**
	 * Constructor for cloning solutions..
	 * @param gi is the gallery instance.
	 * @param triangle
	 * @param cam is the current camera.
	 * @param pBest is the personal best camera.
	 * @param currValue is the current value.
	 * @param pBestValue is the personal best value.
	 */
	private Particle(GalleryInstance gi, Polygon triangle, Camera cam,	Camera pBest, double currValue, double pBestValue) {
		this.camera = new Camera(cam.x, cam.y);
		this.pBestCam = new Camera(pBest.x, pBest.y);
		this.gi = gi;
		this.triangle = triangle;
		this.currValue = currValue;
		this.pBestValue = pBestValue;
	}

	/**
	 * Method for initializing speed bounds.
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
		speedBounds[0][0] = x / sideDivisor;
		speedBounds[0][1] = -x / sideDivisor;
		speedBounds[1][0] = y / sideDivisor;
		speedBounds[1][1] = -y / sideDivisor;
	}

	/**
	 * Generate random point within triangle with random speed between speed bounds.
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
		speed[0] = rand.nextDouble() * (speedBounds[0][0] - speedBounds[0][1])
				- speedBounds[0][1];
		speed[1] = rand.nextDouble() * (speedBounds[1][0] - speedBounds[1][1])
				- speedBounds[1][1];
		checkSpeedBounds();

		this.camera = new Camera(x, y);
	}

	/**
	 * Method checks whether speed is between its bounds.
	 */
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
		temp = camera.visibilityPolygon(gi).calculateArea();
		if (temp > pBestValue) {
			pBestValue = temp;
			pBestCam = new Camera(camera.x, camera.y);
		}
		currValue = temp;
	}

	/**
	 * Update particle speed depending on global best solution.
	 * @param gBest is the global best solution.
	 */
	public void updateSpeed(Camera gBest) {
		double xCord = speed[0] + c1 * rand.nextDouble() * (pBestCam.x - camera.x)
				+ c2 * rand.nextDouble() * (gBest.x - camera.x);
		double yCord = speed[1] + c1 * rand.nextDouble() * (pBestCam.y - camera.y)
				+ c2 * rand.nextDouble() * (gBest.y - camera.y);
		speed[0] = xCord;
		speed[1] = yCord;
		checkSpeedBounds();
	}

	@Override
	public Particle clone() {
		return new Particle(gi, triangle, camera, pBestCam, currValue, pBestValue);
	}

	/**
	 * Method updates particle position.
	 */
	public void updatePosition() {
		Point p = gf.createPoint(new Coordinate(speed[0] + camera.x, speed[1] + camera.y));
		if(!p.within(triangle)){
			return;
		}
		Coordinate[] verts = triangle.getCoordinates();
		LineSegment ls1 = new LineSegment(verts[0], verts[1]);
		LineSegment ls2 = new LineSegment(verts[1], verts[2]);
		LineSegment ls3 = new LineSegment(verts[0], verts[2]);
		LineSegment speedVec = new LineSegment(
				new Coordinate(speed[0] + camera.x, speed[1] + camera.y), camera);

		Coordinate c;
		if ((c = ls1.intersection(speedVec)) != null) {

			camera = new Camera( c);
			
			Point point = gf.createPoint(camera);
			if(!point.within(triangle)){
				System.out.println("Vani je.........");
			}
		} else if ((c = ls2.intersection(speedVec)) != null) {
			camera = new Camera( c);
			
			Point point = gf.createPoint(c);
			if(!point.within(triangle)){
				System.out.println("Vani je.........");
			}
		} else if ((c = ls3.intersection(speedVec)) != null) {
			camera = new Camera(c);	
				Point point = gf.createPoint(c);
				if(!point.within(triangle)){
					System.out.println("Vani je.........");
				}
		} else {
			camera.x = speed[0] + camera.x;
			camera.y = speed[1] + camera.y;
		}
	}

	/**
	 * @return the camera
	 */
	public Camera getCam() {
		return camera;
	}

	/**
	 * @param camera
	 *            the camera to set
	 */
	public void setCam(Camera cam) {
		this.camera = cam;
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
