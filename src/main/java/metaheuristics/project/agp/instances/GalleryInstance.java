package metaheuristics.project.agp.instances;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;


import metaheuristics.project.agp.alg.Algorithm;
import metaheuristics.project.agp.instances.components.Camera;
import metaheuristics.project.agp.instances.components.Polygon;

/**
 * Instances of this class represent an instance of art gallery
 * to calculate number of cameras for.
 * Extends class {@link Polygon}.
 * @author jelena
 *
 */
public class GalleryInstance extends Polygon{

	public static final int DEFAULT_SIZE = 20;
	/**
	 * List of holes instance contains.
	 */
	private List<Polygon> holes;
	
	/**
	 * List of cameras( positions where they should be put) to cover 
	 * the gallery.
	 */
	public Set<Camera> cameras;
	
	/**
	 * Class constructor.
	 * Creates gallery instance with boundary consisting of given vertices,
	 * in given order; It contains no holes.
	 * @param vertices vertices creating boundary of the gallery.
	 */
	public GalleryInstance(List<Coordinate> vertices) {
		super(vertices);
		this.holes = new ArrayList<Polygon>(DEFAULT_SIZE);
		this.cameras = new HashSet<Camera>(vertices.size() / 3);
	}
	
	/**
	 * Class constructor.
	 * Creates gallery instance with boundaries consisting of given
	 * vertices, in given order. 
	 * Gallery contains holes passed as the second argument.
	 * @param vertices vertices creating boundary of the gallery
	 * @param holes list of holes
	 */
	public GalleryInstance(List<Coordinate> vertices, List<Polygon> holes) {
		super(vertices);
		this.holes = holes;
		this.cameras = new HashSet<Camera>(
				(vertices.size() + holes.size()) / 3
				);
	}
	
	/**
	 * Method determines position of cameras needed to guide the gallery.
	 * @param algorithm algorithm used to determine positions.
	 */
	public void setCameras(Algorithm alg) {
		alg.process(this);
	}
	
	/**
	 * Method adding new hole.
	 * @param h hole to be added.
	 */
	public void addHole(Polygon h) {
		holes.add(h);
	}
	
	/**
	 * Method returns list of holes for this gallery.
	 * If no holes exist, it returns empty {@link List}.
	 * @return list of holes.
	 */
	public List<Polygon> getHoles() {
		return holes;
	}
	
	/**
	 * Method returns i-th hole in a list.
	 * Holes are stored in the same order as in benchmark.
	 * @param i index of a hole.
	 * @return hole stored on the given index.
	 */
	public Polygon getHoleOnIndex(int i) {
		return holes.get(i);
	}
	
	/**
	 * Method returns cameras needed to guide gallery.
	 * If values are not known yet, <code>null</code> is
	 * returned.
	 * @return list of cameras.
	 */
	public Set<Camera> getCameras() {
			return this.cameras;
	}
	
	public void addCamera(Camera c) {
		cameras.add(c);
	}
	
	public int cameraNum() {
		return cameras.size();
	}
	
	/**
	 * Method calculating area of holes.
	 * @return area of holes.
	 */
	private double calculateHolesArea() {
		double area = 0.0;
		for(Polygon h : this.holes) {
			area += h.calculateArea();
		}
		return area;
	}
	
	public int saveResults(String fname) {
		File file = new File(fname);
		StringBuilder sb = new StringBuilder();
		for(Camera c : getCameras()) {
			sb.append(c.toString() + " ");
		}
		try {
			FileWriter fw = new FileWriter(file, false);
			fw.write(sb.toString());
			fw.flush();
			fw.close();
		} catch (IOException ignorable) {}
		return cameraNum();
	}
	
	@Override
	public double calculateArea() {
		return super.calculateArea() - calculateHolesArea();
	}
}
