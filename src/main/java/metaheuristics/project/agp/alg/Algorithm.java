package metaheuristics.project.agp.alg;

import metaheuristics.project.agp.instances.GalleryInstance;

/**
 * Concrete implementation of this interface represent 
 * algorithms for solving Art Gallery Problem.
 * @author jelenadrzaic
 *
 */
public interface Algorithm {

	/**
	 * Main method.
	 * Finds best cameras on gallery instance passed.
	 * @param gi Gallery Instance to cover.
	 */
	void process(GalleryInstance gi);
}
