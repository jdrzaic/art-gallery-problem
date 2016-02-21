package metaheuristics.project.agp.instances.components;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

/**
 * Class representing simple polygon specified by its boundary.
 * @author jelena
 *
 */
public class Polygon {
	
	/**
	 * Default number of vertices to be initialized.
	 */
	public static final int DEFAULT_SIZE = 64;
	
	/**
	 * List of vertices to create border of the polygon.
	 */
	private List<Coordinate> vertices;
	
	/**
	 * Default class constructor.
	 */
	public Polygon() {
		this(new ArrayList<Coordinate>(DEFAULT_SIZE));
	}
	
	/**
	 * Class constructor.
	 * Creates polygon with boundary consisting of
	 * a given list of vertices, in a given order.
	 * @param vertices list of vertices
	 */
	public Polygon(List<Coordinate> vertices) {
		this.vertices = vertices;
	}
	
	/**
	 * Method returns vertex on passed index.
	 * @param i index.
	 * @return vertex on passed index.
	 * @throws IllegalArgumentException if index is invalid.
	 */
	public Coordinate getOnIndex(int i) {
		if(i < 0 || i > vertices.size() - 1) {
			throw new IllegalArgumentException("Invalid vertex index.");
		}
		return vertices.get(i);
	}
	
	/**
	 * Method returning list of vertices.
	 * @return
	 */
	public List<Coordinate> getVertices() {
		return this.vertices;
	}
	
	/**
	 * Method adds vertex to the end of vertex list.
	 * @param v vertex to be added.
	 */
	public void addVertex(Coordinate v) {
		vertices.add(v);
	}
	
	/**
	 * Method used for calculating area of a polygon.
	 * @return area of a polygon.
	 */
	public double calculateArea() {
		int n = this.vertices.size();
		Coordinate[] cords = new Coordinate[n + 1];
		for(int i = 0; i < n; ++i) {
			cords[i] = new Coordinate(vertices.get(i).x, vertices.get(i).y);
		}
		cords[n] = new Coordinate(vertices.get(0).x, vertices.get(0).y);
		CoordinateArraySequence cas = new CoordinateArraySequence(cords);
		LinearRing lr = new LinearRing(cas, new GeometryFactory());
		com.vividsolutions.jts.geom.Polygon p = new 
				com.vividsolutions.jts.geom.Polygon(lr, null, new GeometryFactory());
		return p.getArea();
	}
}
