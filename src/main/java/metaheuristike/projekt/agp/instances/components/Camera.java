package metaheuristike.projekt.agp.instances.components;

import metaheuristike.projekt.agp.instances.GalleryInstance;

/**
 * Class represents one camera.
 * Instances of this class are used for covering 
 * {@link GalleryInstance}.
 * @author jelena
 *
 */
public class Camera extends Vertex{

	/**
	 * Class constructor.
	 * @param x x coordinate value.
	 * @param y x coordinate value.
	 */
	public Camera(double x, double y) {
		super(x, y);
	}
	
	/**
	 * Method determining visibility polygon of a camera in
	 * a given {@link GalleryInstance}.
	 * @param gi {@link GalleryInstance} camera is in.
	 * @return visibility polygon of a camera.
	 */
	public Polygon visibilityPolygon(GalleryInstance gi) {
		//todo CGAL
		return null;
	}
}
