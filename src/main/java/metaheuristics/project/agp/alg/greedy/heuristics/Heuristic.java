package metaheuristics.project.agp.alg.greedy.heuristics;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public abstract class Heuristic {

	/**
	 * Method returns util value for polygon passed.
	 * @param polygon polygon to calculate util value for
	 * @param cover
	 * @param gf
	 * @return
	 */
	public abstract double utilValue(Geometry polygon, Geometry cover,
			GeometryFactory gf);
}
