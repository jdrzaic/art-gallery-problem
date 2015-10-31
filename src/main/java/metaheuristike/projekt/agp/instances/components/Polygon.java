package metaheuristike.projekt.agp.instances.components;

import java.util.ArrayList;
import java.util.List;

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
	private List<Vertex> vertices;
	
	/**
	 * Default class constructor.
	 */
	public Polygon() {
		this(new ArrayList<Vertex>(DEFAULT_SIZE));
	}
	
	/**
	 * Class constructor.
	 * Creates polygon with boundary consisting of
	 * a given list of vertices, in a given order.
	 * @param vertices list of vertices
	 */
	public Polygon(List<Vertex> vertices) {
		this.vertices = vertices;
	}
	
	/**
	 * Method returns vertex on passed index.
	 * @param i index.
	 * @return vertex on passed index.
	 * @throws IllegalArgumentException if index is invalid.
	 */
	public Vertex getOnIndex(int i) {
		if(i < 0 || i > vertices.size() - 1) {
			throw new IllegalArgumentException("Invalid vertex index.");
		}
		return vertices.get(i);
	}
	
	/**
	 * Method returning list of vertices.
	 * @return
	 */
	public List<Vertex> getVertices() {
		return this.vertices;
	}
	
	/**
	 * Method adds vertex to the end of vertex list.
	 * @param v vertex to be added.
	 */
	public void addVertex(Vertex v) {
		vertices.add(v);
	}
	
	/**
	 * Method used for calculating area of a polygon.
	 * @return area of a polygon.
	 */
	public double calculateArea() {
		//todo CGAL
		return 0;
	}
}
