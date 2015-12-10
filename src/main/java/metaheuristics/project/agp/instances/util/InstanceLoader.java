package metaheuristics.project.agp.instances.util;

import metaheuristics.project.agp.instances.GalleryInstance;

/**
 * Classes implementing this interface are used for 
 * loading content from files and creating 
 * {@link GalleryInstance} objects.
 * Format of a file is specified by concrete implementation. 
 * @author jelena
 *
 */
public interface InstanceLoader {

	/**
	 * Method loads {@link GalleryInstance} object
	 * from file.
	 * @param filename file containing gallery data.
	 * @return constructed {@link GalleryInstance} object.
	 */
	public GalleryInstance load(String filename);
}
