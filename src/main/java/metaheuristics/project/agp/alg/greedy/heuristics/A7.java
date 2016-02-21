package metaheuristics.project.agp.alg.greedy.heuristics;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * One of the heuristics used by GreedyAlgorithm.
 * @author jelenadrzaic
 *
 */
public class A7 extends Heuristic{
  
	@Override
	public double utilValue(Geometry polygon, Geometry cover, GeometryFactory gf) {

		Geometry rest;
		try {
			rest = polygon.difference(cover);
		}catch(Exception e) {
			return 0;
		}
		return rest.getArea();
	}

}
