package metaheuristics.project.agp.algorithms.pso.components;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import metaheuristics.project.agp.alg.Algorithm;
import metaheuristics.project.agp.instances.GalleryInstance;

public class TriangleOptimization
		implements Algorithm, Comparable<TriangleOptimization> {

	private static GeometryFactory gf = new GeometryFactory();

	private GalleryInstance gi;

	private Polygon triangle;

	private List<Particle> particles;

	private Particle gBest;

	private double maxAreaCovered = -1;

	private int populationSize;

	private int numOfIteration;

	public Polygon visiblePolygon;

	public TriangleOptimization(GalleryInstance gi, int populationSize,
			Polygon triangle, int numOfIteration) {
		this.gi = gi;
		this.populationSize = populationSize;
		this.triangle = triangle;
		this.numOfIteration = numOfIteration;
		this.particles = new ArrayList<>();

		for (int j = 0; j < populationSize; j++) {
			Particle par = new Particle(gi, triangle);
			particles.add(par);
		}
		
		this.gBest = particles.get(0).clone();

	}

	private void createVisiblePolygon() {
		List<Coordinate> bound = gBest.getpBestCam().visibilityPolygon(gi).getVertices();
		visiblePolygon = createPolygon(bound);
	}
	
	Polygon createPolygon(List<Coordinate> bound) {
		Coordinate[] boundary = new Coordinate[bound.size() + 1];
		for (int i = 0; i < boundary.length - 1; ++i)
			boundary[i] = bound.get(i);
		boundary[boundary.length - 1] = bound.get(0);
		return gf.createPolygon(boundary);
	}

	@Override
	public void process(GalleryInstance gi) {
		int iter = numOfIteration;
		do {
			for (Particle par : particles) {
				par.evaluate();
				if (par.getCurrValue() > maxAreaCovered) {
					maxAreaCovered = par.getCurrValue();
					gBest = par.clone();
				}
				par.updateSpeed(gBest.getCam());
				par.updatePosition();
			}

		} while (--iter > 0);
		createVisiblePolygon();
	}

	@Override
	public int compareTo(TriangleOptimization o) {
		return Double.valueOf(maxAreaCovered).compareTo(o.maxAreaCovered);
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
	 * @return the particles
	 */
	public List<Particle> getParticles() {
		return particles;
	}

	/**
	 * @param particles
	 *            the particles to set
	 */
	public void setParticles(List<Particle> particles) {
		this.particles = particles;
	}

	/**
	 * @return the gBest
	 */
	public Particle getBest() {
		return gBest;
	}

	/**
	 * @param gBest
	 *            the gBest to set
	 */
	public void setgBest(Particle gBest) {
		this.gBest = gBest;
	}

	/**
	 * @return the bestValue
	 */
	public double getBestValue() {
		return maxAreaCovered;
	}

	/**
	 * @param bestValue
	 *            the bestValue to set
	 */
	public void setBestValue(double bestValue) {
		this.maxAreaCovered = bestValue;
	}

	/**
	 * @return the populationSize
	 */
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * @param populationSize
	 *            the populationSize to set
	 */
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	/**
	 * @return the numOfIteration
	 */
	public int getNumOfIteration() {
		return numOfIteration;
	}

	/**
	 * @param numOfIteration
	 *            the numOfIteration to set
	 */
	public void setNumOfIteration(int numOfIteration) {
		this.numOfIteration = numOfIteration;
	}

	/**
	 * @return the visiblePolygon
	 */
	public Polygon getVisiblePolygon() {
		return visiblePolygon;
	}

	/**
	 * @param visiblePolygon
	 *            the visiblePolygon to set
	 */
	public void setVisiblePolygon(Polygon visiblePolygon) {
		this.visiblePolygon = visiblePolygon;
	}

}
